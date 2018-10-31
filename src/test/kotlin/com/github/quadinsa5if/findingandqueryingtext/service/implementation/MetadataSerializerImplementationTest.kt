package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleHeader
import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl
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
object MetadataSerializerImplementationTest : Spek({
    val TEST_FOLDER_NAME = "fileTest/novb"

    val testDirectory = File(TEST_FOLDER_NAME)
    if (!testDirectory.exists()) {
        testDirectory.mkdirs()
    } else {
        testDirectory.listFiles().forEach { it.delete() }
    }
    val serializer = MetadataSerializerImplementation(TEST_FOLDER_NAME)

    val metadata1 = ArticleHeader(1, "path1")
    val metadata2 = ArticleHeader(2, "path2")
    val metadata3 = ArticleHeader(3, "path3")
    val metadata4 = ArticleHeader(4, "path4")
    val metadata5 = ArticleHeader(5, "path5")

    val metadataList = listOf(
            metadata1,
            metadata2,
            metadata3,
            metadata4,
            metadata5
    )

    var _i = 0

    fun inc() = _i++

    fun genFile() = File(TEST_FOLDER_NAME + "/test_" + inc())

    given("a metadata file serializer implementation") {
        on("serialize") {
            val result = serializer.serialize(metadataList, genFile()).attempt()
            it("result should be ok") {
                assertThat(result.isOk, equalTo(true))
            }
        }
    }

    given("the serialized files") {
        val result = serializer.serialize(metadataList, genFile()).attempt().unwrap()
        val metadataFile = result!!

        on("unserialize metadata") {
            val metadataResult = serializer.unserialize(FileReader(metadataFile)).attempt()
            it("result must be ok") {
                assertThat(metadataResult.isOk, equalTo(true))
            }
        }

    }
})