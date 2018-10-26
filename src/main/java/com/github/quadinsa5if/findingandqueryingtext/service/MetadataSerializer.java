package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.lang.IO;
import com.github.quadinsa5if.findingandqueryingtext.model.Metadata;
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
    public Result<File, Exception> serialize(List<Metadata> metadata);

    /**
     * Unserialize the metadata from a metadata file
     *
     * @param file
     * @return
     */
    public IO<List<Metadata>> unserialize(FileReader file);

    /**
     * Get metadata for a specific article
     *
     * @param file
     * @param articleId
     * @return
     */
    public IO<Optional<Metadata>> unserialize(FileReader file, int articleId);


}
