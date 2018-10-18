package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.*;
import java.util.List;
import java.util.Map;

public interface InvertedFileSerializer {

    /**
     * Serialize and write the vocabulary to the disk
     *
     * @param vocabulary The InMemoryVocabularyImpl to serialize
     * @return The while on the disk
     */
    Result<HeaderAndInvertedFile, Exception> serialize(InMemoryVocabularyImpl vocabulary);

    /**
     * Unserialize the vocabulary from a inverted file
     *
     * @param file The inverted file
     * @param header  The offset map
     * @return The InMemoryVocabularyImpl structure
     */
    Result<InMemoryVocabularyImpl, IOException> unserialize(RandomAccessFile file, Map<String, ReversedIndexIdentifier> header);

    Result<Map<String, ReversedIndexIdentifier>, IOException> unserializeHeader(FileReader reader);

    Result<List<Entry>, IOException> unserializePostingList(RandomAccessFile reader, int postingListOffset, int postingListLength);


}
