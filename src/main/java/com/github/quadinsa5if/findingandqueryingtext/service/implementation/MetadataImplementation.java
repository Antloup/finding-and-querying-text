package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleHeader;
import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;
import com.github.quadinsa5if.findingandqueryingtext.service.MetadataSerializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetadataImplementation implements DatasetVisitor {

    private String currentPath;
    private MetadataSerializer serializer;
    private List<ArticleHeader> headers;
    private List<File> headerFiles;

    public MetadataImplementation(MetadataSerializer serializer) {
        currentPath = "";
        this.serializer = serializer;
        headers = new ArrayList<>();
        headerFiles = new ArrayList<>();
    }

    @Override
    public int getTotalPassNumber() {
        return 1;
    }

    @Override
    public void onOpeningArticle(int articleId, int currentPassNumber) {
        headers.add(new ArticleHeader(articleId, currentPath));
    }

    @Override
    public void onTermRead(String term, int currentPassNumber) {}

    @Override
    public void onEndingPass(int currentPassNumber) {
        IO<File> result = serializer.serialize(headers, new File("tmp/mt_0"));
        headerFiles.add(result.attempt().expect("Cannot write metadata"));
        headers.clear();
    }

    @Override
    public void onOpeningFile(File file, int currentPassNumber) {
        currentPath = file.getPath();
    }

    @Override
    public void onClosingArticle(int articleId, int currentPassNumber) {}
    
    
    /**
     * ! COSTLY PROCESS ! , might be replace by Map giving file for an article id
     *
     * @param articleId
     * @return
     */
    public Optional<ArticleHeader> getArticleHeader(int articleId) {
        for (File headerFile : headerFiles) {
            final FileReader fileReader = read(headerFile)
                    .expect("Cannot read file " + headerFile);
            Optional<ArticleHeader> articleHeader = serializer.unserialize(fileReader, articleId)
                    .attempt()
                    .expect("Cannot unserialize the article " + articleId + " in file " + headerFile);
            if (articleHeader.isPresent()) {
                return articleHeader;
            }
        }
        return Optional.empty();
    }

    private Result<FileReader, FileNotFoundException> read(@NotNull File file) {
        try {
            return Result.ok(new FileReader(file));
        } catch (FileNotFoundException e) {
            return Result.err(e);
        }
    }

}
