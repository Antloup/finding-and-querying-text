package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile;

public interface InvertedFileMerger {

  /**
   * Compile a list of partial inverted files to a full one
   * @param parts The parts list
   * @param outputFiles
   * @return The merged file
   */
  HeaderAndInvertedFile merge(Iterable<HeaderAndInvertedFile> parts, HeaderAndInvertedFile outputFiles);

}
