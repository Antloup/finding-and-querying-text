package com.github.quadinsa5if.findingandqueryingtext.exception;

public class InvalidInvertedFileException extends Exception {
    public InvalidInvertedFileException(String message) {
        super("Inverted file exception : " + message);
    }
}
