package com.github.quadinsa5if.findingandqueryingtext.model;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class HeaderAndInvertedFile {

    public final File headerFile;
    public final File invertedFile;

    public HeaderAndInvertedFile(
            @NotNull File headerFile,
            @NotNull File invertedFile
    ) {
        this.headerFile = headerFile;
        this.invertedFile = invertedFile;
    }

}
