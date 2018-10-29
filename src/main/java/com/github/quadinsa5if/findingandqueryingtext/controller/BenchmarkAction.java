package com.github.quadinsa5if.findingandqueryingtext.controller;

import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.rloic.quadinsa5if.findindandqueryingtext.benchmark.Benchmark;
import kotlin.Unit;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BenchmarkAction {

    private final Benchmark benchmark = new Benchmark();
    private final StandardAction standardAction = new StandardAction();

    private static final File benchDataSetFolder = new File("benchmark/dataset");
    private static final HeaderAndInvertedFile benchInvertedFile = HeaderAndInvertedFile.autoSuffix("benchmark/FILE");

    public void run() {
        benchBuildInvertedIndex();
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


}
