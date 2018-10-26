package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.*;
import com.github.quadinsa5if.findingandqueryingtext.service.MetadataSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.Serializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class MetadataSerializerImplementation extends Serializer implements MetadataSerializer {

    public MetadataSerializerImplementation() {
    }

    public MetadataSerializerImplementation(String fileFolder) {
        super(fileFolder);
    }

    @Override
    public Result<File, Exception> serialize(List<ArticleHeader> metadata) {
        int fileNumber = 0;
        final File directory = new File(fileFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File mfFile = new File(fileFolder + METADATA_FILE + fileNumber);
        while (mfFile.exists()) {
            fileNumber += 1;
            mfFile = new File(fileFolder + METADATA_FILE + fileNumber);
        }
        final File mfValidFile = mfFile;
        return Result.Try(() -> {
            StringBuilder strBuilder = new StringBuilder();
            if (mfValidFile.createNewFile()) {
                BufferedWriter mfbw = Files.newBufferedWriter(mfValidFile.toPath());
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
            return mfValidFile;
        });
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
