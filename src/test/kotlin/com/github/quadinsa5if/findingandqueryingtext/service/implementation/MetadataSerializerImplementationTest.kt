package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId
import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import com.github.quadinsa5if.findingandqueryingtext.model.Metadata
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
    testDirectory.run { deleteRecursively(); delete() }
    val serializer = MetadataSerializerImplementation(TEST_FOLDER_NAME)

    val metadata1 = Metadata(1, "path1")
    val metadata2 = Metadata(2, "path2")
    val metadata3 = Metadata(3, "path3")
    val metadata4 = Metadata(4, "path4")
    val metadata5 = Metadata(5, "path5")

    val metadataList = listOf(
            metadata1,
            metadata2,
            metadata3,
            metadata4,
            metadata5)

    given("a metadata file serializer implementation") {
        on("serialize") {
            val result = serializer.serialize(metadataList)
            it("result should be ok") {
                assertThat(result.isOk, equalTo(true))
            }
        }
    }

    given("the serialized files") {
        val result = serializer.serialize(metadataList).unwrap()
        val metadataFile = result!!

        on("unserialize metadata") {
            val metadataResult = serializer.unserialize(FileReader(metadataFile)).attempt()
            it("result must be ok") {
                assertThat(metadataResult.isOk, equalTo(true))
            }
        }

    }
})