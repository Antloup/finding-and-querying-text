package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class InvertedFileSerializerImplementation implements InvertedFileSerializer {

    private static final String INVERTED_FILE = "/if";
    private static final String HEADER_FILE = "/hf";
    private String fileFolder = "fileTest/novb";

    private static final char PARTS_DELIMITER = ':';
    private static final char IDENTIFIERS_DELIMITER = ';';
    private static final String NEW_LINE = "\r\n";

    public InvertedFileSerializerImplementation() {
    }

    /**
     * @param fileFolder : path of the folder (without '/' at the end)
     */
    public InvertedFileSerializerImplementation(String fileFolder) {
        this.fileFolder = fileFolder;
    }

    @Override
    public Result<HeaderAndInvertedFile, Exception> serialize(InMemoryVocabularyImpl vocabulary) {
        int fileNumber = 0;
        final File directory = new File(fileFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File ifFile = new File(fileFolder + INVERTED_FILE + fileNumber);
        File hfFile = new File(fileFolder + HEADER_FILE + fileNumber);
        while (ifFile.exists() || hfFile.exists()) {
            fileNumber += 1;
            ifFile = new File(fileFolder + INVERTED_FILE + fileNumber);
            hfFile = new File(fileFolder + HEADER_FILE + fileNumber);
        }
        final File ifValidFile = ifFile;
        final File hfValidFile = hfFile;
        return Result.Try(() -> {
            StringBuilder strBuilder = new StringBuilder();
            if (hfValidFile.createNewFile() && ifValidFile.createNewFile()) {
                BufferedWriter hfbw = Files.newBufferedWriter(hfValidFile.toPath());
                BufferedWriter ifbw = Files.newBufferedWriter(ifValidFile.toPath());
                int offset = 0;
                int length = 0;
                for (String term : vocabulary.getTerms()) {
                    for (Entry entry : vocabulary.getPostingList(term)) {
                        strBuilder.setLength(0); // Clear buffer
                        strBuilder.append(entry.articleId.id)
                                .append(PARTS_DELIMITER)
                                .append(entry.score)
                                .append(IDENTIFIERS_DELIMITER);
                        String e = strBuilder.toString();
                        length += e.length();
                        ifbw.write(e);
                    }
                    strBuilder.setLength(0);
                    strBuilder.append(term)
                            .append(PARTS_DELIMITER)
                            .append(offset)
                            .append(PARTS_DELIMITER)
                            .append(length)
                            .append(NEW_LINE);
                    hfbw.write(strBuilder.toString());
                    offset += length;
                    length = 0;
                }
                hfbw.close();
                ifbw.close();
            } else {
                throw new InvalidInvertedFileException("Files already exists");
            }
            return new HeaderAndInvertedFile(hfValidFile, ifValidFile);
        });
    }

    @Override
    public IO<InMemoryVocabularyImpl> unserialize(RandomAccessFile file, Map<String, ReversedIndexIdentifier> header) {
        return () -> {
            InMemoryVocabularyImpl vocabulary = new InMemoryVocabularyImpl();
            for (Map.Entry<String, ReversedIndexIdentifier> termHeader : header.entrySet()) {
                IO<List<Entry>> entries = unserializePostingList(file, termHeader.getValue().offset, termHeader.getValue().length);
                entries.map(it -> {
                    for(Entry entry : it) {
                        vocabulary.putEntry(termHeader.getKey(), entry);
                    }
                    return 0;
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
            List<Entry> entries = new ArrayList<>();

            reader.seek(postingListOffset);
            byte[] bytes = new byte[postingListLength];
            reader.read(bytes);
            String[] termPl = new String(bytes).split(String.valueOf(IDENTIFIERS_DELIMITER));

            for (String term : termPl) {
                if ("".equals(term)) {
                    break;
                }
                String[] score = term.split(":");
                if (score.length != 2) {
                    throw new InvalidInvertedFileException("Invalid inverted file between offset " + postingListOffset + " and " + (postingListOffset + postingListLength));
                }
                //TODO: Call Metadata service
                entries.add(new Entry(new ArticleId(Integer.valueOf(score[0])), Float.valueOf(score[1])));
            }
            return entries;
        };
    }
}
