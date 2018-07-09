import kotlin.math.max
import kotlin.math.min

fun alphaBetaSearch(state: Node): Node {
    val v = maxValue(state, Int.MIN_VALUE, Int.MAX_VALUE)
    return state.children.first { it.value == v }
}

fun maxValue(state: Node, a: Int, beta: Int): Int {
    counter++
    var alpha = a
    if (terminalTest(state)) return state.value
    var v = Int.MIN_VALUE
    state.children.forEach {
        it.value = minValue(it, alpha, beta)
        v = max(v, it.value)
        if (v >= beta) return v
        alpha = max(alpha, v)
    }
    return v
}

fun minValue(state: Node, alpha: Int, b: Int): Int {
    counter++
    var beta = b
    if (terminalTest(state)) return state.value
    var v = Int.MAX_VALUE
    state.children.forEach {
        it.value = maxValue(it, alpha, beta)
        v = min(v, it.value)
        if (v <= alpha) return v
        beta = min(beta, v)
    }
    return v
}


fun main(args: Array<String>) {
    val nextPoint = alphaBetaSearch(Tree.root)
    println(nextPoint.state)
    println(counter)
}