package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Unit;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InDiskVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileMerger;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.*;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.DocumentParser;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) {
        final Options options = new Options();

        final Option articleFolder = Option.builder("f")
                .longOpt("article_folder")
                .hasArg(true)
                .argName("article folder")
                .desc("the article folder")
                .required()
                .build();

        final Option ouputFile = Option.builder("o")
                .longOpt("output_file")
                .hasArg(true)
                .argName("output file")
                .desc("the inverted index ouput file")
                .required()
                .build();

        final Option buildInvertedFile = Option.builder("i")
                .longOpt("inverted")
                .hasArg(false)
                .desc("build the inverted index file")
                .build();

        final Option query = Option.builder("q")
                .longOpt("query")
                .hasArg(true)
                .argName("terms")
                .desc("terms query")
                .valueSeparator(':')
                .build();

        final Option benchmark = Option.builder("b")
                .longOpt("benchmark")
                .hasArg(false)
                .desc("run the benchmarks")
                .build();

        final Option test = Option.builder("t")
                .longOpt("test")
                .hasArg(false)
                .desc("run the tests")
                .build();

        final Option help = Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("display the help")
                .build();

        options
                .addOption(articleFolder)
                .addOption(ouputFile)
                .addOption(query)
                .addOption(buildInvertedFile)
                .addOption(benchmark)
                .addOption(test)
                .addOption(help);

        final HelpFormatter helpFormatter = new HelpFormatter();
        final CommandLineParser commandLineParser = new DefaultParser();
        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            final IO<Stream<Path>> dataFolder = listFiles(commandLine.getOptionValue(articleFolder.getOpt()));
            final String outputFileName = commandLine.getOptionValue(ouputFile.getOpt());
            final File outputFile = new File(outputFileName);

            final boolean buildIndex = commandLine.hasOption(buildInvertedFile.getOpt());
            final boolean runBenchmarks = commandLine.hasOption(benchmark.getOpt());
            final boolean hasQuery = commandLine.hasOption(query.getOpt());
            final boolean runTests = commandLine.hasOption(test.getOpt());

            if (buildIndex) {
                dataFolder.map(folder -> {
                    buildInvertedFile(folder, outputFile);
                    return new Unit();
                }).sync();
            }
            if (hasQuery) {
                String[] termsAndK = commandLine.getOptionValues(query.getOpt());
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


        } catch (ParseException e) {
            helpFormatter.printHelp("java -jar ./app.jar -f folder -o inverted_file [opts]", options);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static HeaderAndInvertedFile buildInvertedFile(Stream<Path> articlesFolder, File outputFile) {
        final InvertedFileSerializer serializer = new InvertedFileSerializerImplementation();

        ScorerImplementation scorerVisitor = new ScorerImplementation(serializer, 10);
        MetadataImplementation metadataVisitor = new MetadataImplementation();

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
