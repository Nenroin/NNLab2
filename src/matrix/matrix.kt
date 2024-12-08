package matrix

class Matrix(private var _data: List<List<Double>>) {
    constructor(rows: Int, cols: Int, defaultValue: Double) : this(List(rows) { List(cols) { defaultValue } })

    companion object {
        fun identity(size: Int): Matrix {
            val result = Matrix(size, size, 0.0)
            for (i in 0..<size) {
                result[i, i] = 1.0
            }
            return result
        }
    }

    operator fun get(row: Int, col: Int): Double = _data[row][col]
    fun getRowsNum(): Int = _data.size
    fun getColumnsNum(): Int = _data[0].size

    operator fun set(row: Int, col: Int, value: Double) {
        _data = _data.mapIndexed { rIndex, r ->
            if (rIndex == row) r.mapIndexed { cIndex, c -> if (cIndex == col) value else c } else r
        }
    }

    operator fun plus(other: Matrix): Matrix {
        checkSizes(other)
        val result = Matrix(_data)
        for (row in _data.indices) {
            for (col in _data[row].indices) {
                result[row, col] = result[row, col] + other[row, col]
            }
        }
        return result
    }

    operator fun minus(other: Matrix): Matrix {
        checkSizes(other)
        val result = Matrix(_data)
        for (row in _data.indices) {
            for (col in _data[row].indices) {
                result[row, col] = result[row, col] - other[row, col]
            }
        }
        return result
    }

    operator fun plus(scalar: Double): Matrix {
        val result = Matrix(_data)
        for (i in _data.indices) {
            for (j in _data[i].indices) {
                result[i, j] += scalar
            }
        }
        return result
    }

    operator fun minus(scalar: Double): Matrix {
        val result = Matrix(_data)
        return result + (-scalar)
    }

    operator fun times(other: Matrix): Matrix {
        require(this._data[0].size == other._data.size) {
            "Матрицы нельзя перемножить: ${this._data[0].size} != ${other._data.size}"
        }
        val result = Matrix(this._data.size, other._data[0].size, 0.0)
        for (i in this._data.indices) {
            for (j in other._data[0].indices) {
                var sum = 0.0
                for (k in this._data[0].indices) {
                    sum += this[i, k] * other[k, j]
                }
                result[i, j] = sum
            }
        }
        return result
    }

    operator fun times(scalar: Double): Matrix {
        val result = Matrix(_data)
        for (row in _data.indices) {
            for (col in _data[row].indices) {
                result[row, col] = result[row, col] * scalar
            }
        }
        return result
    }

    fun getColumn(index: Int): Matrix {
        require(index in _data[0].indices) { "Индекс столбца выходит за границы матрицы." }
        return Matrix(_data.map { row -> listOf(row[index])})
    }

    fun getRowAsMatrix(index: Int): Matrix {
        require(index in _data.indices) { "Индекс строки выходит за границы матрицы." }
        return Matrix(listOf(_data[index]))
    }

    fun countDifferences(other: Matrix): Int {
        require(_data.size == other._data.size && _data[0].size == other._data[0].size) {
            "Размеры матриц не совпадают для сравнения."
        }

        var count = 0
        for (row in _data.indices) {
            for (col in _data[row].indices) {
                if (_data[row][col] != other[row, col]) {
                    count++
                }
            }
        }
        return count
    }

    fun transpose(): Matrix {
        val result = Matrix(_data[0].size, _data.size, 0.0)
        for (row in _data.indices) {
            for (col in _data[row].indices) {
                result[col, row] = this[row, col]
            }
        }
        return result
    }

    fun zero(): Matrix {
        return Matrix(_data.map { col -> col.map { 0.0 }.toMutableList() }.toMutableList())
    }

    fun toListOfLists(): List<List<Double>> {
        return _data.map { it.toList() }
    }

    private fun checkSizes(other: Matrix) {
        require(
            _data.size == other._data.size
                    && _data[0].size == other._data[0].size
        ) { "Размеры матриц не совпадают для сложения." }
    }

    fun copy(): Matrix {
        return Matrix(_data.map { row -> row.toList() })
    }

    override fun toString(): String {
        return _data.joinToString(separator = "\n") { row ->
            row.joinToString(separator = " ") { "%.0f".format(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix) return false

        if (this._data.size != other._data.size || this._data[0].size != other._data[0].size) return false

        // Проверяем содержимое
        for (i in _data.indices) {
            for (j in _data[i].indices) {
                if (this[i, j] != other[i, j]) return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        return _data.hashCode()
    }
}

