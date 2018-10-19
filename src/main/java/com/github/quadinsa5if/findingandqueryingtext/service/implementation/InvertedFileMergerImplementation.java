package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileMerger;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.FileIterator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class InvertedFileMergerImplementation implements InvertedFileMerger {

    private static final char PARTS_DELIMITER = ':';
    private static final char IDENTIFIERS_DELIMITER = ';';
    private static final String NEW_LINE = "\r\n";

    private final InvertedFileSerializer serializer;

    public InvertedFileMergerImplementation(InvertedFileSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public HeaderAndInvertedFile merge(Iterable<HeaderAndInvertedFile> parts, HeaderAndInvertedFile headerAndInvertedFile) {

        final List<List<Pair<String, ReversedIndexIdentifier>>> headers = new ArrayList<>();
        final List<RandomAccessFile> invertedFiles = new ArrayList<>();

        for(HeaderAndInvertedFile headerAndInverted : parts) {
            final IO<FileReader> headerReader = () -> new FileReader(headerAndInverted.headerFile);
            final SortedMap<String, ReversedIndexIdentifier> headerMap = headerReader.flatMap(serializer::unserializeHeader)
                    .attempt()
                    .ok()
                    .get();
            final List sortedHeader = headerMap.entrySet()
                    .stream()
                    .map(it -> new Pair(it))
                    .collect(Collectors.toList());
            headers.add(sortedHeader);

            final IO<RandomAccessFile> invertedFileAccess = () -> new RandomAccessFile(headerAndInverted.invertedFile, "r");
            invertedFiles.add(invertedFileAccess.attempt().ok().get());
        }
        return null;
    }

    public List<Integer> getIndicesOfMinimalTerm(
            List<List<Pair<String, ReversedIndexIdentifier>>> terms
    ) {
        String minimalTerm = terms.get(0).get(0).first;
        final List<Integer> indicesOfMinimalTerms = new ArrayList<>();
        for(int i = 0; i < terms.size(); i++) {
            final Pair<String, ReversedIndexIdentifier> firstTermEntry = terms.get(i).get(0);
            if (firstTermEntry.first.compareTo(minimalTerm) < 0) {
                indicesOfMinimalTerms.clear();
                minimalTerm = firstTermEntry.first;
                indicesOfMinimalTerms.add(i);
            } else if (firstTermEntry.first.compareTo(minimalTerm) == 0) {
                indicesOfMinimalTerms.add(i);
            }
        }
        return indicesOfMinimalTerms;
    }

    public List<Pair<String, ReversedIndexIdentifier>> getMinimals(
            List<Integer> indicesOfMinimalTerms,
            List<List<Pair<String, ReversedIndexIdentifier>>> identifiers
    ) {
        final List<Pair<String, ReversedIndexIdentifier>> result = new ArrayList<>();
        for(int i : indicesOfMinimalTerms) {
            result.add(identifiers.get(i).get(0));
        }
        return result;
    }

    public List<RandomAccessFile> getInvertedFileForMinimals(
            List<Integer> indicesOfMinimalTerms,
            List<RandomAccessFile> randomAccessFiles
    ) {
        final List<RandomAccessFile> result = new ArrayList<>();
        for(int i : indicesOfMinimalTerms) {
            result.add(randomAccessFiles.get(i));
        }
        return result;
    }

    public int doThing(
            int offset,
            List<Pair<String, ReversedIndexIdentifier>> terms,
            List<RandomAccessFile> invertedFiles
    ) {

        final String key = terms.get(0).first;
        int length = 0;
        for(Pair<String, ReversedIndexIdentifier> element : terms) {
            length += element.second.length;
        }
        final Pair<String, ReversedIndexIdentifier> aggregateTerm = new Pair(key, new ReversedIndexIdentifier(offset, length));
        
        return 0;
    }

    public void increments(
            List<Integer> indices,
            List<List<Pair<String, ReversedIndexIdentifier>>> terms,
            List<RandomAccessFile> invertedFiles
    ) {
        final List<Integer> emptyListIndices = new ArrayList<>();
        for(int i : indices) {
            terms.get(i).remove(0);
            if (terms.get(i).isEmpty()) {
                emptyListIndices.add(i);
            }
        }
        int offset = 0;
        for(int i : emptyListIndices) {
            terms.remove(i - offset);
            invertedFiles.remove(i - offset);
            offset += 1;
        }
    }


}