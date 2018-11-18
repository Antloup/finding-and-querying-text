package com.github.quadinsa5if.findingandqueryingtext.controller;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InDiskVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileMerger;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.MetadataSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.*;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances.CosinusSimilarity;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.DocumentParser;
import com.github.quadinsa5if.findingandqueryingtext.util.NaiveCompressor;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;
import com.github.rloic.quadinsa5if.findindandqueryingtext.service.implementation.FaginSolverImp;
import com.github.rloic.quadinsa5if.findindandqueryingtext.service.implementation.InvertedFileMergerImpl;
import org.apache.commons.cli.CommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

import static com.github.quadinsa5if.findingandqueryingtext.util.Arguments.*;

public class StandardAction {

    private final InvertedFileSerializer serializer = new InvertedFileSerializerImplementation(new NaiveCompressor());
    private final MetadataSerializer metadataSerializer = new MetadataSerializerImplementation();

    public void run(@NotNull CommandLine cl) {

        boolean mustBuildInvertedFile = cl.hasOption(BUILD_INVERTED_INDEX.full);
        final String dataSetFolderPath = cl.getOptionValue(ARTICLES_FOLDER.full);
        final File dataSetFolder = getDataSetFolder(dataSetFolderPath);

        final HeaderAndInvertedFile invertedFile;
        final HeaderAndInvertedFile outputFile = HeaderAndInvertedFile.autoSuffix(cl.getOptionValue(OUTPUT_FILE.full));

        SemanticEnhancer semanticEnhancer = new SemanticEnhancer(new CosinusSimilarity());

        if (mustBuildInvertedFile) {
            final Pair<HeaderAndInvertedFile, RandomIndexerImplementation> resultAfterBuilding =
                    buildInvertedFile(dataSetFolder, outputFile, 1000)
                    .attempt()
                    .expect("Something did wrong when building the inverted files");
            invertedFile = resultAfterBuilding.first;
            RandomIndexerImplementation randomIndexer = resultAfterBuilding.second;
            semanticEnhancer.loadAndSaveContextVectorsToFile(randomIndexer.getContextVectors(), CONTEXT_VECTORS_FILE.full);
            System.out.println("Building inverted file done...");
        } else {
            semanticEnhancer.loadContextVectorsFromFile(CONTEXT_VECTORS_FILE.full);
            invertedFile = outputFile;
        }

        final FileReader headerFile = readFile(invertedFile.headerFile)
                .expect("Cannot read header file " + invertedFile.headerFile);
        final RandomAccessFile postingListFile = readFileRandom(invertedFile.invertedFile)
                .expect("Cannot read posting list file " + invertedFile.invertedFile);

        Vocabulary voc = new InDiskVocabularyImpl(headerFile, postingListFile, 10);
        QuerySolver querySolver = new FaginSolverImp();

        final Pair<Integer, String[]> parsedArguments = parseQueryArguments(cl)
                .expect("Invalid query arguments, the arguments must be like k:T1:T2:...:Tn where k is an int and T1...Tn the terms");

        int k = parsedArguments.first;
        String[] terms = parsedArguments.second;

        terms = semanticEnhancer.enhanceTerms(terms, 5);

        System.out.println("Enhanced query");

        for(String t : terms) {
            System.out.println(t);
        }

        Iter<Integer> answer = querySolver.answer(voc, terms, k);
        for (int i : answer) {
            System.out.println("Article " + i);
        }
        System.out.println("End");

    }

    public IO<Pair<HeaderAndInvertedFile, RandomIndexerImplementation>> buildInvertedFile(
            File dataSetFolder,
            HeaderAndInvertedFile outputFile,
            int batchSize
    ) {
        ScorerImplementation scorerVisitor = new ScorerImplementation(serializer, batchSize);
        MetadataImplementation metadataVisitor = new MetadataImplementation(metadataSerializer);
        RandomIndexerImplementation randomIndexerVisitor = new RandomIndexerImplementation();

        DocumentParser parser = new DocumentParser(Arrays.asList(scorerVisitor, metadataVisitor, randomIndexerVisitor));
        parser.parse(dataSetFolder.listFiles());

        List<HeaderAndInvertedFile> partitions = scorerVisitor.getPartitions();
        final InvertedFileMerger merger = new InvertedFileMergerImpl(serializer);
        return merger.merge(partitions, outputFile).map( hfFile -> new Pair(hfFile, randomIndexerVisitor));
    }

    private File getDataSetFolder(@NotNull String path) {
        final File dataSetFolder = new File(path);
        if (!dataSetFolder.exists()) {
            throw new RuntimeException("Dataset folder not found " + path);
        } else if (!dataSetFolder.isDirectory()) {
            throw new RuntimeException(path + " is not a directory");
        }
        return dataSetFolder;
    }

    private Result<FileReader, FileNotFoundException> readFile(@NotNull File file) {
        try {
            return Result.ok(new FileReader(file));
        } catch (FileNotFoundException e) {
            return Result.err(e);
        }
    }

    private Result<RandomAccessFile, FileNotFoundException> readFileRandom(@NotNull File file) {
        try {
            return Result.ok(new RandomAccessFile(file, "r"));
        } catch (FileNotFoundException e) {
            return Result.err(e);
        }
    }

    private Result<Pair<Integer, String[]>, Exception> parseQueryArguments(
            @NotNull CommandLine cl
    ) {
        final Result<Pair<Integer, String[]>, Exception> error = Result.err(
                new RuntimeException("Invalid query arguments, the arguments must be like k:T1:T2:...:Tn where k is an int and T1...Tn the terms")
        );

        String[] queryArguments = cl.getOptionValues(PERFORM_QUERY.full);
        if (queryArguments.length < 2) {
            return error;
        } else {
            int k;
            try {
                k = Integer.valueOf(queryArguments[0]);
            } catch (NumberFormatException n) {
                return error;
            }
            String[] terms = new String[queryArguments.length - 1];
            System.arraycopy(queryArguments, 1, terms, 0, queryArguments.length - 1);
            return Result.ok(new Pair<>(k, terms));
        }

    }
}
