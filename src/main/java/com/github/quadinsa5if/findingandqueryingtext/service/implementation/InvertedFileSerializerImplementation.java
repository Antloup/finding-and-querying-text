package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.lang.Unit;
import com.github.quadinsa5if.findingandqueryingtext.model.*;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Compressor;
import com.github.quadinsa5if.findingandqueryingtext.service.SerializerProperties;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class InvertedFileSerializerImplementation extends SerializerProperties implements InvertedFileSerializer {

    public final Compressor compressor;

    public InvertedFileSerializerImplementation(Compressor compressor) {
        this.compressor = compressor;
    }

    /**
     * @param fileFolder : path of the folder (without '/' at the end)
     */
    public InvertedFileSerializerImplementation(String fileFolder, Compressor compressor) {
        super(fileFolder);
        this.compressor = compressor;
    }

    @Override
    public IO<HeaderAndInvertedFile> serialize(
            InMemoryVocabularyImpl vocabulary,
            HeaderAndInvertedFile outputFile
    ) {

        final File ifValidFile = outputFile.invertedFile;
        final File hfValidFile = outputFile.headerFile;

        return () -> {
            if (hfValidFile.createNewFile() && ifValidFile.createNewFile()) {
                BufferedWriter hfbw = Files.newBufferedWriter(hfValidFile.toPath());
                DataOutputStream ifbw = new DataOutputStream((new FileOutputStream(ifValidFile)));
//                BufferedWriter ifbw = Files.newBufferedWriter(ifValidFile.toPath());
                int offset = 0;
                List<Pair<String, ReversedIndexIdentifier>> reversedIndexIdentifiers = new ArrayList<>();
                for (String term : vocabulary.getTerms()) {
                    int length = writeEntries(vocabulary.getPostingList(term), ifbw).sync();
                    reversedIndexIdentifiers.add(
                            new Pair(term, new ReversedIndexIdentifier(offset, length))
                    );
                    offset += length;
                }
                writeReversedIndexIdentifier(reversedIndexIdentifiers, hfbw).sync();
                ifbw.close();
                hfbw.close();
            } else {
                throw new InvalidInvertedFileException("Files " + hfValidFile + " and " + ifValidFile + " already exists.");
            }
            return outputFile;
        };
    }

    @Override
    public IO<Integer> writeEntries(List<Entry> entries, DataOutputStream writer) {
        final StringBuilder stringBuilder = new StringBuilder();
        return () -> {
            int totalLength = 0;
            for (Entry entry : entries) {
                totalLength += compressor.putEntry(entry,writer).attempt().ok().get();
            }
            return totalLength;
        };
    }

    @Override
    public IO<Unit> writeReversedIndexIdentifier(List<Pair<String, ReversedIndexIdentifier>> reversedIndexIdentifiers, BufferedWriter writer) {
        final StringBuilder stringBuilder = new StringBuilder();
        return () -> {
            for (Pair<String, ReversedIndexIdentifier> rIdx : reversedIndexIdentifiers) {
                stringBuilder.setLength(0);
                stringBuilder.append(rIdx.first)
                        .append(PARTS_DELIMITER)
                        .append(rIdx.second.offset)
                        .append(PARTS_DELIMITER)
                        .append(rIdx.second.length)
                        .append(NEW_LINE);
                writer.write(stringBuilder.toString());
            }
            return Unit.create();
        };
    }

    @Override
    public IO<InMemoryVocabularyImpl> unserialize(RandomAccessFile file, Map<String, ReversedIndexIdentifier> header) {
        return () -> {
            InMemoryVocabularyImpl vocabulary = new InMemoryVocabularyImpl();
            for (Map.Entry<String, ReversedIndexIdentifier> termHeader : header.entrySet()) {
                IO<List<Entry>> entries = unserializePostingList(file, termHeader.getValue().offset, termHeader.getValue().length);
                entries.map(it -> {
                    for (Entry entry : it) {
                        vocabulary.putEntry(termHeader.getKey(), entry);
                    }
                    return Unit.create();
                }).sync();
            }
            return vocabulary;
        };
    }

    @Override
    public IO<SortedMap<String, ReversedIndexIdentifier>> unserializeHeader(FileReader reader) {
        return () -> {
            SortedMap<String, ReversedIndexIdentifier> header = new TreeMap<>();
            String line;
            final LineNumberReader lineReader = new LineNumberReader(reader);
            while ((line = lineReader.readLine()) != null) {
                String[] attributes = line.split(String.valueOf(PARTS_DELIMITER));
                if (attributes.length != 3) {
                    throw new InvalidInvertedFileException("Invalid header file at line " + lineReader.getLineNumber());
                }
                header.put(attributes[0], new ReversedIndexIdentifier(Integer.valueOf(attributes[1]), Integer.valueOf(attributes[2])));
            }
            return header;
        };
    }

    @Override
    public IO<List<Entry>> unserializePostingList(RandomAccessFile reader, int postingListOffset, int postingListLength) {
        return () -> {
            return compressor.getEntries(reader,postingListOffset,postingListLength).attempt().ok().get();
        };
    }

}
