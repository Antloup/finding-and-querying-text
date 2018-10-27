package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.Scorer;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.DocumentParser;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractScorerImplementation implements Scorer {

    private static char[] ESCAPED = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // Digits
            '?', ',', '.', ';', '?', ':', '!', '\'', '"', '(', ')', '{', '}', '[', ']', '$', // Punctuation
            '&', // Special character
            '+', '-', '*', '%', '=' // Operators
    };
    private static String[] WHITE_SPACES = new String[]{" ", "\n", "\r\n"};
    private final List<File> dataSetFiles;

    private InMemoryVocabularyImpl vocabulary;

    protected int currentPassNumber;

    private int batchSize;
    private int currentIndexInBatch;
    private InvertedFileSerializer serializer;
    private final List<HeaderAndInvertedFile> outputs = new ArrayList<>();

    AbstractScorerImplementation(Stream<Path> dataSetFolder, InvertedFileSerializer serializer) {
        this.currentPassNumber = 1;
        this.serializer = serializer;
        vocabulary = new InMemoryVocabularyImpl();
        dataSetFiles = dataSetFolder.map(Path::toFile)
                .collect(Collectors.toList());
    }

    /**
     * Set the score of a term in an article
     *
     * @param term      The term
     * @param articleId The article
     * @param score     The score
     */
    protected void setScore(String term, int articleId, float score) {
        vocabulary.putEntry(term, new Entry(articleId, score));
    }

    public List<HeaderAndInvertedFile> evaluate(int batchSize) {
        this.currentIndexInBatch = 0;
        this.batchSize = batchSize;

        for (currentPassNumber = 1; currentPassNumber <= getTotalPassNumber(); currentPassNumber++) {
            for (File file : dataSetFiles) {
                if (file.isFile()) {
                    try {
                        DocumentParser documentParser = new DocumentParser(file, this);
                        documentParser.parse(ESCAPED, WHITE_SPACES);
                    } catch (XMLStreamException | FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            this.onPassEnd();
        }

        if (vocabulary.isEmpty()) {
            finalizeToScoreArticle();
        }

        return outputs;
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
        outputs.add(result.ok().get());
        resetVocabulary();
    }

    /**
     * Reset the temporary stored vocabulary
     */
    private void resetVocabulary() {
        vocabulary = new InMemoryVocabularyImpl();
    }
}
