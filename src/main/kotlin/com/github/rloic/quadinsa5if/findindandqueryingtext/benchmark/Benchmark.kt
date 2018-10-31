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
        val heapMonitor = HeapMonitor()

        println("Function: $functionName for $iterations iterations")
        for (param in parameters) {
            var time = 0L
            var heapUsage = 0L
            for (i in 0 until iterations) {
                heapMonitor.reset()
                time += measureTimeMillis { func(param) }
                heapUsage += heapMonitor.heapUsage()
            }
            print("With parameter $parameterName = $param, \t")
            val score = "%.4f".format(time / iterations.toDouble())
            val meanHeap = HeapMonitor.format(heapUsage / iterations)
            println("mean runtime = $score ms, mean heap usage = $meanHeap")
        }
        println("---------------------------------------")
    }

}