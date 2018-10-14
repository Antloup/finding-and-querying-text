package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InvertedFileSerializerImplementation implements InvertedFileSerializer {

    private String fileFolder = "fileTest/novb";

    public InvertedFileSerializerImplementation() {
    }

    /**
     * @param fileFolder : path of the folder (without '/' at the end)
     */
    public InvertedFileSerializerImplementation(String fileFolder) {
        this.fileFolder = fileFolder;
    }

    @Override
    public File serialize(Vocabulary vocabulary) {
        int fileNumber = 0;
        boolean exists = true;
        File ifFile = null;
        while (exists) {
            fileNumber++;
            ifFile = new File(this.fileFolder + "/if" + fileNumber);
            exists = ifFile.exists();
        }
        System.out.println("File path:" + ifFile.getPath());
        try {
            //TODO : Creating/filling metadata file
            File ofFile = new File(this.fileFolder + "/of" + fileNumber);
            if (ofFile.createNewFile() && ifFile.createNewFile()) {
                BufferedWriter ofbw = new BufferedWriter(new FileWriter(ofFile));
                BufferedWriter ifbw = new BufferedWriter(new FileWriter(ifFile));
                int offset = 0;
                int length = 0;
                for (String term : vocabulary.data.keySet()) {
                    System.out.println(term);
                    for (Entry entry : vocabulary.data.get(term)) {
                        System.out.println(entry);
                        String e = entry.articleId.id + ":" + entry.score + ";";
                        length += e.length();
                        ifbw.write(e);
                    }
                    ofbw.write(term + ":" + offset + ":" + length + "\r\n");
                    offset += length;
                    length = 0;
                }
                ofbw.close();
                ifbw.close();
            } else {
                throw new InvalidInvertedFileException("Files already exists");
            }
        } catch (IOException | InvalidInvertedFileException e) {
            e.printStackTrace();
        }

        return ifFile;
    }

    public Vocabulary unserialize(String scoreFile, String termFile) {
        return this.unserialize(new File(this.fileFolder + "/" + scoreFile), new File(this.fileFolder + "/" + termFile));
    }

    @Override
    public Vocabulary unserialize(File scoreFile, File termFile) {
        List<ReversedIndexIdentifier> termsOffset = this.buildOffsetTerms(termFile);
        Vocabulary vocabulary = new Vocabulary();

        try {
            RandomAccessFile raf = new RandomAccessFile(scoreFile, "r");

            for (ReversedIndexIdentifier termOffset : termsOffset) {
                raf.seek(termOffset.offset);
                byte[] bytes = new byte[termOffset.length];
                raf.read(bytes);
                String[] termPl = new String(bytes).split(";");

                for (String term : termPl) {
                    if ("".equals(term)) {
                        break;
                    }
                    String[] score = term.split(":");
                    if (score.length != 2) {
                        throw new InvalidInvertedFileException("Invalid inverted file between offset " + termOffset.offset + " and " + (termOffset.offset + termOffset.length));
                    }
                    //TODO: Read Metadata and get ArticleId path
                    Entry entry = new Entry(new ArticleId(Integer.valueOf(score[0]), "TODO"), Float.valueOf(score[1]));
                    vocabulary.putEntry(termOffset.term, entry);
                }
            }

            raf.close();
        } catch (IOException | InvalidInvertedFileException e) {
            e.printStackTrace();
        }

        return vocabulary;
    }

    private List<ReversedIndexIdentifier> buildOffsetTerms(File file) {
        List<ReversedIndexIdentifier> termsOffset = new ArrayList<>();
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(":");
                if (attributes.length != 3) {
                    throw new InvalidInvertedFileException("Invalid offset file at line " + reader.getLineNumber());
                }
                termsOffset.add(new ReversedIndexIdentifier(attributes[0], Integer.valueOf(attributes[1]), Integer.valueOf(attributes[2])));
            }

            reader.close();
        } catch (IOException | InvalidInvertedFileException e) {
            e.printStackTrace();
        }
        return termsOffset;
    }
}
