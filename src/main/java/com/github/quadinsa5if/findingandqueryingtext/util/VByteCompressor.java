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

    public VByteCompressor() {
        this.separator = false;
    }

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
            List<Byte> encodedBytes = new ArrayList<>();
            reader.seek(postingListOffset);
            byte[] bytes = new byte[postingListLength];
            reader.read(bytes);

            String id = "";
            boolean scorePart = false;
            boolean digitChecked = false;
            for (byte b : bytes) {
                if (!scorePart) {
                    int it = (int) b & 0xFF;
                    encodedBytes.add(b);
                    if(it >= 128){
                        int idInt = this.decode(new Iter<Byte>() {
                            int i = 0;
                            @Override
                            public Optional<Byte> next() {
                                if (i == encodedBytes.size()) {
                                    return Optional.empty();
                                } else {
                                    return Optional.of(encodedBytes.get(i++));
                                }
                            }
                        });
                        id = String.valueOf(idInt);
                        encodedBytes.clear();
                        scorePart = true;
                    }
                } else {
                    if(digitChecked){
                        int it = (int) b & 0xFF;
                        encodedBytes.add(b);
                        if(it >= 128){
                            int score = this.decode(new Iter<Byte>() {
                                int i = 0;
                                @Override
                                public Optional<Byte> next() {
                                    if (i == encodedBytes.size()) {
                                        return Optional.empty();
                                    } else {
                                        return Optional.of(encodedBytes.get(i++));
                                    }
                                }
                            });
                            entries.add(new Entry(Integer.valueOf(id), Float.valueOf("0." + String.valueOf(score))));
                            encodedBytes.clear();
                            scorePart = false;
                            digitChecked = false;
                        }
                    }
                    else if (b == (byte) '.' ) digitChecked = true;
                    else if(b == (byte) '1'){
                        entries.add(new Entry(Integer.valueOf(id), 1f));
                        digitChecked = false;
                    }

                }
            }
            return entries;
        };
    }

}
