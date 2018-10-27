package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;

public class MetadataImplementation implements DatasetVisitor {

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

    }

    @Override
    public void onPassEnd(int currentPassNumber) {

    }
}
