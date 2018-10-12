package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FaginSolvertImplTest {

  private static ArticleId d1 = new ArticleId(1, "/d1");
  private static ArticleId d2 = new ArticleId(2, "/d2");
  private static ArticleId d3 = new ArticleId(3, "/d3");
  private static ArticleId d4 = new ArticleId(4, "/d4");
  private static ArticleId d5 = new ArticleId(5, "/d5");
  private static ArticleId d6 = new ArticleId(6, "/d6");

  @Test
  public void sortedAccess() {
    final Map<String, List<Entry>> sortedEntries = new HashMap<>();

    final List<Entry> postingListT1 = Arrays.asList(
        new Entry(d2, .9f),
        new Entry(d5, .8f),
        new Entry(d6, .7f),
        new Entry(d4, .6f),
        new Entry(d1, .5f),
        new Entry(d3, .4f)
    );

    final List<Entry> postingListT2 = Arrays.asList(
        new Entry(d3, .85f),
        new Entry(d5, .8f),
        new Entry(d2, .75f),
        new Entry(d6, .74f),
        new Entry(d1, .74f),
        new Entry(d4, .7f)
    );


    sortedEntries.put("t1", postingListT1);
    sortedEntries.put("t2", postingListT2);


  }
}