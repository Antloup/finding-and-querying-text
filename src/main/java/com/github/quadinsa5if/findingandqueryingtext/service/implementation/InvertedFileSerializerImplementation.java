package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;

import java.io.File;

public class InvertedFileSerializerImplementation implements InvertedFileSerializer {
    @Override
    public File serialize(InMemoryVocabularyImpl vocabulary) {
        return null;
    }

    @Override
    public InMemoryVocabularyImpl unserialize(File file) {
        return null;
    }
}
