package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.Metadata;
import com.github.quadinsa5if.findingandqueryingtext.service.MetadataSerializer;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MetadataSerializerImplementation implements MetadataSerializer {
    @Override
    public Result<File, Exception> serialize(List<Metadata> metadata) {
        //TODO
        return null;
    }

    @Override
    public Result<List<Metadata>, IOException> unserialize(File file) {
        //TODO
        return null;
    }

    @Override
    public Result<Metadata, IOException> unserialize(File file, int articleId) {
        //TODO
        return null;
    }

    @Override
    public Result<List<Metadata>, IOException> unserialize(File file, List<Integer> articlesId) {
        //TODO
        return null;
    }
}
