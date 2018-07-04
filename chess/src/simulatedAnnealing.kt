import java.util.*
import kotlin.math.exp

// поиск с имуляцией отжига
fun simulatedAnnealing(problem: List<Int>, schedule: (t: Int) -> Double): List<Int> {
    var current = Node(problem)
    var t = 0
    while (true) {
        val T = schedule(t++)
        if (T == 0.0) return current.state
        val next = current.children.value[Random().nextInt(56)]
        val dE = current.value - next.value
        if (dE > 0) current = next
        else if (Random().nextDouble() < exp(dE/T)) current = next
    }
}

fun main(args: Array<String>) {
    val best = simulatedAnnealing(Chess.state) { t -> exp(-0.05*Math.pow(t.toDouble(), 0.9))}
    Chess.show(best)
    println(Chess.h(best))
}