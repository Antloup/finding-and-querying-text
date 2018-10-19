package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.model.Metadata;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface MetadataSerializer {

    public Result<File, Exception> serialize(List<Metadata> metadata);
    public Result<List<Metadata>, IOException> unserialize(File file);
    public Result<Metadata, IOException> unserialize(File file, int articleId);
    public Result<List<Metadata>, IOException> unserialize(File file, List<Integer> articlesId);


}
