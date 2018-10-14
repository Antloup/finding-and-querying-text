package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.exception.InvalidInvertedFileException;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    public File serialize(InMemoryVocabularyImpl vocabulary) {
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
            File hfFile = new File(this.fileFolder + "/hf" + fileNumber);
            if (hfFile.createNewFile() && ifFile.createNewFile()) {
                BufferedWriter hfbw = new BufferedWriter(new FileWriter(hfFile));
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
                    hfbw.write(term + ":" + offset + ":" + length + "\r\n");
                    offset += length;
                    length = 0;
                }
                hfbw.close();
                ifbw.close();
            } else {
                throw new InvalidInvertedFileException("Files already exists");
            }
        } catch (IOException | InvalidInvertedFileException e) {
            e.printStackTrace();
        }

        return ifFile;
    }

    public InMemoryVocabularyImpl unserialize(String ifPath, String hfPath) {
        return this.unserialize(new File(this.fileFolder + "/" + ifPath), new File(this.fileFolder + "/" + hfPath));
    }

    public InMemoryVocabularyImpl unserialize(File ifFile, File hfFile) {
        return this.unserialize(ifFile, this.unserializeHeader(hfFile));
    }

    @Override
    public InMemoryVocabularyImpl unserialize(File file, Map<String, ReversedIndexIdentifier> header) {
        InMemoryVocabularyImpl vocabulary = new InMemoryVocabularyImpl();

        for (Map.Entry<String, ReversedIndexIdentifier> termHeader : header.entrySet()) {
            List<Entry> entries = this.unserializePostingList(file, termHeader.getValue().offset, termHeader.getValue().length);
            for (Entry entry : entries) {
                vocabulary.putEntry(termHeader.getKey(), entry);
            }
        }
        return vocabulary;
    }

    @Override
    public Map<String, ReversedIndexIdentifier> unserializeHeader(File file) {
        Map<String, ReversedIndexIdentifier> header = new TreeMap<>();
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(":");
                if (attributes.length != 3) {
                    throw new InvalidInvertedFileException("Invalid header file at line " + reader.getLineNumber());
                }
                header.put(attributes[0], new ReversedIndexIdentifier(Integer.valueOf(attributes[1]), Integer.valueOf(attributes[2])));
            }

            reader.close();
        } catch (IOException | InvalidInvertedFileException e) {
            e.printStackTrace();
        }
        return header;
    }

    @Override
    public List<Entry> unserializePostingList(File file, int postingListOffset, int postingListLength) {
        List<Entry> entries = new ArrayList<>();

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(postingListOffset);
            byte[] bytes = new byte[postingListLength];
            raf.read(bytes);
            String[] termPl = new String(bytes).split(";");

            for (String term : termPl) {
                if ("".equals(term)) {
                    break;
                }
                String[] score = term.split(":");
                if (score.length != 2) {
                    throw new InvalidInvertedFileException("Invalid inverted file between offset " + postingListOffset + " and " + (postingListOffset + postingListLength));
                }
                //TODO: Read Metadata and get ArticleId path
                entries.add(new Entry(new ArticleId(Integer.valueOf(score[0]), "TODO"), Float.valueOf(score[1])));
            }

            raf.close();
        } catch (IOException | InvalidInvertedFileException e) {
            e.printStackTrace();
        }

        return entries;
    }
}
