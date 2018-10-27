package com.github.rloic.quadinsa5if.findindandqueryingtext.benchmark

import kotlin.system.measureTimeMillis

class Benchmark {

    fun <T> bench(
            functionName: String,
            parameterName: String,
            parameters: List<T>,
            iteration: Int = 100,
            func: (T) -> Unit
    ) {
        val times = LongArray(parameters.size)
        for (i in 0 until iteration) {
            var j = 0
            for (param in parameters) {
                times[j] += measureTimeMillis { func(param) }
                j += 1
            }
        }

        println("Function: $functionName")
        for (i in parameters.indices) {
            print("With parameter $parameterName = ${parameters[i]}, \t")
            val score = "%.4f".format(times[i] / iteration.toDouble())
            println("mean runtime = $score ms, for $iteration iterations")
        }
        println("---------------------------------------")
    }

}

fun main(args: Array<String>) {


    val benchmark = Benchmark()
    benchmark.bench("addition", "x", listOf(1, 2, 3, 4, 5), iteration = 1000) { x -> x + 5 }

}