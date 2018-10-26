package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;

import java.util.List;

public interface Scorer {

    /**
     * Parse and score the data
     *
     * @param batchSize The number of article parsed before serialization
     * @return The list of all files
     */
    List<HeaderAndInvertedFile> evaluate(int batchSize);

}
