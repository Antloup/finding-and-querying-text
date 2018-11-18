package com.github.quadinsa5if.findingandqueryingtext.controller;

import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InDiskVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.NaiveSolverImpl;
import com.github.rloic.quadinsa5if.findindandqueryingtext.benchmark.Benchmark;
import com.github.rloic.quadinsa5if.findindandqueryingtext.service.implementation.FaginSolverImp;
import kotlin.Unit;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

public class BenchmarkAction {

    private final Benchmark benchmark = new Benchmark();
    private final StandardAction standardAction = new StandardAction();

    private static final File benchDataSetFolder = new File("benchmark/dataset");
    private static final HeaderAndInvertedFile benchInvertedFile = HeaderAndInvertedFile.autoSuffix("benchmark/FILE");

    public void run() {
        benchBuildInvertedIndex();
        benchNaiveAndFagin();
    }

    private void benchBuildInvertedIndex() {
        final List<Integer> batchSizes = Arrays.asList(100, 1_000, 10_000, 100_000);
        benchmark.bench(
                "build inverted index",
                "batch size",
                batchSizes,
                1,
                batchSize -> {
                    standardAction.buildInvertedFile(benchDataSetFolder, benchInvertedFile, batchSize).attempt();
                    return Unit.INSTANCE;
                }
        );
    }

    private void benchNaiveAndFagin() {
        final List<String[]> queries = Arrays.asList(
                new String[]{"fade"},

                new String[]{"hammer",
                        "baby"},

                new String[]{"invite",
                        "property",
                        "connection",
                        "grubby"},

                new String[]{"unfasten",
                        "table",
                        "imperfect",
                        "elated",
                        "furtive",
                        "unknown",
                        "important",
                        "strong"},

                new String[]{"cherries",
                        "minor",
                        "demonic",
                        "rabid",
                        "fail",
                        "competition",
                        "dogs",
                        "ripe",
                        "writing",
                        "toys",
                        "spark",
                        "drunk",
                        "sweater",
                        "force",
                        "shape",
                        "elated"},

                new String[]{"cute",
                        "disgusted",
                        "loaf",
                        "sleepy",
                        "careless",
                        "start",
                        "bottle",
                        "pies",
                        "well-to-do",
                        "grin",
                        "protest",
                        "alarm",
                        "error",
                        "hobbies",
                        "smoke",
                        "grain",
                        "wet",
                        "truculent",
                        "form",
                        "cheese",
                        "disapprove",
                        "fruit",
                        "kneel",
                        "explain",
                        "coat",
                        "reduce",
                        "hollow",
                        "afterthought",
                        "tense",
                        "chess",
                        "magic",
                        "alert"}
        );

        benchmark.bench(
                "naive solver",
                "terms",
                queries,
                10,
                terms -> {
                    runQuery(terms, 5, new NaiveSolverImpl());
                    return Unit.INSTANCE;
                });

        benchmark.bench(
                "fagin solver",
                "terms",
                queries,
                10,
                terms -> {
                    runQuery(terms, 5, new FaginSolverImp());
                    return Unit.INSTANCE;
                });

        List<Integer> ks = Arrays.asList(2, 4, 8, 16, 32);
        benchmark.bench(
                "naive solver",
                "k",
                ks,
                100,
                k -> {
                    runQuery(new String[]{"apple", "microsoft"}, k, new NaiveSolverImpl());
                    return Unit.INSTANCE;
                });

        benchmark.bench(
                "fagin solver",
                "k",
                ks,
                100,
                k -> {
                    runQuery(new String[]{"apple", "microsoft"}, k, new FaginSolverImp());
                    return Unit.INSTANCE;
                });

    }

    private void runQuery(String[] terms, int k, QuerySolver solver) {
        try {
            Vocabulary voc = new InDiskVocabularyImpl(
                    new FileReader(benchInvertedFile.headerFile),
                    new RandomAccessFile(benchInvertedFile.invertedFile, "r"),
                    1_000
            );
            solver.answer(voc, terms, k);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
