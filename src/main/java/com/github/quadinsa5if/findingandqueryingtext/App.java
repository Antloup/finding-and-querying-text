package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InDiskVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileMerger;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.*;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
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
            final File outputFileName = new File(commandLine.getOptionValue(ouputFile.getOpt()));

            final boolean buildIndex = commandLine.hasOption(buildInvertedFile.getOpt());
            final boolean runBenchmarks = commandLine.hasOption(benchmark.getOpt());
            final boolean hasQuery = commandLine.hasOption(query.getOpt());
            final boolean runTests = commandLine.hasOption(test.getOpt());

            if (buildIndex) {
                dataFolder.map(folder -> {
                    buildInvertedFile(folder, outputFileName);
                    return 0;
                }).sync();
            }
            if (hasQuery) {
                System.out.println("Running query");
                // Todo: perform query
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
        AbstractScorerImplementation scorer = new IdfTfScorerImplementation(articlesFolder, serializer);
        List<HeaderAndInvertedFile> partitions = scorer.evaluate(1);
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
