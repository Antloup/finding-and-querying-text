package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import java.io.FileReader
import java.io.RandomAccessFile

@RunWith(JUnitPlatform::class)
object InvertedFileSerializerImplementationTest: Spek({

  val TEST_FOLDER_NAME = "test_folder_inverted_file_serializer"
  val testDirectory = File(TEST_FOLDER_NAME)
  testDirectory.run { deleteRecursively(); delete() }
  val serializer: InvertedFileSerializer = InvertedFileSerializerImplementation(TEST_FOLDER_NAME)

  val d1 = 1
  val d2 = 2
  val d3 = 3
  val d4 = 4
  val d5 = 5
  val d6 = 6

  val postingListT1 = listOf(
      Entry(d2, .9),
      Entry(d5, .8),
      Entry(d6, .7),
      Entry(d4, .6),
      Entry(d1, .5),
      Entry(d3, .4)
  )

  val postingListT2 = listOf(
      Entry(d3, .85),
      Entry(d5, .8),
      Entry(d2, .75),
      Entry(d6, .74),
      Entry(d1, .74),
      Entry(d4, .7)
  )

  val postingLists = InMemoryVocabularyImpl()
  postingListT1.forEach { postingLists.putEntry("t1", it) }
  postingListT2.forEach { postingLists.putEntry("t2", it) }

  given("an inverted file serializer implementation") {
    on("serialize") {
      val result = serializer.serialize(postingLists)
      it("result should be ok") {
        assertThat(result.isOk, equalTo(true))
      }
    }
  }

  given("the serialized files") {
    val result = serializer.serialize(postingLists).unwrap()
    val headerFile = result.headerFile!!
    val invertedFile = result.invertedFile!!

    on("unserialize header") {
      val headerResult = serializer.unserializeHeader(FileReader(headerFile)).attempt()
      it("result must be ok") {
        assertThat(headerResult.isOk, equalTo(true))
      }
    }

    on("unserialize vocabulary from header") {
      val headerResult = serializer.unserializeHeader(FileReader(headerFile)).attempt()
      val vocResult = serializer.unserialize(RandomAccessFile(invertedFile, "r"), headerResult.ok().get()).attempt()
      it("result must be ok") {
        assertThat(vocResult.isOk, equalTo(true))
      }
      it("result me be equals to the serialized posting list") {
        assertThat(vocResult.ok().get(), equalTo(postingLists))
      }
    }
  }

})