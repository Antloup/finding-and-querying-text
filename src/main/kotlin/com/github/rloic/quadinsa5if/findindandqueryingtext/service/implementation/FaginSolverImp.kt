package com.github.rloic.quadinsa5if.findindandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter
import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver
import java.util.*

typealias Term = String
typealias ArticleId = Int
typealias Score = Float
typealias ScoreMatrix = MutableMap<ArticleId, MutableMap<Term, Score>>

class FaginSolverImp : QuerySolver {

    override fun answer(vocabulary: Vocabulary, terms: Array<String>, k: Int): Iter<Int> {
        val scoreSortEntries = mutableMapOf<Term, MutableList<Entry>>()
        val randomAccessEntries = mutableMapOf<Term, MutableMap<ArticleId, Entry>>()

        for (term in terms) {
            val sortedEntriesForTerm = vocabulary.getPostingList(term).toMutableList()
            val randomAccessEntriesForTerm = randomAccessEntries[term] ?: mutableMapOf()

            scoreSortEntries[term] = sortedEntriesForTerm
            for (entry in sortedEntriesForTerm) {
                randomAccessEntriesForTerm[entry.articleId] = entry
            }
            randomAccessEntries[term] = randomAccessEntriesForTerm
        }

        val entries: ScoreMatrix = mutableMapOf()

        while (countAllTerms(terms, entries) < k && !areAllEmpty(scoreSortEntries)) {
            for (entriesOfTerm in scoreSortEntries) {
                if (entriesOfTerm.value.isNotEmpty()) {
                    val highestEntry = entriesOfTerm.value.removeAt(0)
                    val scoresForArticle = entries.getOrPut(highestEntry.articleId) { mutableMapOf() }
                    scoresForArticle[entriesOfTerm.key] = highestEntry.score
                }
            }
        }

        for ((articleId, articleScores) in entries) {
            if (!hasAllTerms(terms, articleScores)) {
                val unknownTerms = terms.filter { it !in articleScores.keys }
                for (term in unknownTerms) {
                    articleScores[term] = randomAccessEntries[term]!![articleId]?.score ?: .0f
                }
            }
        }

        val firstArticles = entries
                .asSequence()
                .map { aggregate(it) }
                .sortedByDescending { it.second }
                .map { it.first }
                .toList()

        return object : Iter<Int> {
            var i = 0
            override fun next(): Optional<Int> {
                return if (i < k && i < firstArticles.size) {
                    val result = Optional.of(firstArticles[i])
                    i += 1
                    result
                } else {
                    Optional.empty()
                }
            }
        }

    }

    private fun hasAllTerms(terms: Array<String>, entries: Map<String, Float>): Boolean {
        val keys = entries.keys
        return terms.all { it in keys }
    }


    private fun countAllTerms(terms: Array<String>, entriesMap: ScoreMatrix): Int {
        var count = 0
        for (entries in entriesMap.values) {
            if (hasAllTerms(terms, entries)) {
                count += 1
            }
        }
        return count
    }

    private fun areAllEmpty(entriesMap: Map<Term, List<Entry>>): Boolean {
        return entriesMap.values.all { it.isEmpty() }
    }

    fun aggregate(articleToScores: Map.Entry<ArticleId, Map<Term, Score>>): Pair<ArticleId, Score> {
        var score = 0f
        var count = 0
        for (scoreEntry in articleToScores.value) {
            score += scoreEntry.value
            count += 1
        }
        return articleToScores.key to ( score / count )

    }

}