package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NaiveCompressor implements EncoderDecoder<Integer> {

    private static byte PARTS_DELIMITER = (byte) ':';
    private static byte POSTING_LIST_DELIMITER = (byte) ';';
    private static byte ZERO = (byte) '0';

    @Override
    public Integer decode(Iter<Byte> msg) {
        int sum = 0;
        Optional<Byte> current = msg.next();
        while (current.isPresent()) {
            byte it = current.get();
            sum *= 10;
            sum += (int)it;
            current = msg.next();
        }

        return sum;
    }

    @Override
    public Iter<Byte> encode(Integer input) {
        List<Byte> bytes = new ArrayList<>();

        LinkedList<Integer> stack = new LinkedList<>();
        while (input > 0) {
            stack.push( input % 10 );
            input = input / 10;
        }

        while (!stack.isEmpty()) {
            bytes.add(stack.pop().byteValue());
        }

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
}
