package com.github.rloic.quadinsa5if.findindandqueryingtext.benchmark

import kotlin.system.measureTimeMillis

class Benchmark {

    fun <T> bench(
            functionName: String,
            parameterName: String,
            parameters: List<T>,
            iterations: Int = 100,
            func: (T) -> Unit
    ) {

        println("Function: $functionName for $iterations iterations")
        for (param in parameters) {
            var time = 0L
            for (i in 0 until iterations) {
                time += measureTimeMillis { func(param) }
            }
            print("With parameter $parameterName = $param, \t")
            val score = "%.4f".format(time / iterations.toDouble())

            println("mean runtime = $score ms")
        }
        println("---------------------------------------")
    }

}