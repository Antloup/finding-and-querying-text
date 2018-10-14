package com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.InvertedFileSerializerImplementation;
import com.github.quadinsa5if.findingandqueryingtext.util.Cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Vocabulary recorded on the disk (not in the RAM)
 */
public class InDiskVocabularyImpl implements Vocabulary {

  /**
   * The cache of the posting list
   */
  private final Cache<String, List<Entry>> cache;

  /**
   * The reversed index identifiers. They are used to localize the different posting lists.
   */
  private final Map<String, ReversedIndexIdentifier> reversedIndexIdentifiers;

  /**
   * An inverted file serializer
   */
  private final InvertedFileSerializer serializer;

  public InDiskVocabularyImpl(final File headerFile, final File postingListFile, int cacheSize) {
    serializer = new InvertedFileSerializerImplementation();
    reversedIndexIdentifiers = serializer.unserializeHeader(headerFile).unwrap();
    cache = new Cache<>(cacheSize, term -> {
      final Optional<ReversedIndexIdentifier> postingListIdentifier = Optional.ofNullable(reversedIndexIdentifiers.get(term));
      return postingListIdentifier.map(it -> serializer.unserializePostingList(postingListFile, it.offset, it.length).unwrap())
          .orElse(new ArrayList<>());
    });
  }

  @Override
  public List<Entry> getPostingList(String term) {
    return cache.getOrLoad(term);
  }

}
