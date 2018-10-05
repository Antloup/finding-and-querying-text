package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.model.Vocabulary;

import java.io.File;

public interface InvertedFileSerializer {

  /**
   * Serialize and write the vocabulary to the disk
   * @param vocabulary The Vocabulary to serialize
   * @return The while on the disk
   */
  File serialize(Vocabulary vocabulary);

  /**
   * Unserialize the vocabulary from a inverted file
   * @param file The inverted file
   * @return The Vocabulary structure
   */
  Vocabulary unserialize(File file);

}
