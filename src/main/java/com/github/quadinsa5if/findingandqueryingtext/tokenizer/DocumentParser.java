package com.github.quadinsa5if.findingandqueryingtext.tokenizer;

import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentParser {

    private static char[] ESCAPED = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // Digits
            '?', ',', '.', ';', '?', ':', '!', '\'', '"', '(', ')', '{', '}', '[', ']', '$', // Punctuation
            '&', // Special character
            '+', '-', '*', '%', '=' // Operators
    };
    private static String[] WHITE_SPACES = new String[]{" ", "\n", "\r\n"};

    private static final String DOCUMENT_ID = "DOCID";
    private static final String DOCUMENT = "DOC";
    //  private static final String HEADER = "HEADLINE";
    private static final String PARAGRAPH = "P";

    private final List<DatasetVisitor> visitors;
    private int currentPassNumber;

    public DocumentParser(List<DatasetVisitor> visitors) {
        this.visitors = visitors;
        currentPassNumber = 1;
    }

    public void parse(File[] files) {

        List<XMLEventReader> readers = new ArrayList<>();

        int totalPassNumber = 0;

        for (DatasetVisitor visitor : visitors) {
            totalPassNumber = Math.max(totalPassNumber, visitor.getTotalPassNumber());
        }

        for (currentPassNumber = 1; currentPassNumber <= totalPassNumber; currentPassNumber++) {

            for (File file : files) {

                for (DatasetVisitor visitor : visitors) {
                    if (currentPassNumber <= visitor.getTotalPassNumber()) {
                        visitor.onPassStart(file, currentPassNumber);
                    }
                }

                XMLEventReader reader = null;
                try {
                    reader = XMLInputFactory.newFactory().createXMLEventReader(new FileInputStream(file));
                } catch (XMLStreamException | FileNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }

                while (reader.hasNext()) {
                    final XMLEvent event;
                    try {
                        event = reader.nextEvent();

                        if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(DOCUMENT)) {
                            parseArticle(reader, ESCAPED, WHITE_SPACES);
                        }
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    }
                }

                for (DatasetVisitor visitor : visitors) {
                    if (currentPassNumber <= visitor.getTotalPassNumber()) {
                        visitor.onPassEnd(currentPassNumber);
                    }
                }
            }
        }

    }


    private void parseArticle(
            final XMLEventReader reader,
            char[] ignored,
            String[] delimiters
    ) throws XMLStreamException {
        int articleId = 0;
        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(DOCUMENT)) {

                for (DatasetVisitor visitor : visitors) {
                    if (currentPassNumber <= visitor.getTotalPassNumber()) {
                        visitor.onArticleParseEnd(articleId, currentPassNumber);
                    }
                }
                return;
            }
            if (event.isStartElement()) {
                final StartElement element = event.asStartElement();
                final String elementName = element.getName().getLocalPart();
                switch (elementName) {
                    case DOCUMENT_ID:
                        articleId = Integer.valueOf(reader.getElementText().trim());
                        for (DatasetVisitor visitor : visitors) {
                            if (currentPassNumber <= visitor.getTotalPassNumber()) {
                                visitor.onArticleParseStart(currentPassNumber);
                            }
                        }

                        break;
                    case PARAGRAPH:
                        for (String term : split(reader.getElementText(), ignored, delimiters)) {
                            for (DatasetVisitor visitor : visitors) {
                                if (currentPassNumber <= visitor.getTotalPassNumber()) {
                                    visitor.onTermRead(term, currentPassNumber);
                                }
                            }
                        }
                        break;
                    default:
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
