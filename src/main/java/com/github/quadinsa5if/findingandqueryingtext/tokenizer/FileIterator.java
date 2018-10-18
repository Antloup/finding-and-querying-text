package com.github.quadinsa5if.findingandqueryingtext.tokenizer;


import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;


/**
 * Iterate through a text file by using a delimiter
 * Don't hesitate to improve that class!
 */
public class FileIterator implements Iter<String> {
    private static final char EOF = (char) -1;

    private final BufferedReader bf;
    private final char delimiter;
    private boolean isEnded = false;

    public static IO<FileIterator> fromFile(File file, char delimiter) {
        return () -> {
            final BufferedReader bf = Files.newBufferedReader(file.toPath());
            return new FileIterator(bf, delimiter);
        };
    }

    private FileIterator(BufferedReader bf, char delimiter) {
        this.bf = bf;
        this.delimiter = delimiter;
    }

    @Override
    public Optional<String> next() {
        final StringBuilder buff = new StringBuilder();
        if (isEnded) {
            return Optional.empty();
        } else {
            return Result.Try(() -> {
                char curr = (char) bf.read();
                while (curr != EOF && curr != delimiter) {
                    buff.append(curr);
                    curr = (char) bf.read();
                }
                if (curr == EOF) {
                    bf.close();
                    isEnded = true;
                    if (buff.length() == 0) {
                        return Optional.<String>empty();
                    } else {
                        return Optional.of(buff.toString());
                    }
                } else {
                    return Optional.of(buff.toString());
                }
            }).unwrap();
        }
    }
}



