package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.FileTokenizer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class App {

  static char[] PUNCTUATIONS = new char[]{'!', ',', '?', '.', ':', ';', '/', '"', '\''};
  static String[] WHITE_SPACES = new String[]{" ", "\n", "\r\n"};

  public static void main(String[] args) throws FileNotFoundException {
    final FileTokenizer fileTokenizer = new FileTokenizer(PUNCTUATIONS, WHITE_SPACES);

    Instant start = Instant.now();

    File article = new File("dataset/la010189");
    Iter<String> iter = fileTokenizer.tokenize(article);
    Optional<String> current = iter.next();

    SortedMap<String, Integer> count = new TreeMap<>();
    while (current.isPresent()) {
      String curr = current.get().toLowerCase();
      if (count.containsKey(curr)) {
        count.put(curr, count.get(curr) + 1);
      } else {
        count.put(curr, 1);
      }
      current = iter.next();
    }

    System.out.println(count);
    Instant end = Instant.now();
    System.out.println(Duration.between(start, end).toMillis());

  }
}
