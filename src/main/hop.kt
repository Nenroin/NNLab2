package main
import matrix.Matrix
import matrix.hopLogic

fun main() {
    // Variant 12
    // n = 10 m = 10 vectors = 7, 8, 5, 12
    val yOriginals:List<Matrix> = listOf(
        Matrix(listOf(listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0))),
        Matrix(listOf(listOf(1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0))),
        Matrix(listOf(listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))),
        Matrix(listOf(listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))))

    val y = Matrix(yOriginals.flatMap { matrix -> matrix.toListOfLists() })
    hopLogic(yOriginals, y)
}

fun printOriginalsVectors(yOriginals: List<Matrix>, name: String) {
    println("${name}:\n")
    println("1. Source vectors:")
    yOriginals.forEachIndexed { index, value -> println("Y$index = [$value]") }
    println()
}
