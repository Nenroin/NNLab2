package matrix

class MMath {
    companion object {
        fun sign(value: Double): Double {
            return if (value > 0.0) {
                1.0
            } else {
                0.0
            }
        }

        fun sign(value: Matrix): Matrix {
            val result: Matrix = value.copy()
            for (i in 0..< value.getRowsNum()) {
                for (j in 0..< value.getColumnsNum()){
                    result[i, j] = sign(result[i, j])
                }
            }
            return result
        }
    }
}