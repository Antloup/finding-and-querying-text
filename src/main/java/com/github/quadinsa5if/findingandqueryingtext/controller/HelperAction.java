package com.github.quadinsa5if.findingandqueryingtext.controller;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.jetbrains.annotations.NotNull;

public class HelperAction {

    private final HelpFormatter helpFormatter = new HelpFormatter();

    public void run(@NotNull Options options) {
        helpFormatter.printHelp("", options);
    }

}
