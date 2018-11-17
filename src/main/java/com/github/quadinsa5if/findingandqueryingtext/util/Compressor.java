package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;

import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.util.List;

public abstract class Compressor implements EncoderDecoder<Integer>{

    public static final char PARTS_DELIMITER = ':';
    public static final char IDENTIFIERS_DELIMITER = ';';
    private final static int FLOAT_PRECISION = 1000;

    public IO<Integer> putEntry(Entry entry, DataOutputStream writer){
        return () -> {
            int length = 0;
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.setLength(0);
            stringBuilder.append(entry.articleId)
                    .append(PARTS_DELIMITER)
                    .append((int) entry.score + ".");
            String res = stringBuilder.toString();
            length += res.length();
            writer.write(res.getBytes());
            Iter<Byte> bytes = this.encode(getDecimal(entry.score));
            for (Byte b : bytes) {
                writer.write(b);
                length++;
            }

            return length;
        };
    }

    /**
     * @param f
     * @return Decimal part of float
     */
    protected Integer getDecimal(float f) {
        int lowerBound = (int) f;
        float decimalPart = f - lowerBound;
        return (int) (decimalPart * FLOAT_PRECISION);
    }

    public abstract IO<List<Entry>> getEntries(RandomAccessFile reader, int postingListOffset, int postingListLength);

}
