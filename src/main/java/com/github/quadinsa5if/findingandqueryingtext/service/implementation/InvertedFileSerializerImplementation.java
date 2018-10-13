package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;

import java.io.File;
import java.util.List;
import java.util.Map;

public class InvertedFileSerializerImplementation implements InvertedFileSerializer {

    @Override
    public InMemoryVocabularyImpl unserialize(File file) {
        return null;
    }

    @Override
    public File serialize(Vocabulary vocabulary) {
        return null;
    }

    @Override
    public Map<String, ReversedIndexIdentifier> unserializeHeader(File file) {
        return null;
    }

    @Override
    public List<Entry> unserializePostingList(File file, int postingListOffset, int postingListLength) {
        return null;
    }
}
