package com.github.rloic.quadinsa5if.findindandqueryingtext.benchmark


class HeapMonitor {

    private var previousUsage = 0L

    init {
        reset()
    }

    private fun cumulHeap(): Long  = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

    fun reset() {
        Runtime.getRuntime().gc()
        previousUsage = cumulHeap()
    }

    fun heapUsage() = cumulHeap() - previousUsage

    companion object {
        fun format(v: Long): String {
            if (v < 1024) return v.toString() + " B"
            val z = (63 - java.lang.Long.numberOfLeadingZeros(v)) / 10
            return String.format("%.1f %sB", v.toDouble() / (1L shl z * 10), " KMGTPE"[z])
        }
    }

}

