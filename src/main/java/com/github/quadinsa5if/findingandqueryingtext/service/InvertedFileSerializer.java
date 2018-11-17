package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.lang.Unit;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public interface InvertedFileSerializer {

    /**
     * Serialize and write the vocabulary to the disk
     *
     * @param vocabulary The InMemoryVocabularyImpl to serialize
     * @return The while on the disk
     */
    IO<HeaderAndInvertedFile> serialize(
            InMemoryVocabularyImpl vocabulary,
            HeaderAndInvertedFile ouputFile
    );

    IO<Integer> writeEntries(List<Entry> entries, DataOutputStream writer);
    IO<Unit> writeReversedIndexIdentifier(List<Pair<String, ReversedIndexIdentifier>> reversedIndexIdentifiers, BufferedWriter writer);

    /**
     * Unserialize the vocabulary from a inverted file
     *
     * @param file The inverted file
     * @param header  The offset map
     * @return The InMemoryVocabularyImpl structure
     */
    IO<InMemoryVocabularyImpl> unserialize(RandomAccessFile file, Map<String, ReversedIndexIdentifier> header);

    IO<SortedMap<String, ReversedIndexIdentifier>> unserializeHeader(FileReader reader);

    IO<List<Entry>> unserializePostingList(RandomAccessFile reader, int postingListOffset, int postingListLength);


}
