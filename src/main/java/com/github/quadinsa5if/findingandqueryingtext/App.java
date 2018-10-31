package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.service.Runner;
import org.apache.commons.cli.*;

import static com.github.quadinsa5if.findingandqueryingtext.util.Arguments.*;

public class App {

    public static void main(String[] args) {
        final Options options = new Options();
        final CommandLineParser commandLineParser = new DefaultParser();
        final Runner applicationRunner = new Runner();
        final HelpFormatter helpFormatter = new HelpFormatter();

        final Option articleFolder = Option.builder(ARTICLES_FOLDER.abbreviate)
                .longOpt(ARTICLES_FOLDER.full)
                .hasArg(true)
                .argName("article folder")
                .desc("the article folder")
                .build();

        final Option outputFile = Option.builder(OUTPUT_FILE.abbreviate)
                .longOpt(OUTPUT_FILE.full)
                .hasArg(true)
                .argName("output file")
                .desc("the inverted index ouput file")
                .build();

        final Option buildInvertedFile = Option.builder(BUILD_INVERTED_INDEX.abbreviate)
                .longOpt(BUILD_INVERTED_INDEX.full)
                .hasArg(false)
                .desc("build the inverted index file")
                .build();

        final Option query = Option.builder(PERFORM_QUERY.abbreviate)
                .longOpt(PERFORM_QUERY.full)
                .hasArgs()
                .argName("terms")
                .desc("terms query")
                .valueSeparator(':')
                .build();

        final Option benchmark = Option.builder(EXECUTE_BENCHMARK.abbreviate)
                .longOpt(EXECUTE_BENCHMARK.full)
                .hasArg(false)
                .desc("run the benchmarks")
                .build();

        final Option test = Option.builder(EXECUTE_TESTS.abbreviate)
                .longOpt(EXECUTE_TESTS.full)
                .hasArg(false)
                .desc("run the tests")
                .build();

        final Option help = Option.builder(PRINT_HELP.abbreviate)
                .longOpt(PRINT_HELP.full)
                .hasArg(false)
                .desc("display the help")
                .build();

        final Option synonym = Option.builder(IMPROVE_QUERY.abbreviate)
                .longOpt(IMPROVE_QUERY.full)
                .hasArg(false)
                .desc("add synonyms")
                .build();

        options
                .addOption(articleFolder)
                .addOption(outputFile)
                .addOption(query)
                .addOption(buildInvertedFile)
                .addOption(benchmark)
                .addOption(test)
                .addOption(help)
                .addOption(synonym);

        try {
            applicationRunner.run(commandLineParser.parse(options, args), options);
        } catch (ParseException e) {
            helpFormatter.printHelp("java -jar ./app.jar -f folder -o inverted_file [opts]", options);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

}