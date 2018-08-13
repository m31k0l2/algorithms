val transition = listOf(0.7, 0.3) // P(Rt)
val perception = listOf(0.9, 0.2) // P(Ut)
val observation = listOf(true, true)


operator fun List<Double>.times(list: List<Double>) = zip(list).map { (a, b) -> a*b }
operator fun List<Double>.plus(list: List<Double>) = zip(list).map { (a, b) -> a+b }
operator fun Double.times(list: List<Double>) = list.map { it * this }
fun List<Double>.normalize() = map { it / sum() }

fun main(args: Array<String>) {
    val fv = Array(observation.size+1) { listOf(0.5, 0.5) }
    val sv = Array(observation.size+1) { listOf(0.0, 0.0) }
    observation.forEachIndexed { index, e ->
        fv[index+1] = forward(fv[index], e)
    }
    var b = listOf(1.0, 1.0)
    for (i in observation.size downTo 0) {
        sv[i] = (fv[i]*b).normalize()
        b = backward(b)
    }
    println(fv.toList().map { list -> list.map { (it*1000).toInt()/1000.0 } })
    println(sv.toList().map { list -> list.map { (it*1000).toInt()/1000.0 } })
}

fun forward(ft: List<Double>, e: Boolean) = if (e) {
    val prediction = ft[0] * transition + ft[1] * transition.asReversed()
    (perception * prediction).normalize()
} else {
    val prediction = ft[0] * transition.asReversed() + ft[1] * transition
    (perception.asReversed() * prediction).normalize()
}

fun backward(b: List<Double>) = (perception[0] * b[0]) * transition + (perception[1] * b[1]) * transition.asReversed()

