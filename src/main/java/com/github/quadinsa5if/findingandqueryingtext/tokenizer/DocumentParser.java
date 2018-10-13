package com.github.quadinsa5if.findingandqueryingtext.tokenizer;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class DocumentParser {

  private static final String DOCUMENT_ID = "DOCID";
  private static final String DOCUMENT = "DOC";
  private static final String HEADER = "HEADLINE";
  private static final String PARAGRAPH = "P";

  private final XMLEventReader reader;

  public DocumentParser(File document) throws XMLStreamException, FileNotFoundException {
    reader = XMLInputFactory.newFactory().createXMLEventReader(new FileInputStream(document));
  }

  public void parse(
      InMemoryVocabularyImpl vocabulary,
      char[] ignored,
      String[] delimiters
  ) throws XMLStreamException {
    while (reader.hasNext()) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(DOCUMENT)) {
        parseArticle(reader, vocabulary, ignored, delimiters);
      }
    }

  }

  private void parseArticle(
      final XMLEventReader reader,
      InMemoryVocabularyImpl vocabulary,
      char[] ignored,
      String[] delimiters
  ) throws XMLStreamException {
    int id = 0;
    Map<String, Integer> wordCount = new HashMap<>();
    int nbOfWords = 0;
    while (reader.hasNext()) {
      final XMLEvent event = reader.nextEvent();
      if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(DOCUMENT)) {
        final ArticleId article = new ArticleId(id, "");
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
          vocabulary.putEntry(entry.getKey(), new Entry(article, entry.getValue().floatValue() / nbOfWords));
        }
        return;
      }
      if (event.isStartElement()) {
        final StartElement element = event.asStartElement();
        final String elementName = element.getName().getLocalPart();
        switch (elementName) {
          case DOCUMENT_ID:
            id = Integer.valueOf(reader.getElementText().trim());
            break;
          case PARAGRAPH:
            for (String word : split(reader.getElementText(), ignored, delimiters)) {
              if (wordCount.containsKey(word)) {
                wordCount.put(word, wordCount.get(word) + 1);
              } else {
                wordCount.put(word, 1);
              }
              nbOfWords += 1;
            }
            break;
        }
      }
    }
  }

  private List<String> split(String text, char[] ignored, String[] delimiters) {
    final List<String> tokens = new ArrayList<>();
    final StringBuilder buffer = new StringBuilder();
    int length = text.length();
    for (int i = 0; i < length; i++) {
      char c = text.charAt(i);
      if (!contains(ignored, c)) {
        buffer.append(Character.toLowerCase(c));
      }
      for (String delimiter : delimiters) {
        if (endsWith(buffer, delimiter)) {
          removeSuffix(buffer, delimiter);
          if (buffer.length() > 0) {
            tokens.add(buffer.toString());
            buffer.setLength(0);
          }
        }
      }
    }
    if (buffer.length() > 0) {
      tokens.add(buffer.toString());
    }
    return tokens;
  }

  private boolean contains(char[] chars, char c) {
    int i = 0;
    while (i < chars.length && chars[i] != c) {
      i += 1;
    }
    return i != chars.length;
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

}
