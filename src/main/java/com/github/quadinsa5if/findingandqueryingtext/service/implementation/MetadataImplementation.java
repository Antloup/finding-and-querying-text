package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleHeader;
import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;

import java.io.File;

public class MetadataImplementation implements DatasetVisitor {

    String currentPath;

    public MetadataImplementation() {
        currentPath = "";
    }

    @Override
    public int getTotalPassNumber() {
        return 1;
    }

    @Override
    public void onArticleParseStart(int currentPassNumber) {

    }

    @Override
    public void onTermRead(String term, int currentPassNumber) {

    }

    @Override
    public void onArticleParseEnd(int articleId, int currentPassNumber) {
        ArticleHeader articleHeader = new ArticleHeader(articleId, currentPath);

        //todo: do sth here
    }

    @Override
    public void onPassEnd(int currentPassNumber) {

    }

    @Override
    public void onPassStart(File file, int currentPassNumber) {
        currentPath = file.getPath();
    }
}
