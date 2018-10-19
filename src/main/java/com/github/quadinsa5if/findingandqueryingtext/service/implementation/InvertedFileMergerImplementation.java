package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileMerger;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class InvertedFileMergerImplementation implements InvertedFileMerger {

    private final InvertedFileSerializer serializer;

    public InvertedFileMergerImplementation(InvertedFileSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public HeaderAndInvertedFile merge(Iterable<HeaderAndInvertedFile> parts, HeaderAndInvertedFile outputFiles) {
        final IO<HeaderAndInvertedFile> ioWork = () -> {
            final List<List<Pair<String, ReversedIndexIdentifier>>> headers = new ArrayList<>();
            final List<RandomAccessFile> invertedFiles = new ArrayList<>();

            for (HeaderAndInvertedFile headerAndInverted : parts) {
                final SortedMap<String, ReversedIndexIdentifier> headerMap = serializer
                        .unserializeHeader(new FileReader(headerAndInverted.headerFile))
                        .sync();
                final List sortedHeader = headerMap.entrySet()
                        .stream()
                        .map(it -> new Pair(it))
                        .collect(Collectors.toList());
                headers.add(sortedHeader);
                invertedFiles.add(new RandomAccessFile(headerAndInverted.invertedFile, "r"));
            }
            // Todo: change to real result
            return null;
        };
        return ioWork.attempt().ok().get();
    }

    public List<Integer> getIndicesOfMinimalTerm(
            List<List<Pair<String, ReversedIndexIdentifier>>> terms
    ) {
        String minimalTerm = terms.get(0).get(0).first;
        final List<Integer> indicesOfMinimalTerms = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
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
        for (int i : indicesOfMinimalTerms) {
            result.add(identifiers.get(i).get(0));
        }
        return result;
    }

    public List<RandomAccessFile> getInvertedFileForMinimals(
            List<Integer> indicesOfMinimalTerms,
            List<RandomAccessFile> randomAccessFiles
    ) {
        final List<RandomAccessFile> result = new ArrayList<>();
        for (int i : indicesOfMinimalTerms) {
            result.add(randomAccessFiles.get(i));
        }
        return result;
    }

    public IO<Integer> doThing(
            int offset,
            List<Pair<String, ReversedIndexIdentifier>> terms,
            List<RandomAccessFile> invertedFiles,
            FileWriter outputHeaderFile,
            FileWriter outputInvertedFile
    ) {
        return () -> {
            final String key = terms.get(0).first;
            int length = 0;
            for (Pair<String, ReversedIndexIdentifier> element : terms) {
                length += element.second.length;
            }
            final Pair<String, ReversedIndexIdentifier> aggregateTerm = new Pair(key, new ReversedIndexIdentifier(offset, length));
            final IO<List<Entry>> recomposedPostingList = getParts(terms.stream().map(it -> it.second).collect(Collectors.toList()), invertedFiles)
                    .map(it -> it.stream().sorted((a, b) -> Float.compare(a.score, b.score)).collect(Collectors.toList()));
            recomposedPostingList.map(it -> serializer.writeEntries(it, new BufferedWriter(outputInvertedFile))).sync();
            serializer.writeReversedIndexIdentifier(Arrays.asList(aggregateTerm), new BufferedWriter(outputHeaderFile)).sync();
            return offset + length;
        };

    }

    public void increments(
            List<Integer> indices,
            List<List<Pair<String, ReversedIndexIdentifier>>> terms,
            List<RandomAccessFile> invertedFiles
    ) {
        final List<Integer> emptyListIndices = new ArrayList<>();
        for (int i : indices) {
            terms.get(i).remove(0);
            if (terms.get(i).isEmpty()) {
                emptyListIndices.add(i);
            }
        }
        int offset = 0;
        for (int i : emptyListIndices) {
            terms.remove(i - offset);
            invertedFiles.remove(i - offset);
            offset += 1;
        }
    }

    public IO<List<Entry>> getParts(
            List<ReversedIndexIdentifier> partsIdentifiers,
            List<RandomAccessFile> files
    ) {
        return () -> {
            final List<Entry> result = new ArrayList<>();
            for (int i = 0; i < partsIdentifiers.size(); i++) {
                final ReversedIndexIdentifier indexIdentifier = partsIdentifiers.get(i);
                final RandomAccessFile file = files.get(i);
                result.addAll(serializer.unserializePostingList(file, indexIdentifier.offset, indexIdentifier.length).sync());
            }
            return result;
        };

    }


}