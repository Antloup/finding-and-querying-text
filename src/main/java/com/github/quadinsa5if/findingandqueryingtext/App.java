package com.github.quadinsa5if.findingandqueryingtext;

import com.github.quadinsa5if.findingandqueryingtext.service.Runner;
import org.apache.commons.cli.*;

public class App {

    public static void main(String[] args) {


        final HelpFormatter helpFormatter = new HelpFormatter();
        final CommandLineParser commandLineParser = new DefaultParser();
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

        final Option synonym = Option.builder("s")
                .longOpt("synonym")
                .hasArg(false)
                .desc("add synonyms")
                .build();

        options.addOption(articleFolder)
                .addOption(ouputFile)
                .addOption(query)
                .addOption(buildInvertedFile)
                .addOption(benchmark)
                .addOption(test)
                .addOption(help)
                .addOption(synonym);
        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            Thread t = new Thread(new Runner(commandLine,options));
            t.start();
            synchronized (t) {
                t.wait();
            }

        } catch (ParseException e) {
            helpFormatter.printHelp("java -jar ./app.jar -f folder -o inverted_file [opts]", options);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


}
