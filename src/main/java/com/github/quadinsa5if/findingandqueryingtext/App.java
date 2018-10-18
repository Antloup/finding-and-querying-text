package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.IdfTfScorerImplementation;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.AbstractScorerImplementation;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.InvertedFileSerializerImplementation;
import org.apache.commons.cli.*;

import java.io.File;

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
            final File dataFolder = new File(commandLine.getOptionValue(articleFolder.getOpt()));
            final File outputFileName = new File(commandLine.getOptionValue(ouputFile.getOpt()));

            final boolean buildIndex = commandLine.hasOption(buildInvertedFile.getOpt());
            final boolean runBenchmarks = commandLine.hasOption(benchmark.getOpt());
            final boolean hasQuery = commandLine.hasOption(query.getOpt());
            final boolean runTests = commandLine.hasOption(test.getOpt());

            if (buildIndex) {
                buildInvertedFile(dataFolder, outputFileName);
            }
            if (hasQuery) {
                // Todo: perform query
            }
            if (runTests) {
                // Todo: test build (gradle)
            }


        } catch (ParseException e) {
            helpFormatter.printHelp("java -jar ./app.jar -f folder -o inverted_file [opts]", options);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void buildInvertedFile(File articlesFolder, File outputFile) {
        AbstractScorerImplementation scorer = new IdfTfScorerImplementation(articlesFolder, new InvertedFileSerializerImplementation());
        scorer.evaluate(1);
        Vocabulary vocabulary = scorer.getVocabulary();
    }

}
