package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleHeader;
import com.github.quadinsa5if.findingandqueryingtext.service.MetadataSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.SerializerProperties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetadataSerializerImplementation extends SerializerProperties implements MetadataSerializer {

    public MetadataSerializerImplementation() {
    }

    public MetadataSerializerImplementation(String fileFolder) {
        super(fileFolder);
    }

    @Override
    public IO<File> serialize(List<ArticleHeader> metadata, File output) {
        return () -> {
            StringBuilder strBuilder = new StringBuilder();
            if (output.createNewFile()) {
                BufferedWriter mfbw = Files.newBufferedWriter(output.toPath());
                for (ArticleHeader meta : metadata) {
                    strBuilder.setLength(0);
                    strBuilder.append(meta.id)
                            .append(PARTS_DELIMITER)
                            .append(meta.path)
                            .append(NEW_LINE);
                    String e = strBuilder.toString();
                    mfbw.write(e);
                }
                mfbw.close();
            } else {
                throw new InvalidInvertedFileException("Files already exists");
            }
            return output;
        };
    }

    @Override
    public IO<List<ArticleHeader>> unserialize(FileReader file) {
        return () -> {
            List<ArticleHeader> metadata = new ArrayList<>();
            String line;
            final LineNumberReader lineReader = new LineNumberReader(file);
            while ((line = lineReader.readLine()) != null) {
                String[] attributes = line.split(String.valueOf(PARTS_DELIMITER));
                if (attributes.length != 2) {
                    throw new InvalidInvertedFileException("Invalid metadata file at line " + lineReader.getLineNumber());
                }
                metadata.add(new ArticleHeader(Integer.valueOf(attributes[0]),attributes[1]));
            }
            return metadata;
        };
    }

    @Override
    public IO<Optional<ArticleHeader>> unserialize(FileReader file, int articleId) {
        return () -> {
            String line;
            final LineNumberReader lineReader = new LineNumberReader(file);
            while ((line = lineReader.readLine()) != null) {
                String[] attributes = line.split(String.valueOf(PARTS_DELIMITER));
                if (attributes.length != 2) {
                    throw new InvalidInvertedFileException("Invalid metadata file at line " + lineReader.getLineNumber());
                } else if(Integer.valueOf(attributes[0]) == articleId){
                    return Optional.of(new ArticleHeader(Integer.valueOf(attributes[0]),attributes[1]));
                }
            }
            return Optional.empty();
        };
    }

}
