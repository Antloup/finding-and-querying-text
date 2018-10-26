package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.lang.Unit;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.Serializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class InvertedFileSerializerImplementation extends Serializer implements InvertedFileSerializer {

    public InvertedFileSerializerImplementation() {
    }

    /**
     * @param fileFolder : path of the folder (without '/' at the end)
     */
    public InvertedFileSerializerImplementation(String fileFolder) {
        super(fileFolder);
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
            if (hfValidFile.createNewFile() && ifValidFile.createNewFile()) {
                BufferedWriter hfbw = Files.newBufferedWriter(hfValidFile.toPath());
                BufferedWriter ifbw = Files.newBufferedWriter(ifValidFile.toPath());
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
                hfbw.close();
                ifbw.close();
            } else {
                throw new InvalidInvertedFileException("Files already exists");
            }
            return new HeaderAndInvertedFile(hfValidFile, ifValidFile);
        });
    }

    @Override
    public IO<Integer> writeEntries(List<Entry> entries, BufferedWriter writer) {
        final StringBuilder stringBuilder = new StringBuilder();
        return () -> {
            int totalLength = 0;
            for (Entry entry : entries) {
                stringBuilder.setLength(0);
                stringBuilder.append(entry.articleId.id)
                        .append(PARTS_DELIMITER)
                        .append(entry.score)
                        .append(IDENTIFIERS_DELIMITER);
                String res = stringBuilder.toString();
                totalLength += res.length();
                writer.write(res);
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
            return new Unit();
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
                    return new Unit();
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
                entries.add(new Entry(new ArticleId(Integer.valueOf(score[0])), Float.valueOf(score[1])));
            }
            return entries;
        };
    }
}
