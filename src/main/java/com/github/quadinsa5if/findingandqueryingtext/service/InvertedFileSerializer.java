package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InDiskVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface InvertedFileSerializer {

    /**
     * Serialize and write the vocabulary to the disk
     *
     * @param vocabulary The InMemoryVocabularyImpl to serialize
     * @return The while on the disk
     */
    Result<File, Exception> serialize(InMemoryVocabularyImpl vocabulary);

    /**
     * Unserialize the vocabulary from a inverted file
     *
     * @param file The inverted file
     * @param header  The offset map
     * @return The InMemoryVocabularyImpl structure
     */
    Result<InMemoryVocabularyImpl, Exception> unserialize(File file, Map<String, ReversedIndexIdentifier> header) throws InvalidInvertedFileException;

    Result<Map<String, ReversedIndexIdentifier>, Exception> unserializeHeader(File file);

    Result<List<Entry>, Exception> unserializePostingList(File file, int postingListOffset, int postingListLength);


}
