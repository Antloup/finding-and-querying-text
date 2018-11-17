package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.pow;

public class VByteCompressor extends Compressor {

    @Override
    public Integer decode(Iter<Byte> msg) {
        int sum = 0;
        List<Integer> vbInt = new ArrayList<Integer>();
        Optional<Byte> current = msg.next();

        while (current.isPresent()) {
            int it = (int) current.get() & 0xFF;
            vbInt.add(it);
            if (it >= 128) break;
            current = msg.next();
        }

        int i = vbInt.size() - 1;
        for (Integer it : vbInt) {
            if (it >= 128) {
                sum += it - 128;
            } else {
                sum += pow(128, i--) * it;
            }
        }

        return sum;
    }

    @Override
    public Iter<Byte> encode(Integer input) {

        List<Byte> parts = new ArrayList<>();
        int value = input;
        byte k = (byte) ((value % 128) + 128);
        parts.add(k);
        value /= 128;
        while (value != 0) {
            k = (byte) (value % 128);
            parts.add(0, k);
            value /= 128;
        }

        return new Iter<Byte>() {
            int i = 0;

            @Override
            public Optional<Byte> next() {
                if (i == parts.size()) {
                    return Optional.empty();
                } else {
                    return Optional.of(parts.get(i++));
                }
            }
        };

    }

    public IO<List<Entry>> getEntries(RandomAccessFile reader, int postingListOffset, int postingListLength) {
        return () -> {
            List<Entry> entries = new ArrayList<>();
            reader.seek(postingListOffset);
            byte[] bytes = new byte[postingListLength];
            reader.read(bytes);

            String id = "";
            int score = 0;
            boolean scorePart = false;
            List<Integer> vbInt = new ArrayList<Integer>();
            for (byte b : bytes) {
                if (b == PARTS_DELIMITER) {
                    scorePart = true;
                } else if (!scorePart) {
                    id += (char) b;
                } else {
                    if (b == (byte) '.' || b == (byte) '0') continue;
                    else {
                        int it = (int) b & 0xFF;
                        vbInt.add(it);
                        if (it >= 128) {
                            int i = vbInt.size() - 1;
                            for (Integer it2 : vbInt) {
                                if (it2 < 128) {
                                    score += pow(128, i--) * it2;
                                }
                            }
                            score += it - 128;
                            entries.add(new Entry(Integer.valueOf(id), Float.valueOf("0." + String.valueOf(score))));
                            vbInt.clear();
                            id = "";
                            score = 0;
                            scorePart = false;
                        }
                    }

                }
            }
            return entries;
        };
    }

}
