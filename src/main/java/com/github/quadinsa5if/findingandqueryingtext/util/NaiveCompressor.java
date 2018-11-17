package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;

import java.io.RandomAccessFile;
import java.util.*;

public class NaiveCompressor extends Compressor implements EncoderDecoder<Integer> {

    private final static byte ZERO = (byte)'0';
    private final static byte DELIMITER = (byte)';';

    @Override
    public Integer decode(Iter<Byte> msg) {
        int sum = 0;
        Optional<Byte> current = msg.next();
        while (current.isPresent()) {
            byte it = current.get();
            if(it == DELIMITER){
                break;
            }
            sum *= 10;
            sum += (int) it - ZERO;
            current = msg.next();
        }

        return sum;
    }

    @Override
    public Iter<Byte> encode(Integer input) {

        List<Byte> bytes = new ArrayList<>();
        for(byte b: String.valueOf(input).getBytes()) {
            bytes.add(b);
        }
        bytes.add(DELIMITER);

        return new Iter<Byte>() {
            int i = 0;
            @Override
            public Optional<Byte> next() {
                if (i == bytes.size()) {
                    return Optional.empty();
                } else {
                    return Optional.of(bytes.get(i++));
                }
            }
        };

    }

    public IO<List<Entry>> getEntries(RandomAccessFile reader, int postingListOffset, int postingListLength){
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

                Integer decode = this.decode(getEncode(score[1].substring(score[1].indexOf('.') + 1)));
                String decodedString = score[1].substring(0, score[1].indexOf('.') + 1) + String.valueOf(decode);
                entries.add(new Entry(Integer.valueOf(score[0]), Float.valueOf(decodedString)));
            }
            return entries;
        };
    }

    /**
     * @param s
     * @return Encoded part of the string
     */
    protected Iter<Byte> getEncode(String s) {
        byte[] encodedByte = s.getBytes();

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
