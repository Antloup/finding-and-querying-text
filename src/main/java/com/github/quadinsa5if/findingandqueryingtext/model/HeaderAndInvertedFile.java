package com.github.quadinsa5if.findingandqueryingtext.model;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Supplier;

public class HeaderAndInvertedFile {

    public static final String HEADER_SUFFIX = "_header";
    public static final String POSTING_LIST_SUFFIX = "_posting_list";

    public final File headerFile;
    public final File invertedFile;

    public HeaderAndInvertedFile(
            @NotNull File headerFile,
            @NotNull File invertedFile
    ) {
        this.headerFile = headerFile;
        this.invertedFile = invertedFile;
    }


    public static HeaderAndInvertedFile of(
            @NotNull String filePath,
            @NotNull String headerSuffix,
            @NotNull String postingListSuffix
    ) {
        assertNot(() -> isBlankOrEmpty(filePath), "file path must be not blank");
        assertNot(() -> isBlankOrEmpty(headerSuffix), "header suffix must be not blank");
        assertNot(() -> isBlankOrEmpty(postingListSuffix), "posting list suffix must be not blank");
        assertNot(() -> headerSuffix.equals(postingListSuffix), "header suffix and posting list suffix must be different");

        final File headerFile = new File(filePath + headerSuffix);
        final File invertedFile = new File(filePath + postingListSuffix);

        return new HeaderAndInvertedFile(headerFile, invertedFile);
    }

    public static HeaderAndInvertedFile autoSuffix(
            @NotNull String filePath
    ) {
        assertNot(() -> isBlankOrEmpty(filePath), "file path must be not blank");

        final File headerFile = new File(filePath + HEADER_SUFFIX);
        final File invertedFile = new File(filePath + POSTING_LIST_SUFFIX);

        return new HeaderAndInvertedFile(headerFile, invertedFile);
    }

    private static void assertThat(@NotNull Supplier<Boolean> predicate, @NotNull String message) {
        if(!predicate.get()) {
            throw new RuntimeException("Assertion error: " + message);
        }
    }

    private static void assertNot(@NotNull Supplier<Boolean> predicate, @NotNull String message) {
        if(predicate.get()) {
            throw new RuntimeException("Assertion error: " + message);
        }
    }

    private static boolean isBlankOrEmpty(@NotNull String str) {

        if (str.isEmpty()) {
            return true;
        } else {
            for(int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (!Character.isWhitespace(c)) {
                    return false;
                }
            }
        }
        return true;

    }

}
