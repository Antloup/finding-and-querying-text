package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.IdfTfScorerImplementation;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.AbstractScorerImplementation;

import java.io.File;

public class App {

    private static File dataSetFolder = new File("data");

    private static void preprocess() {

        AbstractScorerImplementation scorer = new IdfTfScorerImplementation(dataSetFolder);
        scorer.evaluate(1);

        Vocabulary vocabulary = scorer.getVocabulary();

        /* // Proposition

        Vocabulary voc;

        while((voc = scorer.fetch(1)) && !voc.isEmpty()) {
            // Serialize here
        }

        For the moment : see AbstractScorerImplementation.serializeVocabulary()

        */

    }

    public static void main(String[] args) {
        preprocess();
    }
}
