package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter
import com.github.quadinsa5if.findingandqueryingtext.util.NaiveCompressor
import com.github.quadinsa5if.findingandqueryingtext.util.VByteCompressor
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
object VByteCompressorTest : Spek({

    val compressor = VByteCompressor()

    val encodedArray1 = listOf(170)
    val encodedArray2 = listOf(1,130)
    val encodedByte1 = object : Iter<Byte> {
        var i = 0
        override fun next(): Optional<Byte> {
            return if (i == encodedArray1.size) {
                Optional.empty()
            } else {
                return Optional.of(encodedArray1[i++].toByte())
            }
        }
    };
    val encodedByte2 = object : Iter<Byte> {
        var i = 0
        override fun next(): Optional<Byte> {
            return if (i == encodedArray2.size) {
                Optional.empty()
            } else {
                return Optional.of(encodedArray2[i++].toByte())
            }
        }
    };

    given("a VByteCompressor") {
        on("encoded 170") {
            val decoded = compressor.decode(encodedByte1)
            it("should decode 42") {
                assertThat(decoded, equalTo(42))
            }
        }
        on("encoded 1,130") {
            val decoded = compressor.decode(encodedByte2)
            it("should decode 130") {
                assertThat(decoded, equalTo(130))
            }
        }
        on("decoded 42") {
            val encoded = compressor.encode(42)
            it("should return encoded 170") {
                assertThat(encoded.next().get(), equalTo(170.toByte())) // 128+42
            }
        }
        on("complete encode/decode 19403") {
            val result = compressor.decode(compressor.encode(19403))
            it("should decode 577") {
                assertThat(result, equalTo(19403))
            }
        }
    }

})