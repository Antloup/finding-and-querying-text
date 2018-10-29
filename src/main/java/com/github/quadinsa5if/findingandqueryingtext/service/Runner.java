package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.controller.BenchmarkAction;
import com.github.quadinsa5if.findingandqueryingtext.controller.HelperAction;
import com.github.quadinsa5if.findingandqueryingtext.controller.StandardAction;
import com.github.quadinsa5if.findingandqueryingtext.controller.TestAction;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.lang.Unit;
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InDiskVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.*;
import com.github.quadinsa5if.findingandqueryingtext.tokenizer.DocumentParser;
import com.github.quadinsa5if.findingandqueryingtext.util.Arguments;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;
import com.github.rloic.quadinsa5if.findindandqueryingtext.service.implementation.FaginSolverImp;
import com.github.rloic.quadinsa5if.findindandqueryingtext.service.implementation.InvertedFileMergerImpl;
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

import static com.github.quadinsa5if.findingandqueryingtext.util.Arguments.*;

public class Runner {

    enum MainAction {
        STANDARD,
        BENCHMARK,
        TESTS,
        PRINT_HELP
    }

    private final StandardAction standardAction = new StandardAction();
    private final TestAction testAction = new TestAction();
    private final BenchmarkAction benchmarkAction = new BenchmarkAction();
    private final HelperAction helperAction = new HelperAction();

    public Runner() {}

    public void run(@NotNull CommandLine cl, @NotNull Options options) {
        MainAction action = getMainAction(cl);

        switch (action) {
            case STANDARD:
                standardAction.run(cl);
                break;
            case TESTS:
                break;
            case BENCHMARK:
                break;
            case PRINT_HELP:
                helperAction.run(options);
                break;
        }
    }

    private static MainAction getMainAction(@NotNull CommandLine cl) {
        MainAction action;
        if (cl.hasOption(EXECUTE_BENCHMARK.full)) {
            action = MainAction.BENCHMARK;
        } else if (cl.hasOption(EXECUTE_TESTS.full)) {
            action = MainAction.TESTS;
        } else if (cl.hasOption(PRINT_HELP.full)) {
            action = MainAction.PRINT_HELP;
        } else {
            action = MainAction.STANDARD;
        }
        return action;
    }

}
