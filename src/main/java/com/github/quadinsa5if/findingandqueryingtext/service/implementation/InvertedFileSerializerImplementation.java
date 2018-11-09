package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.lang.Unit;
import com.github.quadinsa5if.findingandqueryingtext.model.*;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.util.EncoderDecoder;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;
import com.github.quadinsa5if.findingandqueryingtext.service.SerializerProperties;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class InvertedFileSerializerImplementation extends SerializerProperties implements InvertedFileSerializer {

    public final EncoderDecoder<Integer> compressor;
    private static final int FLOAT_PRECISION = 1000;

    private final static byte zero = (byte)'0';

    public InvertedFileSerializerImplementation(EncoderDecoder<Integer> compressor) {
        this.compressor = compressor;
    }

    /**
     * @param fileFolder : path of the folder (without '/' at the end)
     */
    public InvertedFileSerializerImplementation(String fileFolder, EncoderDecoder<Integer> compressor) {
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
                ifbw.close();
                hfbw.close();
            } else {
                throw new InvalidInvertedFileException("Files " + hfValidFile + " and " + ifValidFile + " already exists.");
            }
            return outputFile;
        };
    }

    @Override
    public IO<Integer> writeEntries(List<Entry> entries, BufferedWriter writer) {
        final StringBuilder stringBuilder = new StringBuilder();
        return () -> {
            int totalLength = 0;
            for (Entry entry : entries) {
                Iter<Byte> bytes = compressor.encode(getDecimal(entry.score));

                stringBuilder.setLength(0);
                stringBuilder.append(entry.articleId)
                        .append(PARTS_DELIMITER)
                        .append((int) entry.score + ".");
                for (Byte data : bytes) {
                    stringBuilder.append(data);
                }
                stringBuilder.append(IDENTIFIERS_DELIMITER);
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
            List<Entry> entries = new ArrayList<>();

            reader.seek(postingListOffset);
            byte[] bytes = new byte[postingListLength];
            reader.read(bytes);
            String[] termPl = new String(bytes).split(String.valueOf(IDENTIFIERS_DELIMITER));

            for (String term : termPl) {
                if ("".equals(term)) {
                    break;
                }
                String[] score = term.split(String.valueOf(PARTS_DELIMITER));
                if (score.length != 2) {
                    throw new InvalidInvertedFileException("Invalid inverted file between offset " + postingListOffset + " and " + (postingListOffset + postingListLength));
                }

                Integer decode = compressor.decode(getEncode(score[1].substring(score[1].indexOf('.') + 1)));
                String decodedString = score[1].substring(0,score[1].indexOf('.')+1) + String.valueOf(decode);
                entries.add(new Entry(Integer.valueOf(score[0]), Float.valueOf(decodedString)));
            }
            return entries;
        };
    }

    Iter<Byte> iterableOf(byte[] byteArray) {
        int i = 0;
        return new Iter<Byte>() {
            int i = 0;

            @Override
            public Optional<Byte> next() {
                if (i == byteArray.length) {
                    return Optional.empty();
                } else {
                    return Optional.of(byteArray[i++]);
                }
            }
        };
    }


    /**
     *
     * @param f
     * @return Decimal part of float
     */
    protected int getDecimal(float f) {
        int lowerBound = (int) f;
        float decimalPart = f - lowerBound;
        return (int) decimalPart * FLOAT_PRECISION;
    }

    /**
     *
     * @param s
     * @return Encoded part of the string
     */
    protected Iter<Byte> getEncode(String s){
        byte[] encodedByte = s.getBytes();

        for(int i=0;i < encodedByte.length;i++){
            encodedByte[i] -= zero;
        }

        return new Iter<Byte>() {
            int i = 0;

            @Override
            public Optional<Byte> next() {
                if (i == encodedByte.length) {
                    return Optional.empty();
                } else {
                    return Optional.of(encodedByte[i++]);
                }
            }
        };
    }
}
