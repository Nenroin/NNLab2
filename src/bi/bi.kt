package bi

import kotlin.random.Random
import kotlin.math.sign

const val maxIteration = 10
const val measurementsNumber = 10

val x1 = listOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
val x2 = listOf(1, 1, 1, -1, -1, -1, 1, 1, 1, -1)
val x3 = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
val x4 = listOf(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1)

val y1 = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
val y2 = listOf(-1, -1, 1, 1, 1, -1, -1, -1, 1, 1)
val y3 = listOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
val y4 = listOf(1, 1, 1, -1, -1, -1, -1, -1, -1, -1)

val X = listOf(x1, x2, x3, x4)
val Y = listOf(y1, y2, y3, y4)

fun transpose(matrix: List<List<Int>>): List<List<Int>> {
    return List(matrix[0].size) { i -> List(matrix.size) { j -> matrix[j][i] } }
}

fun dotProduct(A: List<List<Int>>, B: List<List<Int>>): List<List<Int>> {
    val rowsA = A.size
    val colsA = A[0].size
    val rowsB = B.size
    val colsB = B[0].size
    require(colsA == rowsB) { "Invalid matrix dimensions for multiplication" }

    return List(rowsA) { i ->
        List(colsB) { j ->
            (0..<colsA).sumOf { k -> A[i][k] * B[k][j] }
        }
    }
}

fun makeBitsNoisy(y: List<Int>, bitsCount: Int): List<Int> {
    val noisyPositions = List(bitsCount) { Random.nextInt(y.size) }
    return y.mapIndexed { index, value -> if (index in noisyPositions) -1 * value else value }
}

fun calculateX(yNoisy: List<Int>, W: List<List<Int>>, yOriginal: List<Int>, enablePrint: Boolean = false): Boolean {
    var yLast = yNoisy
    if (enablePrint) {
        println("y_original = [${formatList(yOriginal)}]")
        println("y_model(0) = [${formatList(yNoisy)}]")
    }

    for (i in 0..<maxIteration) {
        if (enablePrint) println("\nStage ${i + 1}:")

        val sX = dotProduct(listOf(yLast), transpose(W))
        val xOut = sX[0].map { it.sign }
        if (enablePrint) println("x_model(${i + 1}) = [${formatList(xOut)}]")

        val sY = dotProduct(listOf(xOut), W)
        val yOut = sY[0].map { it.sign }
        if (enablePrint) println("y_model(${i + 1}) = [${formatList(yOut)}]")

        if (yLast == yOut) {
            if (enablePrint) println("y_model(${i + 1}) == y_model($i), relaxation")
            if (yLast == yOriginal) {
                if (enablePrint) println("y_model(${i + 1}) == y_original, relaxation with correct value")
                return true
            } else {
                if (enablePrint) println("y_model(${i + 1}) != y_original, relaxation with wrong value")
                return false
            }
        } else {
            yLast = yOut
            if (enablePrint) println("y_model(${i + 1}) != y_model($i), continue calculation")
        }
    }

    if (enablePrint) println("Model can’t find relaxation, max iteration = $maxIteration")
    return false
}

fun calculateY(xNoisy: List<Int>, W: List<List<Int>>, xOriginal: List<Int>, enablePrint: Boolean = false): Boolean {
    var xLast = xNoisy
    if (enablePrint) {
        println("x_original = [${formatList(xOriginal)}]")
        println("x_model(0) = [${formatList(xNoisy)}]")
    }

    for (i in 0..<maxIteration) {
        if (enablePrint) println("\nStage ${i + 1}:")

        val sY = dotProduct(listOf(xLast), W)
        val yOut = sY[0].map { it.sign }
        if (enablePrint) println("y_model(${i + 1}) = [${formatList(yOut)}]")

        val sX = dotProduct(listOf(yOut), transpose(W))
        val xOut = sX[0].map { it.sign }
        if (enablePrint) println("x_model(${i + 1}) = [${formatList(xOut)}]")

        if (xLast == xOut) {
            if (enablePrint) println("x_model(${i + 1}) == x_model($i), relaxation")
            if (xLast == xOriginal) {
                if (enablePrint) println("x_model(${i + 1}) == x_original, relaxation with correct value")
                return true
            } else {
                if (enablePrint) println("x_model(${i + 1}) != x_original, relaxation with wrong value")
                return false
            }
        } else {
            xLast = xOut
            if (enablePrint) println("x_model(${i + 1}) != x_model($i), continue calculation")
        }
    }

    if (enablePrint) println("Model can’t find relaxation, max iteration = $maxIteration")
    return false
}

fun formatList(list: List<Int>): String {
    return list.joinToString(", ") { String.format("%2d", it) }
}

fun main() {
    val W = dotProduct(transpose(X), Y)

    println("Source vectors:")
    X.forEachIndexed { i, x ->
        println("x${i + 1} = [${formatList(x)}] \ty${i + 1} = [${formatList(Y[i])}]")
    }

    for (i in Y.indices) {
        println("\ny${i + 1}:")
        calculateX(Y[i], W, Y[i], enablePrint = true)
    }

    for (i in X.indices) {
        println("\nx${i + 1}:")
        calculateY(X[i], W, X[i], enablePrint = true)
    }


    val measurementsOutputY = mutableMapOf<String, MutableList<Int>>()
    val measurementsOutputX = mutableMapOf<String, MutableList<Int>>()

    for (i in 0..< measurementsNumber) {
        for (yIdx in Y.indices) {
            measurementsOutputY["y_$yIdx"] = mutableListOf()
        }
    }

    for (i in 0..< measurementsNumber) {
        for (xIdx in X.indices) {
            measurementsOutputX["x_$xIdx"] = mutableListOf()
        }
    }

    for (measurementIdx in 0..< measurementsNumber) {
        for (yIdx in Y.indices) {
            var maxRecognizedBits = 0
            for (noisyBitsCount in 1..Y[yIdx].size) {
                val yNoisy = makeBitsNoisy(Y[yIdx], noisyBitsCount)
                if (calculateX(yNoisy, W, Y[yIdx])) {
                    maxRecognizedBits = noisyBitsCount
                }
            }
            measurementsOutputY["y_$yIdx"]?.add(maxRecognizedBits)
        }

        for (xIdx in X.indices) {
            var maxRecognizedBits = 0
            for (noisyBitsCount in 1..X[xIdx].size) {
                val xNoisy = makeBitsNoisy(X[xIdx], noisyBitsCount)
                if (calculateY(xNoisy, W, X[xIdx])) {
                    maxRecognizedBits = noisyBitsCount
                }
            }
            measurementsOutputX["x_$xIdx"]?.add(maxRecognizedBits)
        }
    }

    val medianY = measurementsOutputY.mapValues { (_, values) -> values.sorted().let { it[it.size / 2] } }
    val medianX = measurementsOutputX.mapValues { (_, values) -> values.sorted().let { it[it.size / 2] } }

    println("\nMedian number of recognised noisy bits ($measurementsNumber samples):")
    println("Y: $medianY")
    println("X: $medianX")
}