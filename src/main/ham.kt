package main

import kotlin.math.max
import kotlin.random.Random

const val maxIteration = 40

val y1 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
val y2 = listOf(1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0)
val y3 = listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
val y4 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

val Y = listOf(y1, y2, y3, y4)

val W = Y.map { row -> row.map { it * 0.5 } }
val n = Y[0].size
val T = n / 2.0
val e = 1.0 / n

fun relu(s: List<Double>): List<Double> = s.map { max(0.0, it) }

fun makeBitsNoisy(y: List<Double>, bitsCount: Int): List<Double> {
    val noisyPositions = List(bitsCount) { Random.nextInt(y.size) }
    return y.mapIndexed { index, value -> if (index in noisyPositions) 1.0 - value else value }
}

fun formatList(list: List<Double>): String {
    return list.joinToString(", ") { String.format("%.2f", it) }
}

fun calculateWinner(yNoisy: List<Double>, yOriginal: List<Double>, enablePrint: Boolean = false): Boolean {

    if (enablePrint) println("y_original = ${formatList(yOriginal)}")
    if (enablePrint) println("y_noisy    = ${formatList(yNoisy)}")

    var z = W.map { row -> row.zip(yNoisy) { a, b -> a * b }.sum() + T }

    repeat(maxIteration) { iteration ->
        val s = List(z.size) { i ->
            z.mapIndexed { k, zk -> if (i == k) zk else -e * zk }.sum()
        }
        z = relu(s)

        if (enablePrint) println("winner($iteration) = ${formatList(z)}")

        val positiveIndices = z.mapIndexedNotNull { index, value -> if (value > 0) index else null }
        when {
            positiveIndices.size == 1 -> {
                val yModel = Y[positiveIndices.first()]
                if (enablePrint) println("y_model($iteration) = $yModel")
                return if (yModel == yOriginal) {
                    if (enablePrint) println("y_model($iteration) == y_original, correct")
                    true
                } else {
                    if (enablePrint) println("y_model($iteration) != y_original, wrong")
                    false
                }
            }
            positiveIndices.isEmpty() -> {
                if (enablePrint) println("Model can't find relaxation")
                return false
            }
        }
    }
    if (enablePrint) println("Model can't find relaxation, max iteration = $maxIteration")
    return false
}

fun main() {
    println("\nSource vectors:")
    Y.forEachIndexed { index, y -> println("y${index + 1} = ${formatList(y)}") }

    Y.forEachIndexed { index, yOriginal ->
        println("\nWinner y${index + 1}:")
        calculateWinner(yOriginal, yOriginal, enablePrint = true)
    }

    val measurementsNumber = 10
    val measurementsOutput = mutableMapOf<String, MutableList<Int>>()

    repeat(measurementsNumber) { _ ->
        Y.forEachIndexed { index, yOriginal ->
            val key = "y_${index + 1}"
            measurementsOutput.putIfAbsent(key, mutableListOf())
            var maxRecognizedBits = 0

            for (noisyBitsCount in 1..n) {
                val yNoisy = makeBitsNoisy(yOriginal, noisyBitsCount)
                if (calculateWinner(yNoisy, yOriginal)) {
                    maxRecognizedBits = noisyBitsCount
                }
            }
            measurementsOutput[key]!!.add(maxRecognizedBits)
        }
    }

    val medianOutput = measurementsOutput.mapValues { (_, values) ->
        values.sorted().let { it[it.size / 2] }
    }

    println("\nMedian number of recognised noisy bits ($measurementsNumber samples):")
    println(medianOutput)
}
