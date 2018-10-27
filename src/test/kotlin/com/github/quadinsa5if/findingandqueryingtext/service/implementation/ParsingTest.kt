package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.tokenizer.DocumentParser
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import java.util.*

@RunWith(JUnitPlatform::class)
object ParsingTest : Spek({

        on("computing scores") {

            val serializer = InvertedFileSerializerImplementation()
            val scorerVisitor = ScorerImplementation(serializer, 10)
            val metadataVisitor = MetadataImplementation()

            val parser = DocumentParser(Arrays.asList(scorerVisitor, metadataVisitor))
            val datasetFiles: Array<File> = arrayOf(File("test_data/mini_bible"))
            parser.parse(datasetFiles)

            val allScores = scorerVisitor.allScores


            it("scores are correct") {
                assertThat("'god' score in article 1", allScores.get("1_god"), equalTo(0.8859001247045623))
                assertThat("'god' score in article 2", allScores.containsKey("2_god"), equalTo(false))
                assertThat("'god' score in article 3", allScores.get("3_god"), equalTo(0.6037331324768168))
                assertThat("'god' score in article 4", allScores.containsKey("4_god"), equalTo(false))
            }

            it("each article have his own vocabulary") {
                assertThat("'mercy' word isn't in article 1", allScores.containsKey("1_mercy"), equalTo(false))
                assertThat("'mercy' word is in article 2", allScores.containsKey("2_mercy"), equalTo(true))
                assertThat("'mercy' word isn't in article 3", allScores.containsKey("3_mercy"), equalTo(false))
                assertThat("'mercy' word isn't in article 4", allScores.containsKey("4_mercy"), equalTo(false))
            }

        }

})