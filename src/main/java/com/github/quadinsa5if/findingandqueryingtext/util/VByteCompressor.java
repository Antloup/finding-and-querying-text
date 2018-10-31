package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VByteCompressor implements EncoderDecoder<Integer> {

    @Override
    public Integer decode(Iter<Byte> msg) {
        int sum = 0;
        Optional<Byte> current = msg.next();
        while (current.isPresent()) {
            int it = (int) current.get();
            if (it >= 0) {
                sum *= 128;
                sum += it;
            } else {
                sum *= 128;
                sum += it + 128;
                break;
            }
            current = msg.next();
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

//    private static byte zero = (byte) '0';
//
//    @Override
//    public Integer decode(Iter<Byte> msg) {
//        int sum = 0;
//        Optional<Byte> current = msg.next();
//        while (current.isPresent()) {
//            int it = (int) current.get();
//            if (it >= 128) {
//                sum += it - 128;
//            } else {
//                sum += 128 * it;
//                break;
//            }
//            current = msg.next();
//        }
//        return sum;
//    }
//
//    @Override
//    public Iter<Byte> encode(Integer input) {
//
//        List<Byte> parts = new ArrayList<>();
//        int value = input;
//        byte k = (byte) ((value % 128) + 128);
//        parts.add(k);
//        value /= 128;
//        while (value != 0) {
//            k = (byte) (value % 128);
//            parts.add(0, k);
//            value /= 128;
//        }
//
//        return new Iter<Byte>() {
//            int i = 0;
//
//            @Override
//            public Optional<Byte> next() {
//                if (i == parts.size()) {
//                    return Optional.empty();
//                } else {
//                    return Optional.of(parts.get(i++));
//                }
//            }
//        };
//
//    }

}
