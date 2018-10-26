package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScorerDatasetVisitor implements DatasetVisitor {

    private InMemoryVocabularyImpl vocabulary;

    private int batchSize;
    private int currentIndexInBatch;
    private InvertedFileSerializer serializer;
    private final List<HeaderAndInvertedFile> partitions = new ArrayList<>();

    private Map<String, Double> numberOfArticlesContainingTerm;
    private Map<String, Double> termsFrequencyInCurrentArticle;
    private Map<String, Double> idf;
    private int numberOfArticles;

    public ScorerDatasetVisitor(InvertedFileSerializer serializer, int batchSize) {

        this.serializer = serializer;
        this.batchSize = batchSize;


        this.numberOfArticles = 0;
        this.numberOfArticlesContainingTerm = new HashMap<>();
        this.termsFrequencyInCurrentArticle = new HashMap<>();
        this.idf = new HashMap<>();
    }

    public int getTotalPassNumber() {
        return 2;
    }

    @Override
    public void onArticleParseStart(int currentPassNumber) {

        if (currentPassNumber == 1) {
            numberOfArticles++;
            termsFrequencyInCurrentArticle.clear();
        }
    }

    @Override
    public void onTermRead(String term, int currentPassNumber) {
        if (currentPassNumber == 1) {
            if (!termsFrequencyInCurrentArticle.containsKey(term)) {
                if (numberOfArticlesContainingTerm.containsKey(term)) {
                    Double currentArticlesNumberContainingTerm = numberOfArticlesContainingTerm.get(term);
                    numberOfArticlesContainingTerm.put(term, currentArticlesNumberContainingTerm + 1.0);
                } else {
                    numberOfArticlesContainingTerm.put(term, 1.0);
                }
            }
            termsFrequencyInCurrentArticle.put(term, 1.0);

        } else if (currentPassNumber == 2) {

            if (!termsFrequencyInCurrentArticle.containsKey(term)) {
                termsFrequencyInCurrentArticle.put(term, 1.0);
            } else {
                termsFrequencyInCurrentArticle.put(term, termsFrequencyInCurrentArticle.get(term) + 1.0);
            }
        }
    }

    @Override
    public void onArticleParseEnd(ArticleId articleId, int currentPassNumber) {
        if (currentPassNumber == 2) {

            for (Map.Entry<String, Double> term : termsFrequencyInCurrentArticle.entrySet()) {
                Double tf = 1 + Math.log(termsFrequencyInCurrentArticle.get(term.getKey()));
                Double score = tf * idf.get(term.getKey());
                setScore(term.getKey(), articleId, score.floatValue());
            }
            finalizeToScoreArticle();
        }
    }

    @Override
    public void onPassEnd(int currentPassNumber) {
        if (currentPassNumber == 1) {
            for (Map.Entry<String, Double> term : numberOfArticlesContainingTerm.entrySet()) {
                idf.put(term.getKey(), Math.log(numberOfArticles / (1 + term.getValue())));
            }
        }
    }

    /**
     * Set the score of a term in an article
     *
     * @param term      The term
     * @param articleId The article
     * @param score     The score
     */
    protected void setScore(String term, ArticleId articleId, float score) {
        vocabulary.putEntry(term, new Entry(articleId, score));
    }

    /**
     * At the end of article terms scoring, serialize the batch of articles if there are enough
     */
    protected void finalizeToScoreArticle() {
        currentIndexInBatch++;
        if (currentIndexInBatch == batchSize) {
            currentIndexInBatch = 0;
            serializeVocabulary();
        }
    }

    /**
     * Serialize the temporary stored vocabulary
     */
    private void serializeVocabulary() {
        Result<HeaderAndInvertedFile, Exception> result = serializer.serialize(vocabulary);
        partitions.add(result.ok().get());
        resetVocabulary();
    }

    /**
     * Reset the temporary stored vocabulary
     */
    private void resetVocabulary() {
        vocabulary = new InMemoryVocabularyImpl();
    }

    public List<HeaderAndInvertedFile> getPartitions() {
        if (vocabulary.isEmpty()) {
            finalizeToScoreArticle();
        }

        return partitions;
    }
}
