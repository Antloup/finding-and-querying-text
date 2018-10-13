package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IdfTfScorerImplementation extends AbstractScorerImplementation {

    private Map<String, Double> numberOfArticlesContainingTerm;
    private Map<String, Double> termsFrequencyInCurrentArticle;
    private Map<String, Double> idf;
    private int numberOfArticles;

    public IdfTfScorerImplementation(File dataSetFolder) {
        super(dataSetFolder);

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

        if (getCurrentPassNumber() == 1) {
            numberOfArticles++;
            termsFrequencyInCurrentArticle.clear();
        }
    }

    @Override
    public void onTermRead(String term) {
        if (getCurrentPassNumber() == 1) {
            if (!termsFrequencyInCurrentArticle.containsKey(term)) {
                if (numberOfArticlesContainingTerm.containsKey(term)) {
                    Double currentArticlesNumberContainingTerm = numberOfArticlesContainingTerm.get(term);
                    numberOfArticlesContainingTerm.put(term, currentArticlesNumberContainingTerm + 1.0);
                } else {
                    numberOfArticlesContainingTerm.put(term, 1.0);
                }
            }
            termsFrequencyInCurrentArticle.put(term, 1.0);

        } else if (getCurrentPassNumber() == 2) {

            if (!termsFrequencyInCurrentArticle.containsKey(term)) {
                termsFrequencyInCurrentArticle.put(term, 1.0);
            } else {
                termsFrequencyInCurrentArticle.put(term, termsFrequencyInCurrentArticle.get(term) + 1.0);
            }
        }
    }

    @Override
    public void onArticleParseEnd(ArticleId articleId) {
        if (getCurrentPassNumber() == 2) {
            for (String term : termsFrequencyInCurrentArticle.keySet()) {
                Double tf = 1 + Math.log(termsFrequencyInCurrentArticle.get(term));
                Double score = tf * idf.get(term);
                setScore(term, articleId, score.floatValue());
            }
            finalizeToScoreArticle();
        }
    }

    @Override
    public void onPassEnd() {
        if (getCurrentPassNumber() == 1) {
            for (String term : numberOfArticlesContainingTerm.keySet()) {
                idf.put(term, Math.log(numberOfArticles / (1 + numberOfArticlesContainingTerm.get(term))));
            }
        }
    }

}