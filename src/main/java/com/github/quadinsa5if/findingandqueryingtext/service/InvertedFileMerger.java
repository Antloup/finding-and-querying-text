package com.github.quadinsa5if.findingandqueryingtext.service;

import java.io.File;
import java.util.List;

public interface InvertedFileMerger {

  /**
   * Compile a list of partial inverted files to a full one
   * @param parts The parts list
   * @return The merged file
   */
  File merge(List<File> parts);

}
