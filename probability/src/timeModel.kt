val transition = listOf(0.7, 0.3) // P(Rt)
val perception = listOf(0.9, 0.2) // P(Ut)
var f = listOf(0.5, 0.5)
val observation = listOf(true, true, false, false, false, false)


private operator fun List<Double>.times(list: List<Double>) = zip(list).map { (a, b) -> a*b }
private operator fun List<Double>.plus(list: List<Double>) = zip(list).map { (a, b) -> a+b }
private operator fun Double.times(list: List<Double>) = list.map { it * this }
private fun List<Double>.normalize() = map { it / sum() }

fun main(args: Array<String>) {
    observation.forEach {
        f = forward(f, it)
    }
    println(f)
}

fun forward(ft: List<Double>, e: Boolean) = if (e) {
    val prediction = ft[0] * transition + ft[1] * transition.asReversed()
    (perception * prediction).normalize()
} else {
    val prediction = ft[0] * transition.asReversed() + ft[1] * transition
    (perception.asReversed() * prediction).normalize()
}


