package com.github.quadinsa5if.findingandqueryingtext.tokenizer;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileTokenizer implements Tokenizer<File, String, FileNotFoundException> {

  private final char[] escapedChars;
  private final String[] delimiters;

  public FileTokenizer(char[] escapedChars, String[] delimiters) {
    this.escapedChars = escapedChars;
    this.delimiters = delimiters;
  }

  @Override
  public Iter<String> tokenize(File input) throws FileNotFoundException {
    final BufferedReader bf = new BufferedReader(new FileReader(input));
    return new BufferedReaderIterator(bf, escapedChars, delimiters);
  }

}
