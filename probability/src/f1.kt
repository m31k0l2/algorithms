//val T = matrixOf(2, 2, 0.7, 0.3, 0.3, 0.7)
//val O = matrixOf(2, 2, 0.9, 0.0, 0.0, 0.2)
//val I = matrixOf(2, 2, 0.0, 1.0, 1.0, 0.0)
//
//fun <E>matrixOf(r: Int, c: Int, vararg x: E): Matrix<E> = Matrix(r, c, x.toList())
//
//fun main(args: Array<String>) {
//    var f = matrixOf(2, 1, 0.5, 0.5)
//    observation.forEach {
//        val Oi = if (it) O else O*I*I
//        f = Matrix(2, 1, ((T * f).t() * Oi).toList().normalize())
//    }
//    println(f)
//}

fun main(args: Array<String>) {
    val T = matrixOf(3, 3,
            0.59, 0.17, 0.24,
            0.14, 0.54, 0.32,
            0.03, 0.31, 0.66
    )
    // Райконен занял 4 место в квале
    val O0 = matrixOf(3, 3,
            0.6, 0.0, 0.0,
            0.0, 0.3, 0.0,
            0.0, 0.0, 0.1
    )
    // Райконен занял 4 место в квале
    val O1 = matrixOf(3, 3,
            0.0, 0.0, 0.0,
            0.0, 0.5, 0.0,
            0.0, 0.0, 0.5
    )
    // Райконен занял 5 место в квале
    val O2 = matrixOf(3, 3,
            0.0, 0.0, 0.0,
            0.0, 0.5, 0.0,
            0.0, 0.0, 0.5
    )
    // Райконен занял 2 место в квале
    val O3 = matrixOf(3, 3,
            0.92, 0.0, 0.0,
            0.0,  0.0, 0.0,
            0.0,  0.0, 0.08
    )
    // Райконен занял 4 место в квале
    val O4 = matrixOf(3, 3,
            0.03, 0.0, 0.0,
            0.0,  0.6, 0.0,
            0.0,  0.0, 0.37
    )
    var f = matrixOf(3, 1, 0.6, 0.3, 0.1)
    f = Matrix(3, 1, ((T * f).t() * O0).toList().normalize())
    println(f.toList().map { (it*100).toInt()/100.0 })
    f = Matrix(3, 1, ((T * f).t() * O1).toList().normalize())
    println(f.toList().map { (it*100).toInt()/100.0 })
    f = Matrix(3, 1, ((T * f).t() * O2).toList().normalize())
    println(f.toList().map { (it*100).toInt()/100.0 })
    f = Matrix(3, 1, ((T * f).t() * O3).toList().normalize())
    println(f.toList().map { (it*100).toInt()/100.0 })
    f = Matrix(3, 1, ((T * f).t() * O4).toList().normalize())
    println(f.toList().map { (it*100).toInt()/100.0 })
}

val statCvala = mapOf(
        2 to listOf(18, 16, 17),
        3 to listOf(10, 5),
        5 to listOf(2, 2),
        7 to listOf(4, 4),
        8 to listOf(6, 17),
        9 to listOf(15, 14, 19),
        11 to listOf(11, 8, 18),
        14 to listOf(13, 13),
        18 to listOf(19, 10),
        19 to listOf(7, 6),
        20 to listOf(17, 12, 20),
        26 to listOf(9, 9),
        27 to listOf(12, 7),
        30 to listOf(20, 18),
        31 to listOf(14, 20),
        33 to listOf(5, 19),
        36 to listOf(16, 15),
        44 to listOf(1, 1),
        55 to listOf(8, 11, 16),
        77 to listOf(3, 3),
        94 to listOf(18, 0)
)

val statRace = mapOf(
        2 to listOf(13, 0),
        3 to listOf(0, 4),
        5 to listOf(1, 2),
        7 to listOf(4, 5),
        8 to listOf(0, 11),
        9 to listOf(0, 15),
        11 to listOf(7, 9),
        14 to listOf(0, 0),
        18 to listOf(0, 0),
        19 to listOf(6, 14),
        20 to listOf(0, 8),
        26 to listOf(9, 0),
        27 to listOf(11, 12),
        30 to listOf(0, 13),
        31 to listOf(10, 10),
        33 to listOf(5, 3),
        36 to listOf(12, 0),
        44 to listOf(2, 1),
        55 to listOf(8, 7),
        77 to listOf(3, 6),
        94 to listOf(0, 0)
)