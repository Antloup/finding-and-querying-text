package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Unit;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InDiskVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.*;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.DocumentParser;
import com.github.quadinsa5if.findingandqueryingtext.util.NaiveCompressor;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Runner implements Runnable{
    final CommandLine cl;
    final IO<Stream<Path>> dataFolder;
    final String outputFileName;
    final File outputFile;

    final boolean buildIndex;
    final boolean runBenchmarks;
    final boolean hasQuery;
    final boolean runTests;


    // Naming
    final String articleFolder = "article_folder";
    final String ouputFile = "output_file";
    final String buildInvertedFile = "inverted";
    final String benchmark = "query";
    final String query = "benchmark";
    final String test = "help";
    final String synonym = "synonym";


    public Runner(@NotNull CommandLine cl){
        this.cl = cl;

        dataFolder = listFiles(cl.getOptionValue(articleFolder));
        outputFileName = cl.getOptionValue(ouputFile);
        outputFile = new File(outputFileName);

        buildIndex = cl.hasOption(buildInvertedFile);
        runBenchmarks = cl.hasOption(benchmark);
        hasQuery = cl.hasOption(query);
        runTests = cl.hasOption(test);
    }

    public void run(){
        try{
            if (buildIndex) {
                dataFolder.map(folder -> {
                    buildInvertedFile(folder, outputFile);
                    return new Unit();
                }).sync();
            }

            if (hasQuery) {
                String[] termsAndK = cl.getOptionValues(query);
                int k = Integer.valueOf(termsAndK[0]);
                String[] terms = new String[termsAndK.length - 1];
                System.arraycopy(termsAndK, 1, terms, 0, termsAndK.length - 1);
                System.out.println("Running query");
                final FileReader header = open(outputFileName + "_header")
                        .expect("Unknown file " + outputFileName + "_header");
                final RandomAccessFile invertedIndex = openRandom(outputFileName + "_posting_lists")
                        .expect("Unknown file " + outputFileName + "_posting_lists");
                final Vocabulary inDiscVoc = new InDiskVocabularyImpl(header, invertedIndex, 10);
                final QuerySolver solver = new NativeSolverImpl(inDiscVoc);
                System.out.println(solver.answer(terms, k));
            }

            if (runTests) {
                System.out.println("Runnin tests");
                // Todo: test build (gradle)
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }




    private static HeaderAndInvertedFile buildInvertedFile(Stream<Path> articlesFolder, File outputFile) {
        final InvertedFileSerializer serializer = new InvertedFileSerializerImplementation(new NaiveCompressor());
        final MetadataSerializerImplementation metadataSerializer = new MetadataSerializerImplementation();

        ScorerImplementation scorerVisitor = new ScorerImplementation(serializer, 10);
        MetadataImplementation metadataVisitor = new MetadataImplementation(metadataSerializer);

        DocumentParser parser = new DocumentParser(Arrays.asList(scorerVisitor, metadataVisitor));

        File datasetFolder = new File("data");
        parser.parse(datasetFolder.listFiles());

        List<HeaderAndInvertedFile> partitions = scorerVisitor.getPartitions();
        final InvertedFileMerger merger = new InvertedFileMergerImplementation(serializer);
        final HeaderAndInvertedFile complete = merger.merge(partitions, new HeaderAndInvertedFile(new File(outputFile + "_header"), new File(outputFile + "_posting_lists")));
        return complete;
    }

    public static void dev() {

    }

    static Result<FileReader, FileNotFoundException> open(String filePath) {
        try {
            return Result.ok(new FileReader(new File(filePath)));
        } catch (FileNotFoundException e) {
            return Result.err(e);
        }
    }

    static Result<RandomAccessFile, FileNotFoundException> openRandom(String filePath) {
        try {
            return Result.ok(new RandomAccessFile(new File(filePath), "r"));
        } catch (FileNotFoundException e) {
            return Result.err(e);
        }
    }

    static IO<Stream<Path>> listFiles(String path) {
        return () -> Files.list(Paths.get(path));
    }
}
