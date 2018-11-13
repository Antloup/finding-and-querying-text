package com.github.quadinsa5if.findingandqueryingtext.util;

import org.jetbrains.annotations.NotNull;

public enum Arguments {

    ARTICLES_FOLDER("f", "article_folder"),
    OUTPUT_FILE("o", "output_file"),
    BUILD_INVERTED_INDEX("i", "inverted"),
    PERFORM_QUERY("q", "query"),
    EXECUTE_TESTS("t", "test"),
    EXECUTE_BENCHMARK("b", "benchmark"),
    PRINT_HELP("h", "help"),
    IMPROVE_QUERY("s", "synonym"),
    CONTEXT_VECTORS_FILE("v", "context_vectors_file");

    public final String abbreviate;
    public final String full;

    Arguments(@NotNull String abbreviate, @NotNull String full) {
        this.abbreviate = abbreviate;
        this.full = full;
    }

}
