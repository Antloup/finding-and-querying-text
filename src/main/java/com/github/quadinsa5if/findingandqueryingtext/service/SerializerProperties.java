package com.github.quadinsa5if.findingandqueryingtext.service;

public abstract class SerializerProperties {

    public SerializerProperties() {
    }

    /**
     * @param fileFolder : path of the folder (without '/' at the end)
     */
    public SerializerProperties(String fileFolder) {
        this.fileFolder = fileFolder;
    }

    public static final String METADATA_FILE = "mf";
    public static final String INVERTED_FILE = "if";
    public static final String HEADER_FILE = "hf";
    protected String fileFolder = "fileTest/novb";

    public static final char PARTS_DELIMITER = ':';
    public static final char IDENTIFIERS_DELIMITER = ';';
    public static final String NEW_LINE = "\r\n";
}
