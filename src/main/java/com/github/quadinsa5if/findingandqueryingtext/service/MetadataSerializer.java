package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.Article;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleHeader;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;

public interface MetadataSerializer {

    /**
     * Serialize metadatas and write to the disk
     *
     * @param metadata
     * @return Metadata file
     */
    IO<File> serialize(List<ArticleHeader> metadata, File output);

    /**
     * Unserialize the metadata from a metadata file
     *
     * @param file
     * @return
     */
    IO<List<ArticleHeader>> unserialize(FileReader file);

    /**
     * Get metadata for a specific article
     *
     * @param file
     * @param articleId
     * @return
     */
    IO<Optional<ArticleHeader>> unserialize(FileReader file, int articleId);


}
