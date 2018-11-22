package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Compressor implements EncoderDecoder<Integer> {

    static final char PARTS_DELIMITER = ':';
    static final char IDENTIFIERS_DELIMITER = ';';
    private final static int FLOAT_PRECISION = 10_000;

    private void addAll(List<Byte> buffer, Iter<Byte> bytes) {
        for (byte b : bytes) {
            buffer.add(b);
        }
    }

    private void addIdentifiersDelimiter(List<Byte> buffer) {
        buffer.add((byte) IDENTIFIERS_DELIMITER);
    }

    public void putEntry(Entry entry, List<Byte> buffer) {
        addAll(buffer, encode(entry.articleId));
        Pair<Integer, Integer> partsOfNumber = splitDecimal(entry.score);
        addAll(buffer, encode(partsOfNumber.first));
        addAll(buffer, encode(partsOfNumber.second));
        addIdentifiersDelimiter(buffer);
    }

    /**
     * @param f
     * @return Decimal part of float
     */
    Pair<Integer,Integer> splitDecimal(float f) {
        if (f < 0) return new Pair<>(0, 0);
        int integerPart = (int) f;
        float decimalPart = f - integerPart;
        return new Pair<>(integerPart, (int) (decimalPart * FLOAT_PRECISION));
    }

    public IO<List<Entry>> getEntries(RandomAccessFile reader, int postingListOffset, int postingListLength) {
        return () -> {
            byte[] bytes = new byte[postingListLength];
            reader.seek(postingListOffset);
            reader.read(bytes);

            List<byte[]> posts = split(bytes, (byte) Compressor.IDENTIFIERS_DELIMITER);

            List<Entry> entries = new ArrayList<>();
            for (byte[] post : posts) {
                Iter<Byte> iter = Iter.over(post);

                int scoredId = decode(iter);
                int intPart = decode(iter);
                int floatPart = decode(iter);

                entries.add(new Entry(scoredId, intPart + floatPart / (float) FLOAT_PRECISION));
            }

            return entries;
        };
    }

    private List<byte[]> split(byte[] bytes, byte split) {
        List<byte[]> result = new ArrayList<>();
        int offset = 0;
        int length = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == split) {
                byte[] part = new byte[length];
                System.arraycopy(bytes, offset, part, 0, length);
                offset = i + 1;
                length = 0;
                result.add(part);
            }
            length += 1;
        }
        return result;
    }

}
