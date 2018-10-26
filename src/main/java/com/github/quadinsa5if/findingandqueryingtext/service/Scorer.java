package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;

import java.util.List;

public interface Scorer {

    /**
     * Set the number of times that the scorer must be run on the data
     *
     * @return The number of pass
     */
    int getTotalPassNumber();

    /**
     * Called when the article parsing will begin
     */
    void onArticleParseStart();

    /**
     * Called when the parser read a term in an article
     *
     * @param term
     */
    void onTermRead(String term);

    /**
     * Called when the article parsing has just finished
     *
     * @param articleId The article that has been parsed
     */
    void onArticleParseEnd(int articleId);

    /**
     * Called when a parsing pass has just finished
     */
    void onPassEnd();

    /**
     * Parse and score the data
     *
     * @param batchSize The number of article parsed before serialization
     * @return The list of all files
     */
    List<HeaderAndInvertedFile> evaluate(int batchSize);

}
