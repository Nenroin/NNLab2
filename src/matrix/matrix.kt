package matrix

import kotlin.math.absoluteValue
import kotlin.random.Random

class Matrix(var data: List<List<Double>>) {
    constructor(rows: Int, cols: Int, defaultValue: Double) : this(List(rows) { List(cols) { defaultValue } })

    companion object {
        fun identity(size: Int): Matrix {
            val result = Matrix(size, size, 0.0)
            for (i in 0..<size) {
                result[i, i] = 1.0
            }
            return result
        }

        fun generateRandomNoise(matrix: Matrix, size: Int = 100): List<Matrix> {
            val rows = matrix.getRowsNum()
            val cols = matrix.getColumnsNum()

            val result = mutableListOf<Matrix>()
            val powerM: Int = matrix.getRowsNum() * matrix.getColumnsNum()

            for (i in 0..< size) {
                val noisyData: Matrix = matrix.copy()
                val chanValNum = Random.nextInt().absoluteValue % powerM
                for(j in 0 ..< chanValNum) {
                    noisyData[Random.nextInt().absoluteValue % rows, Random.nextInt().absoluteValue % cols] =
                        if (Random.nextBoolean()) 1.0 else 0.0
                }

                result.add(noisyData)
            }

            return result
        }
    }

    operator fun get(row: Int, col: Int): Double = data[row][col]
    fun getRowsNum(): Int = data.size
    fun getColumnsNum(): Int = data[0].size

    operator fun set(row: Int, col: Int, value: Double) {
        data = data.mapIndexed { rIndex, r ->
            if (rIndex == row) r.mapIndexed { cIndex, c -> if (cIndex == col) value else c } else r
        }
    }

    operator fun plus(other: Matrix): Matrix {
        checkSizes(other)
        val result = Matrix(data)
        for (row in data.indices) {
            for (col in data[row].indices) {
                result[row, col] = result[row, col] + other[row, col]
            }
        }
        return result
    }

    operator fun minus(other: Matrix): Matrix {
        checkSizes(other)
        val result = Matrix(data)
        for (row in data.indices) {
            for (col in data[row].indices) {
                result[row, col] = result[row, col] - other[row, col]
            }
        }
        return result
    }

    operator fun plus(scalar: Double): Matrix {
        val result = Matrix(data)
        for (i in data.indices) {
            for (j in data[i].indices) {
                result[i, j] += scalar
            }
        }
        return result
    }

    operator fun minus(scalar: Double): Matrix {
        val result = Matrix(data)
        return result + (-scalar)
    }

    operator fun times(other: Matrix): Matrix {
        require(this.data[0].size == other.data.size) {
            "Матрицы нельзя перемножить: ${this.data[0].size} != ${other.data.size}"
        }
        val result = Matrix(this.data.size, other.data[0].size, 0.0)
        for (i in this.data.indices) {
            for (j in other.data[0].indices) {
                var sum = 0.0
                for (k in this.data[0].indices) {
                    sum += this[i, k] * other[k, j]
                }
                result[i, j] = sum
            }
        }
        return result
    }

    operator fun times(scalar: Double): Matrix {
        val result = Matrix(data)
        for (row in data.indices) {
            for (col in data[row].indices) {
                result[row, col] = result[row, col] * scalar
            }
        }
        return result
    }

    fun generateRandomNoise(): Matrix {
        val rows = getRowsNum()
        val cols = getColumnsNum()

        val result: MutableList<MutableList<Double>> = data.map { it.toMutableList() }.toMutableList()

        for(i in result.indices){
            val nR = Random.nextInt().absoluteValue % result[i].size
            for(j in 0 ..< nR) {
                result[Random.nextInt().absoluteValue % rows][Random.nextInt().absoluteValue % cols] =
                    if (Random.nextBoolean()) 1.0 else 0.0
            }
        }

        return Matrix(result.toList())
    }

    fun getColumn(index: Int): Matrix {
        require(index in data[0].indices) { "Индекс столбца выходит за границы матрицы." }
        return Matrix(data.map { row -> listOf(row[index])})
    }

    fun getRowAsMatrix(index: Int): Matrix {
        require(index in data.indices) { "Индекс строки выходит за границы матрицы." }
        return Matrix(listOf(data[index]))
    }

    fun countDifferences(other: Matrix): Int {
        require(data.size == other.data.size && data[0].size == other.data[0].size) {
            "Размеры матриц не совпадают для сравнения."
        }

        var count = 0
        for (row in data.indices) {
            for (col in data[row].indices) {
                if (data[row][col] != other[row, col]) {
                    count++
                }
            }
        }
        return count
    }

    fun transpose(): Matrix {
        val result = Matrix(data[0].size, data.size, 0.0)
        for (row in data.indices) {
            for (col in data[row].indices) {
                result[col, row] = this[row, col]
            }
        }
        return result
    }

    fun zero(): Matrix {
        return Matrix(data.map { col -> col.map { 0.0 }.toMutableList() }.toMutableList())
    }

    fun toListOfLists(): List<List<Double>> {
        return data.map { it.toList() }
    }

    private fun checkSizes(other: Matrix) {
        require(
            data.size == other.data.size
                    && data[0].size == other.data[0].size
        ) { "Размеры матриц не совпадают для сложения." }
    }

    fun copy(): Matrix {
        return Matrix(data.map { row -> row.toList() })
    }

    override fun toString(): String {
        return data.joinToString(separator = "\n") { row ->
            row.joinToString(separator = " ") { "%.0f".format(it) }
        }
    }

    fun getFormattedRow(rowIndex: Int, highlightIndex: Int): String {
        require(rowIndex in data.indices) { "Индекс строки выходит за границы матрицы." }
        require(highlightIndex in data[rowIndex].indices) { "Индекс элемента выходит за границы строки." }

        return data[rowIndex].mapIndexed { index, value ->
            if (index == highlightIndex) "(${value.toInt()})" else value.toInt().toString()
        }.joinToString(" ")
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix) return false

        if (this.data.size != other.data.size || this.data[0].size != other.data[0].size) return false

        // Проверяем содержимое
        for (i in data.indices) {
            for (j in data[i].indices) {
                if (this[i, j] != other[i, j]) return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}
