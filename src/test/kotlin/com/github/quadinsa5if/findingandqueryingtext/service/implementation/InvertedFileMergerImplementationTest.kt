package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.lang.Pair
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier
import com.github.quadinsa5if.findingandqueryingtext.util.NaiveCompressor
import com.github.rloic.quadinsa5if.findindandqueryingtext.service.implementation.InvertedFileMergerImpl
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import java.io.RandomAccessFile

@RunWith(JUnitPlatform::class)
object InvertedFileMergerImplementationTest : Spek({
  val header1 = mutableListOf(
      Pair("t1", ReversedIndexIdentifier(0, 20)),
      Pair("t3", ReversedIndexIdentifier(20, 12))
  )
  val header2 = mutableListOf(
      Pair("t1", ReversedIndexIdentifier(0, 6)),
      Pair("t2", ReversedIndexIdentifier(6, 15))
  )
  val headers = mutableListOf(header1, header2)
  val file = File("tmp_test/fake_inverted_file.txt")
  if (!file.exists()) {
    file.parentFile.mkdirs()
    file.createNewFile()
  }
  val fakeInvertedFiles = mutableListOf(
      RandomAccessFile(file, "r"),
      RandomAccessFile(file, "r")
  )
  val merger = InvertedFileMergerImpl(InvertedFileSerializerImplementation(NaiveCompressor()))
  given("two posting headers") {

    on("get indices of minimal terms") {
      val indicesOfMinimalTerm = merger.getIndicesOfMinimalTerm(headers)
      it("should be return [0, 1]") {
        assertThat(indicesOfMinimalTerm, equalTo(listOf(0, 1)))
      }
    }

    on("read heads after increment") {
      merger.increments(listOf(0, 1), headers, fakeInvertedFiles)
      val headTerms = headers.map { it[0].first }
      it("should be equals to [t3, t2]") {
        assertThat(headTerms, equalTo(listOf("t3", "t2")))
      }
    }

    on("get indices of minimal term after increment") {
      val indicesOfMinimalTerm = merger.getIndicesOfMinimalTerm(headers)
      it("should return [1]") {
        assertThat(indicesOfMinimalTerm, equalTo(listOf(1)))
      }
    }

    on("read heads after the second increment") {
      merger.increments(listOf(1), headers, fakeInvertedFiles)
      val heads = headers.map { it[0].first }
      it("heads must be [t3]") {
        assertThat(heads, equalTo(listOf("t3")))
      }
    }

    on("get indices of minimal term after second increment") {
      val indicesOfMinimalTerm = merger.getIndicesOfMinimalTerm(headers)
      it("should return [0]") {
        assertThat(indicesOfMinimalTerm, equalTo(listOf(0)))
      }
    }

    on("read heads after the third increment") {
      merger.increments(listOf(0), headers, fakeInvertedFiles)
      val heads = headers.map { it[0].first }
      it("heads must be []") {
        assertThat(heads, equalTo(emptyList()))
      }
    }
  }

})