class Matrix<E>(private val row: Int, private val col: Int, init: List<E>) {
    private val init: List<E>
    init {
        if (init.size != row*col) throw Exception("Количество элементов матрицы не соответствует row * col")
        this.init = init.toList()
    }
    operator fun get(i: Int, j: Int): E {
        if (i < 0 || j < 0) throw Exception("Отрицательные границы")
        if (i >= row) throw Exception("Выход за границы")
        return if (j < col) init[i*col+j] else throw Exception("Выход за границы")
    }

    override fun toString(): String {
        var s = ""
        for (i in 0 until row) {
            for (j in 0 until col) {
                s += "${get(i, j)} "
            }
            s = s.trim() + "\n"
        }
        return s.trim()
    }

    fun t() = Matrix(col, row, (0 until col).flatMap { i -> List(row) { j -> init[j*col+i] } })
    @Suppress("UNCHECKED_CAST")
    operator fun times(m: Matrix<E>): Matrix<E> {
        if (col != m.row) throw Exception("Операция не определена")
        val result = (0 until m.col).flatMap { k ->
            (0 until row).map { i ->
                (0 until col).map { j ->
                    val a = get(i, j) as? Number
                            ?: throw Exception("Не могу перемножить матрицы, матрицы должны состоять из чисел")
                    val b = m[j, k] as? Number
                            ?: throw Exception("Не могу перемножить матрицы, матрицы должны состоять из чисел")
                    a * b
                }.reduce { a, b -> a + b }
            }
        }.map { it as E }
        return Matrix(row, m.col, result)
    }

    @Suppress("UNCHECKED_CAST")
    infix fun dot(m: Matrix<E>) = Matrix(row, col, init.mapIndexed { i, a -> ((a as Number)*(m.init[i] as Number)) as E })

    fun toList() = init
}

private operator fun Number.plus(y: Number): Number {
    return if (this is Int && y is Int) toInt() + y.toInt()
    else this.toDouble() + y.toDouble()
}

private operator fun Number.times(y: Number): Number {
    return if (this is Int && y is Int) toInt()*y.toInt()
    else toDouble()*y.toDouble()
}

val T = matrixOf(2, 2, 0.7, 0.3, 0.3, 0.7)
val O = matrixOf(2, 2, 0.9, 0.0, 0.0, 0.2)
val I = matrixOf(2, 2, 0.0, 1.0, 1.0, 0.0)

fun <E>matrixOf(r: Int, c: Int, vararg x: E): Matrix<E> = Matrix(r, c, x.toList())

fun main(args: Array<String>) {
    var f = matrixOf(2, 1, 0.5, 0.5)
    observation.forEach {
        val Oi = if (it) O else O*I*I
        f = Matrix(2, 1, ((T * f).t() * Oi).toList().normalize())
    }
    println(f)
}
