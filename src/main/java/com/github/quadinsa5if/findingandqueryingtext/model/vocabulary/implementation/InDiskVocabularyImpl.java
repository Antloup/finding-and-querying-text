package com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.InvertedFileSerializerImplementation;
import com.github.quadinsa5if.findingandqueryingtext.util.Cache;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * vocabulary recorded on the disk (not in the RAM)
 */
public class InDiskVocabularyImpl implements Vocabulary {

    /**
     * The cache of the posting list
     */
    private final Cache<String, List<Entry>> cache;

    /**
     * The reversed index identifiers. They are used to localize the different posting lists.
     */
    private final Map<String, ReversedIndexIdentifier> reversedIndexIdentifiers;

    /**
     * An inverted file serializer
     */
    private final InvertedFileSerializer serializer;

    public InDiskVocabularyImpl(final FileReader headerFile, final RandomAccessFile postingListFile, int cacheSize) {
        serializer = new InvertedFileSerializerImplementation();
        reversedIndexIdentifiers = serializer.unserializeHeader(headerFile)
                .attempt()
                .unwrap();
        cache = new Cache<>(cacheSize, term -> {
            final Optional<ReversedIndexIdentifier> postingListIdentifier = Optional.ofNullable(reversedIndexIdentifiers.get(term));
            return postingListIdentifier.map(it -> serializer.unserializePostingList(postingListFile, it.offset, it.length).attempt().unwrap())
                    .orElse(new ArrayList<>());
        });
    }

    @Override
    public List<Entry> getPostingList(String term) {
        return cache.getOrLoad(term);
    }

    @Override
    public List<String> getTerms() {
        return new ArrayList<>(reversedIndexIdentifiers.keySet());
    }
}
