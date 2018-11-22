package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;

import java.io.RandomAccessFile;
import java.util.*;

public class NaiveCompressor extends Compressor implements EncoderDecoder<Integer> {

    private final static byte ZERO = (byte)'0';

    public NaiveCompressor() {}

    @Override
    public Integer decode(Iter<Byte> msg) {
        int sum = 0;
        Optional<Byte> current = msg.next();
        while (current.isPresent()) {
            byte it = current.get();
            if(it == Compressor.PARTS_DELIMITER){
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
        return Iter.over((String.valueOf(input) + Compressor.PARTS_DELIMITER).getBytes());
    }

}
