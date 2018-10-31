package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.SerializerProperties;

import java.io.File;
import java.util.*;

public class ScorerImplementation implements DatasetVisitor {

    private InMemoryVocabularyImpl batchVocabulary;
    private HashMap<String, Float> allScores;

    private int batchSize;
    private int currentIndexInBatch;
    private InvertedFileSerializer serializer;
    private final List<HeaderAndInvertedFile> partitions = new ArrayList<>();

    private Map<String, Float> numberOfArticlesContainingTerm;
    private Map<String, Float> termsFrequencyInCurrentArticle;
    private int numberOfArticles;
    private int outputsNumber = 0;

    public ScorerImplementation(InvertedFileSerializer serializer, int batchSize) {

        this.serializer = serializer;
        this.batchSize = batchSize;

        this.batchVocabulary = new InMemoryVocabularyImpl();
        this.allScores = new HashMap<>();
        this.numberOfArticles = 0;
        this.numberOfArticlesContainingTerm = new HashMap<>();
        this.termsFrequencyInCurrentArticle = new HashMap<>();
        this.currentIndexInBatch = 0;

        final File tmpDir = new File("tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        } else {
            Arrays.stream(tmpDir.listFiles()).forEach(File::delete);
        }

    }

    public int getTotalPassNumber() {
        return 2;
    }

    @Override
    public void onOpeningArticle(int articleId, int currentPassNumber) {

        if (currentPassNumber == 1) {
            numberOfArticles++;
        }

        termsFrequencyInCurrentArticle.clear();
    }

    @Override
    public void onTermRead(String term, int currentPassNumber) {
        if (currentPassNumber == 1) {
            if (!termsFrequencyInCurrentArticle.containsKey(term)) {
                termsFrequencyInCurrentArticle.put(term, 1.0f);
                if (numberOfArticlesContainingTerm.containsKey(term)) {
                    numberOfArticlesContainingTerm.put(term, numberOfArticlesContainingTerm.get(term) + 1.0f);
                } else {
                    numberOfArticlesContainingTerm.put(term, 1.0f);
                }
            }

        } else if (currentPassNumber == 2) {

            if (termsFrequencyInCurrentArticle.containsKey(term)) {
                termsFrequencyInCurrentArticle.put(term, termsFrequencyInCurrentArticle.get(term) + 1.0f);
            } else {
                termsFrequencyInCurrentArticle.put(term, 1.0f);
            }
        }
    }

    @Override
    public void onClosingArticle(int articleId, int currentPassNumber) {
        if (currentPassNumber == 2) {

            for (String term : termsFrequencyInCurrentArticle.keySet()) {

                float termFrequencyInTheCurrentArticle = termsFrequencyInCurrentArticle.get(term);
                float numberOfArticlesContainingTheTerm = numberOfArticlesContainingTerm.get(term);

                double tf = 1 + Math.log(termFrequencyInTheCurrentArticle);
                double idf = Math.log(numberOfArticles / (1 + numberOfArticlesContainingTheTerm));
                float score = (float) (tf * idf);

                setScore(term, articleId, score);

            }
            finalizeToScoreArticle();
        }
    }

    @Override
    public void onEndingPass(int currentPassNumber) {
    }

    @Override
    public void onOpeningFile(File file, int currentPassNumber) {
    }

    /**
     * Set the score of a term in an article
     *
     * @param term      The term
     * @param articleId The article
     * @param score     The score
     */
    protected void setScore(String term, int articleId, float score) {
        batchVocabulary.putEntry(term, new Entry(articleId, score));
        allScores.put((articleId + "_" + term), score);
    }

    /**
     * At the end of article terms scoring, serialize the batch of articles if there are enough
     */
    protected void finalizeToScoreArticle() {
        currentIndexInBatch++;
        if (currentIndexInBatch == batchSize) {
            currentIndexInBatch = 0;
            serializeBatchVocabulary();
        }
    }

    /**
     * Serialize the temporary stored batchVocabulary
     */
    private void serializeBatchVocabulary() {
        final HeaderAndInvertedFile outputFile = new HeaderAndInvertedFile(
                new File("tmp/" + SerializerProperties.HEADER_FILE + outputsNumber),
                new File("tmp/" + SerializerProperties.INVERTED_FILE + outputsNumber)
        );
        IO<HeaderAndInvertedFile> result = serializer.serialize(batchVocabulary, outputFile);
        partitions.add(result.attempt().expect("Something did wrong during serialization"));
        resetBatchVocabulary();
        outputsNumber += 1;
    }

    /**
     * Reset the temporary stored batchVocabulary
     */
    private void resetBatchVocabulary() {
        batchVocabulary = new InMemoryVocabularyImpl();
    }

    public List<HeaderAndInvertedFile> getPartitions() {
        if (!batchVocabulary.isEmpty()) {
            serializeBatchVocabulary();
        }

        return partitions;
    }

    public HashMap<String, Float> getAllScores() {
        return allScores;
    }
}
