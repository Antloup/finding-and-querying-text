package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.FileTokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public class App {

  private static char[] ESCAPED = new char[]{
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // Digits
      '?', ',', '.', ';', '?', ':', '!', '\'', '"', '(', ')', '{', '}', '[', ']', '$', // Punctuation
      '&', // Special character
      '+', '-', '*', '%', '=' // Operators
  };
  private static String[] WHITE_SPACES = new String[]{" ", "\n", "\r\n"};

  private static File dataSetFolder = new File("dataset");

  public static void main(String[] args) {

  }
}
