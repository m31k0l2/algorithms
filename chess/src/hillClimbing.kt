// восхождение к вершине
fun hillClimbing(problem: List<Int>): List<Int> {
    var current = Node(problem)
    while (true) {
        val neighbor = current.children.value.minBy { it.value }!!
        if (neighbor.value >= current.value) return current.state
        current = neighbor
    }
}

fun main(args: Array<String>) {
    val best = hillClimbing(Chess.state)
    Chess.show(best)
    println(Chess.h(best))
}