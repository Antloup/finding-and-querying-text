package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleHeader;
import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;
import com.github.quadinsa5if.findingandqueryingtext.service.MetadataSerializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetadataImplementation implements DatasetVisitor {

    public String currentPath;
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
    public void onArticleParseStart(int articleId, int currentPassNumber) {

    }

    @Override
    public void onTermRead(String term, int currentPassNumber) {

    }

    @Override
    public void onArticleParseEnd(int articleId, int currentPassNumber) {
        this.headers.add(new ArticleHeader(articleId, currentPath));
    }

    @Override
    public void onPassEnd(int currentPassNumber) {
        Result<File, Exception> result = serializer.serialize(headers);
        headerFiles.add(result.ok().get());
        headers.clear();
    }

    @Override
    public void onPassStart(File file, int currentPassNumber) {
        currentPath = file.getPath();
    }

    /**
     * ! COSTLY PROCESS ! , might be replace by Map giving file for an article id
     * @param articleId
     * @return
     */
    public Optional<ArticleHeader> getArticleHeader(int articleId){
        for(File headerFile : headerFiles){
            Result<Optional<ArticleHeader>, IOException> articleHeader = this.serializer.unserialize(headerFile,articleId).attempt();
            if(articleHeader.ok().get().isPresent()){
                return articleHeader.unwrap();
            }
        }
        return Optional.empty();
    }
}
