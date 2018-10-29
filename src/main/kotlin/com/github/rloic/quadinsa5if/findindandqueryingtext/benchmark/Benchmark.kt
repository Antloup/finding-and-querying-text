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
        val times = LongArray(parameters.size)
        val heapUsages = LongArray(parameters.size)
        val heapMonitor = HeapMonitor()

        for (i in 0 until iterations) {
            var j = 0
            for (param in parameters) {
                heapMonitor.reset()
                times[j] += measureTimeMillis { func(param) }
                heapUsages[j] += heapMonitor.heapUsage()
                j += 1
            }
        }

        println("Function: $functionName for $iterations iterations")
        for (i in parameters.indices) {
            print("With parameter $parameterName = ${parameters[i]}, \t")
            val score = "%.4f".format(times[i] / iterations.toDouble())
            val heapUsage = HeapMonitor.format(heapUsages[i] / iterations)
            println("mean runtime = $score ms, mean heap usage = $heapUsage")
        }
        println("---------------------------------------")
    }

}