package matrix

import main.printOriginalsVectors

fun hopLogic(yOriginals:List<Matrix>, y: Matrix) {

    val buffValW = (y * 2.0 - 1.0).transpose() * (y * 2.0 - 1.0)
    val w = buffValW - Matrix.identity(buffValW.getRowsNum())

    printOriginalsVectors(yOriginals, "Сеть Хопфилда")

    println("2. Async example for every input vector:")
    val yAsyncList: List<Matrix> = getBestNoiseBitsHopAsync(yOriginals, w)

    val maxNumRecNoisyBit: MutableList<List<Matrix>> = mutableListOf()
    maxNumRecNoisyBit.add(yAsyncList.map { it.copy() })

    for (curY in yAsyncList.indices) {
        println("\ny${curY + 1}_original = ${yOriginals[curY]}")
        println("y'${curY + 1}_noising = ${yAsyncList[curY]}\n")
        for (stage in 1.. 10) {
            println("Stage ${stage}:")

            for (i in 0..<w.getColumnsNum()) {
                val s = yAsyncList[curY] * w.getColumn(i)
                val signS = MMath.sign(s[0, 0])
                yAsyncList[curY][0, i] = signS

                println("y_model(${i + 1}) = \t[${yAsyncList[curY].getFormattedRow(0, i)}]")
            }

            println("y_original = \t[${yOriginals[curY]}]")
            if(yOriginals[curY] == yAsyncList[curY]) {
                print("y_stage_$stage == y_original -> relaxation")
                break
            }
        }
        println()
    }

    println("\n3. Sync example for every input vector:")
    val ySyncList:MutableList<Matrix> = getBestNoiseBitsHopSync(yOriginals, w).toMutableList()

    maxNumRecNoisyBit.add(ySyncList.map { it.copy() })

    for (curY in ySyncList.indices) {
        println("\ny${curY + 1}_original = ${yOriginals[curY]}")
        println("y'${curY + 1}_noising = ${ySyncList[curY]}\n")
        for (stage in 1.. 10) {
            println("Stage ${stage}:")

            var breakBool: Boolean = false
            for (i in 0..<w.getColumnsNum()) {
                val s: Matrix = ySyncList[curY] * w
                ySyncList[curY] = MMath.sign(s)

                println("y_model(${i + 1}) = \t[${ySyncList[curY]}]")
                if(yOriginals[curY] == ySyncList[curY]){
                    breakBool = true
                    break
                }
            }

            println("y_original = \t[${yOriginals[curY]}]")
            if(breakBool) {
                print("y_stage_$stage == y_original -> relaxation")
                break
            }
        }
        println()
    }

    println("\n4. Maximum number of recognised noisy bits:")
    println("  Async:")
    maxNumRecNoisyBit[0].forEachIndexed { index, matrix -> println("\ty_$index = ${matrix.countDifferences(yOriginals[index])}") }
    println("  Sync:")
    maxNumRecNoisyBit[1].forEachIndexed { index, matrix -> println("\ty_$index = ${matrix.countDifferences(yOriginals[index])}") }
}

fun getBestNoiseBitsHopAsync(yOriginals: List<Matrix>, w: Matrix): List<Matrix> {
    var bestNoiseBits: List<Matrix> = yOriginals.map { it.copy() }

    for (mIndex in yOriginals.indices) {
        val yNoiseListOriginal: List<Matrix> = Matrix.generateRandomNoise(yOriginals[mIndex], 1000)
        val yNoiseList: List<Matrix> = yNoiseListOriginal.map { it.copy() }
        for (curY in yNoiseList.indices) {

            // TODO SHIT YOU CAN OPTIMISE
            var bestCount: Int = 0
            for (stage in 1..10) {
                for (i in 0..< w.getColumnsNum()) {
                    val s = yNoiseList[curY] * w.getColumn(i)
                    val signS = MMath.sign(s[0, 0])
                    yNoiseList[curY][0, i] = signS
                }

                if (yOriginals[mIndex] == yNoiseList[curY]
                    && bestCount < yNoiseListOriginal[curY].countDifferences(yOriginals[mIndex])
                ) {
                    bestNoiseBits = bestNoiseBits.mapIndexed { index, matrix ->
                        if (index == mIndex) yNoiseListOriginal[curY].copy() else matrix
                    }
                    bestCount = yNoiseListOriginal[curY].countDifferences(yOriginals[mIndex])
                    break
                }
            }
            // TODO SHIT YOU CAN OPTIMISE

        }
    }

    return bestNoiseBits
}

fun getBestNoiseBitsHopSync(yOriginals: List<Matrix>, w: Matrix): List<Matrix> {
    var bestNoiseBits: List<Matrix> = yOriginals.map { it.copy() }

    for (mIndex in yOriginals.indices) {
        val yNoiseListOriginal: List<Matrix> = Matrix.generateRandomNoise(yOriginals[mIndex], 1000)
        val yNoiseList: MutableList<Matrix> = yNoiseListOriginal.map { it.copy() }.toMutableList()
        for (curY in yNoiseList.indices) {

            // TODO SHIT YOU CAN OPTIMISE
            var bestCount: Int = 0
            for (stage in 1..10) {
                var breakBool: Boolean = false
                for (i in 0..< w.getColumnsNum()) {
                    val s: Matrix = yNoiseList[curY] * w
                    yNoiseList[curY] = MMath.sign(s)

                    if (yOriginals[mIndex] == yNoiseList[curY]
                        && bestCount < yNoiseListOriginal[curY].countDifferences(yOriginals[mIndex])
                    ) {
                        bestNoiseBits = bestNoiseBits.mapIndexed { index, matrix ->
                            if (index == mIndex) yNoiseListOriginal[curY].copy() else matrix
                        }
                        bestCount = yNoiseListOriginal[curY].countDifferences(yOriginals[mIndex])
                        breakBool = true
                        break
                    }
                }
                if(breakBool) break
            }
            // TODO SHIT YOU CAN OPTIMISE

        }
    }

    return bestNoiseBits
}