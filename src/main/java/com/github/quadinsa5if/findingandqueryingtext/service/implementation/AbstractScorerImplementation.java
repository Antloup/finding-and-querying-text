package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
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

public abstract class AbstractScorerImplementation {

    private static char[] ESCAPED = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // Digits
            '?', ',', '.', ';', '?', ':', '!', '\'', '"', '(', ')', '{', '}', '[', ']', '$', // Punctuation
            '&', // Special character
            '+', '-', '*', '%', '=' // Operators
    };
    private static String[] WHITE_SPACES = new String[]{" ", "\n", "\r\n"};
    private final List<File> dataSetFiles;

    private InMemoryVocabularyImpl vocabulary;

    int currentPassNumber;

    private int batchSize;
    private int currentIndexInBatch;
    private InvertedFileSerializer serializer;
    private final List<HeaderAndInvertedFile> outputs = new ArrayList<>();

    public abstract int getTotalPassNumber();

    public abstract void onArticleParseStart();

    public abstract void onTermRead(String word);

    public abstract void onArticleParseEnd(ArticleId articleId);

    public abstract void onPassEnd();

    AbstractScorerImplementation(Stream<Path> dataSetFolder, InvertedFileSerializer serializer) {
        this.currentPassNumber = 1;
        this.serializer = serializer;
        vocabulary = new InMemoryVocabularyImpl();
        dataSetFiles = dataSetFolder.map(Path::toFile)
                .collect(Collectors.toList());
    }

    void setScore(String term, ArticleId articleId, float score) {
        vocabulary.putEntry(term, new Entry(articleId, score));
    }

    public InMemoryVocabularyImpl getVocabulary() {
        return vocabulary;
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

        if(vocabulary.isEmpty()) {
            finalizeToScoreArticle();
        }

        return outputs;
    }

    void finalizeToScoreArticle() {
        currentIndexInBatch++;
        if (currentIndexInBatch == batchSize) {
            currentIndexInBatch = 0;
            serializeVocabulary();
        }
    }

    private void serializeVocabulary() {
        Result<HeaderAndInvertedFile, Exception> result = serializer.serialize(vocabulary);
        outputs.add(result.ok().get());
        resetVocabulary();
    }

    private void resetVocabulary() {
        vocabulary = new InMemoryVocabularyImpl();
    }
}
