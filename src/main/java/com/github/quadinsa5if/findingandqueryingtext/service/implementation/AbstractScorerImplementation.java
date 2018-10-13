package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.DocumentParser;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;

public abstract class AbstractScorerImplementation {

    private static char[] ESCAPED = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // Digits
            '?', ',', '.', ';', '?', ':', '!', '\'', '"', '(', ')', '{', '}', '[', ']', '$', // Punctuation
            '&', // Special character
            '+', '-', '*', '%', '=' // Operators
    };
    private static String[] WHITE_SPACES = new String[]{" ", "\n", "\r\n"};
    private final File[] dataSetFiles;

    private InMemoryVocabularyImpl vocabulary;

    protected int currentPassNumber;

    protected int getCurrentPassNumber() {
        return this.currentPassNumber;
    }

    protected int batchSize;
    protected int currentIndexInBatch;

    public abstract int getTotalPassNumber();
    public abstract void onArticleParseStart();
    public abstract void onTermRead(String word);
    public abstract void onArticleParseEnd(ArticleId articleId);
    public abstract void onPassEnd();
    
    public AbstractScorerImplementation(File dataSetFolder) {
        this.currentPassNumber = 1;
        vocabulary = new InMemoryVocabularyImpl();
        dataSetFiles = dataSetFolder.listFiles();
    }

    protected void setScore(String term, ArticleId articleId, float score) {
        vocabulary.putEntry(term, new Entry(articleId, score));
    }
    
    public InMemoryVocabularyImpl getVocabulary() {
        return vocabulary;
    }

    public void evaluate(int batchSize) {
        this.currentIndexInBatch = 0;
        this.batchSize = batchSize;

        for (currentPassNumber = 1; currentPassNumber <= getTotalPassNumber(); currentPassNumber++) {
            for (File file : dataSetFiles) {
                if (file.isFile()) {
                    try {
                        DocumentParser documentParser = new DocumentParser(file, this);
                        documentParser.parse(vocabulary, ESCAPED, WHITE_SPACES);
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.onPassEnd();
        }
    }

    protected void finalizeToScoreArticle() {
        currentIndexInBatch++;

        if (currentIndexInBatch == batchSize) {
            currentIndexInBatch = 0;

            serializeVocabulary();
        }
    }

    private void serializeVocabulary() {
        //todo: serialization, to be discussed
    }
}
