package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter
import com.github.quadinsa5if.findingandqueryingtext.util.NaiveCompressor
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*

@RunWith(JUnitPlatform::class)
object NaiveCompressorTest : Spek({

    val compressor = NaiveCompressor()

    val encodedArray = listOf(52, 50, 59)
    val encodedByte = object : Iter<Byte> {
        internal var i = 0
        override fun next(): Optional<Byte> {
            return if (i == encodedArray.size) {
                Optional.empty()
            } else {
                return Optional.of(encodedArray[i++].toByte())
            }
        }
    };

    given("a NaiveCompressor") {
        on("encoded 42") {
            val decoded = compressor.decode(encodedByte)
            it("should decode 42") {
                assertThat(decoded, equalTo(42))
            }
        }
        on("decoded 4") {
            val decoded = compressor.encode(4)
            it("should decode 4") {
                assertThat(decoded.next().get(), equalTo('4'.toByte()))
            }
        }
        on("complete encode/decode 42") {
            val result = compressor.decode(compressor.encode(42))
            it("should decode 42") {
                assertThat(result, equalTo(42))
            }
        }
        on("complete encode/decode 543") {
            val result = compressor.decode(compressor.encode(543))
            it("should decode 42") {
                assertThat(result, equalTo(543))
            }
        }
    }

})