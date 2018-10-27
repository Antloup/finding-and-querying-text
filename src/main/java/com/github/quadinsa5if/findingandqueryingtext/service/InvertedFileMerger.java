package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;
import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.IOException;

public interface InvertedFileMerger {

  /**
   * Compile a list of partial inverted files to a full one
   * @param parts The parts list
   * @param outputFiles
   * @return The merged file
   */
  Result<HeaderAndInvertedFile, IOException> merge(Iterable<HeaderAndInvertedFile> parts, HeaderAndInvertedFile outputFiles);

}
