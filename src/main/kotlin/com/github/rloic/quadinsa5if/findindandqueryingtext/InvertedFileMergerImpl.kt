package com.github.rloic.quadinsa5if.findindandqueryingtext

import com.github.quadinsa5if.findingandqueryingtext.lang.IO
import com.github.quadinsa5if.findingandqueryingtext.lang.Pair
import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import com.github.quadinsa5if.findingandqueryingtext.model.HeaderAndInvertedFile
import com.github.quadinsa5if.findingandqueryingtext.model.ReversedIndexIdentifier
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileMerger
import com.github.quadinsa5if.findingandqueryingtext.service.InvertedFileSerializer
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.InvertedFileSerializerImplementation
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.RandomAccessFile

class InvertedFileMergerImpl(
        private val serializer: InvertedFileSerializer = InvertedFileSerializerImplementation()
) : InvertedFileMerger {

    override fun merge(parts: MutableIterable<HeaderAndInvertedFile>, outputFiles: HeaderAndInvertedFile): HeaderAndInvertedFile {
        val process: IO<HeaderAndInvertedFile> = IO {

            val headerWriter = BufferedWriter(FileWriter(outputFiles.headerFile))
            val postingListWriter = BufferedWriter(FileWriter(outputFiles.invertedFile))

            val headers = mutableListOf<MutableList<Pair<String, ReversedIndexIdentifier>>>()
            val invertedFiles = mutableListOf<RandomAccessFile>()

            for (headerAndInverted in parts) {
                val headersPart = serializer
                        .unserializeHeader(FileReader(headerAndInverted.headerFile))
                        .map { header ->
                            header.entries.asSequence().map { mapEntry ->
                                Pair(mapEntry)
                            }.toMutableList()
                        }
                headers += headersPart.sync()
                invertedFiles += RandomAccessFile(headerAndInverted.invertedFile, "r")
            }

            var offset = 0
            while (headers.isNotEmpty()) {
                val currentIndices = getIndicesOfMinimalTerm(headers)
                val currentHeaders = getHeaders(currentIndices, headers)
                val invertedParts = getInvertedParts(currentIndices, invertedFiles)

                val entryList = getEntries(currentHeaders.map { it.second }, invertedParts)
                val term = currentHeaders[0].first!!
                val length = currentHeaders.sumBy { it.second.length }
                val termAndReversedIndexIndexIdentifier = Pair(term, ReversedIndexIdentifier(offset, length))

                entryList.map {
                    serializer.writeEntries(it, postingListWriter)
                }.then {
                    serializer.writeReversedIndexIdentifier(listOf(termAndReversedIndexIndexIdentifier), headerWriter)
                }.sync()
                offset += length
                increments(currentIndices, headers, invertedFiles)
            }
            outputFiles
        }
        return process.attempt().ok().get()
    }

    fun getIndicesOfMinimalTerm(
            terms: List<List<Pair<String, ReversedIndexIdentifier>>>
    ) : List<Int> {
        var minimalTerm = terms[0][0].first
        val indicesOfMinimalTerms = mutableListOf<Int>()
        for (i in terms.indices) {
            val firstTermEntry = terms[i][0]
            if (firstTermEntry.first < minimalTerm) {
                indicesOfMinimalTerms.clear()
                minimalTerm = firstTermEntry.first
                indicesOfMinimalTerms += i
            } else if (firstTermEntry.first == minimalTerm) {
                indicesOfMinimalTerms += i
            }
        }
        return indicesOfMinimalTerms
    }

    fun getHeaders(
            indices: List<Int>,
            identifiers: List<List<Pair<String, ReversedIndexIdentifier>>>
    ) : List<Pair<String, ReversedIndexIdentifier>> {
        val result = mutableListOf<Pair<String, ReversedIndexIdentifier>>()
        for (i in indices) {
            result += identifiers[i][0]
        }
        return result
    }

    fun getInvertedParts(
            indices: List<Int>,
            files: List<RandomAccessFile>
    ) : List<RandomAccessFile> {
        val result = mutableListOf<RandomAccessFile>()
        for (i in indices) {
            result += files[i]
        }
        return result
    }


    fun increments(
            indices: List<Int>,
            terms: MutableList<MutableList<Pair<String, ReversedIndexIdentifier>>>,
            invertedFiles: MutableList<RandomAccessFile>
    ) {
        val emptyListIndices = mutableListOf<Int>()
        for (i in indices) {
            terms[i].removeAt(0)
            if (terms[i].isEmpty()) {
                emptyListIndices += i
            }
        }
        var offset = 0
        for (i in emptyListIndices) {
            terms.removeAt(i - offset)
            invertedFiles.removeAt(i - offset)
            offset += 1
        }
    }

    fun getEntries(
            partsIdentifiers: List<ReversedIndexIdentifier>,
            files: List<RandomAccessFile>
    ): IO<List<Entry>> {
        return IO {
            val result = mutableListOf<Entry>()
            for (i in partsIdentifiers.indices) {
                val indexIdentifier = partsIdentifiers[i]
                val file = files[i]
                result.addAll(serializer.unserializePostingList(file, indexIdentifier.offset, indexIdentifier.length).sync())
            }
            result.sortedBy { it.score }
        }
    }

}