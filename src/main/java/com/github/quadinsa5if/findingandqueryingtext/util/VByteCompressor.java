package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.pow;

public class VByteCompressor extends Compressor {

    public VByteCompressor() {}

    @Override
    public Integer decode(Iter<Byte> msg) {
        int sum = 0;
        List<Integer> vbInt = new ArrayList<>();
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

}
