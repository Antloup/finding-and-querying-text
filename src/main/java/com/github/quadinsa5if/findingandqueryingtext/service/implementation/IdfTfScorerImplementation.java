package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class IdfTfScorerImplementation extends AbstractScorerImplementation {

    private Map<String, Double> numberOfArticlesContainingTerm;
    private Map<String, Double> termsFrequencyInCurrentArticle;
    private Map<String, Double> idf;
    private int numberOfArticles;

    public IdfTfScorerImplementation(Stream<Path> dataSetFolder, InvertedFileSerializer serializer) {
        super(dataSetFolder, serializer);

        numberOfArticles = 0;
        numberOfArticlesContainingTerm = new HashMap<>();
        termsFrequencyInCurrentArticle = new HashMap<>();
        idf = new HashMap<>();
    }

    public int getTotalPassNumber() {
        return 2;
    }

    @Override
    public void onArticleParseStart() {

        if (currentPassNumber == 1) {
            numberOfArticles++;
            termsFrequencyInCurrentArticle.clear();
        }
    }

    @Override
    public void onTermRead(String term) {
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
    public void onArticleParseEnd(ArticleId articleId) {
        if (currentPassNumber == 2) {

            for(Map.Entry<String, Double> term : termsFrequencyInCurrentArticle.entrySet()) {
                Double tf = 1 + Math.log(termsFrequencyInCurrentArticle.get(term.getKey()));
                Double score = tf * idf.get(term.getKey());
                setScore(term.getKey(), articleId, score.floatValue());
            }
            finalizeToScoreArticle();
        }
    }

    @Override
    public void onPassEnd() {
        if (currentPassNumber == 1) {
            for(Map.Entry<String, Double> term: numberOfArticlesContainingTerm.entrySet()) {
                idf.put(term.getKey(), Math.log(numberOfArticles / (1 + term.getValue())));
            }
        }
    }

}
