package com.github.quadinsa5if.findingandqueryingtext.tokenizer;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.BufferedReader;
import java.util.Optional;

public class BufferedReaderIterator implements Iter<String> {

  static final char EOF = (char) -1;

  private final BufferedReader reader;
  private final StringBuilder buffer;

  private final char[] ESCAPED_CHARS;
  private final String[] DELIMITERS;

  private boolean isEnded;

  BufferedReaderIterator(BufferedReader reader, char[] escapedChars, String[] delimiters) {
    this.reader = reader;
    this.buffer = new StringBuilder();
    ESCAPED_CHARS = escapedChars;
    DELIMITERS = delimiters;
    isEnded = false;
  }

  @Override
  public Optional<String> next() {
    Optional<String> result;
    if (isEnded) {
      result = Optional.empty();
    } else {
      result = Result.Try(() -> {
        char currentChar;
        do {
          currentChar = (char) reader.read();
          if (mustTake(currentChar)) {
            buffer.append(currentChar);
          }
        } while (!mustStop(currentChar) && currentChar != EOF);
        if (currentChar == EOF) {
          reader.close();
          isEnded = true;
        }
        if (!isEmpty(buffer)) {
          Optional<String> res = Optional.of(buffer.toString());
          clear(buffer);
          return res;
        } else {
          return Optional.<String>empty();
        }
      }).unwrap();
    }
    return result;
  }

  private static boolean endsWith(StringBuilder buffer, String suffix) {
    int bufferLength = buffer.length();
    int suffixLength = suffix.length();
    return bufferLength >= suffixLength
        && buffer.substring(bufferLength - suffixLength, bufferLength).equals(suffix);
  }

  private static void removeSuffix(StringBuilder buffer, String suffix) {
    buffer.setLength(buffer.length() - suffix.length());
  }

  private static boolean isEmpty(StringBuilder buffer) {
    return buffer.length() == 0;
  }

  private static void clear(StringBuilder buffer) {
    buffer.setLength(0);
  }

  private boolean mustTake(char c) {
    boolean mustTake;
    if (c == EOF) {
      mustTake = false;
    } else {
      int i = 0;
      while (i < ESCAPED_CHARS.length && ESCAPED_CHARS[i] != c) {
        i++;
      }
      mustTake = i == ESCAPED_CHARS.length;
    }
    return mustTake;
  }

  private boolean mustStop(char currentChar) {
    boolean mustStop;
    if (currentChar == EOF) {
      mustStop = true;
    } else {
      int i = 0;
      while (i < DELIMITERS.length && !endsWith(buffer, DELIMITERS[i])) {
        i++;
      }
      if (i < DELIMITERS.length) {
        removeSuffix(buffer, DELIMITERS[i]);
        mustStop = buffer.length() != 0;
      } else {
        mustStop = false;
      }
    }
    return mustStop;
  }

}
