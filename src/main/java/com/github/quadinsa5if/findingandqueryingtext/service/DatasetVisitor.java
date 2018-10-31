package com.github.quadinsa5if.findingandqueryingtext.service;

import java.io.File;

public interface DatasetVisitor {

    /**
     * Set the number of times that the visitor must be run on the data
     *
     * @return The number of pass
     */
    int getTotalPassNumber();

    /**
     * Called when an article parsing will begin
     */
    void onOpeningArticle(int articleId, int currentPassNumber);

    /**
     * Called when the parser read a term in an article
     *
     * @param term
     */
    void onTermRead(String term, int currentPassNumber);

    /**
     * Called when the article parsing has just finished
     *
     * @param articleId The article that has been parsed
     */
    void onClosingArticle(int articleId, int currentPassNumber);

    /**
     * Called when a parsing pass has just finished
     */
    void onEndingPass(int currentPassNumber);

    /**
     * Called when a new pass will start on the document
     */
    void onOpeningFile(File file, int currentPassNumber);
}
