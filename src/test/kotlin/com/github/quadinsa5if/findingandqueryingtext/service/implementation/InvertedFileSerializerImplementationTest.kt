package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId
import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object InvertedFileSerializerImplementationTest: Spek({

  val serializer: InvertedFileSerializer = InvertedFileSerializerImplementation()

  val d1 = ArticleId(1, "/d1")
  val d2 = ArticleId(2, "/d2")
  val d3 = ArticleId(3, "/d2")
  val d4 = ArticleId(4, "/d4")
  val d5 = ArticleId(5, "/d5")
  val d6 = ArticleId(6, "/d6")

  val postingListT1 = listOf(
      Entry(d2, .9f),
      Entry(d5, .8f),
      Entry(d6, .7f),
      Entry(d4, .6f),
      Entry(d1, .5f),
      Entry(d3, .4f)
  )

  val postingListT2 = listOf(
      Entry(d3, .85f),
      Entry(d5, .8f),
      Entry(d2, .75f),
      Entry(d6, .74f),
      Entry(d1, .74f),
      Entry(d4, .7f)
  )

  val postingLists = InMemoryVocabularyImpl()
  postingListT1.forEach { postingLists.putEntry("t1", it) }
  postingListT2.forEach { postingLists.putEntry("t2", it) }

  given("an inverted file serializer implementation") {

    on("serialize") {
      serializer.serialize(postingLists)
    }

  }

})