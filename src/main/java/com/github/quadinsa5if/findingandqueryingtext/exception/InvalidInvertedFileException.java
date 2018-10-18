package com.github.quadinsa5if.findingandqueryingtext.exception;

import java.io.IOException;

public class InvalidInvertedFileException extends IOException {
    public InvalidInvertedFileException(String message) {
        super("Inverted file exception : " + message);
    }
}
