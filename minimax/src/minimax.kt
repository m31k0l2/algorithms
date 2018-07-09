import kotlin.math.max
import kotlin.math.min

object Tree {
    val root = Node("A")
    init {
        val b = Node("B", root)
        val b1 = Node("B1", b, 3)
        val b2 = Node("B2", b, 12)
        val b3 = Node("B3", b, 8)
        val c = Node("C", root)
        val c1 = Node("C1", c, 2)
        val c2 = Node("C2", c, 4)
        val c3 = Node("C3", c, 6)
        val d = Node("D", root)
        val d1 = Node("D1", d, 14)
        val d2 = Node("D2", d, 5)
        val d3 = Node("D3", d, 2)
        root.children.addAll(listOf(b, c, d))
        b.children.addAll(listOf(b1, b2, b3))
        c.children.addAll(listOf(c1, c2, c3))
        d.children.addAll(listOf(d1, d2, d3))
    }
}

var counter = 0

data class Node(val state: String, val parent: Node?=null, var value: Int=0) {
    val children = mutableListOf<Node>()
}

fun minimaxDecision(state: Node): Node {
    val v = maxValue(state)
    return state.children.first { it.value == v }
}

fun maxValue(state: Node): Int {
    counter++
    if (terminalTest(state)) return state.value
    var v = Int.MIN_VALUE
    state.children.forEach {
        v = max(v, minValue(it))
        it.value = v
    }
    return v
}

fun minValue(state: Node): Int {
    counter++
    if (terminalTest(state)) return state.value
    var v = Int.MAX_VALUE
    state.children.forEach {
        v = min(v, maxValue(it))
        it.value = v
    }
    return v
}

fun terminalTest(state: Node) = state.state in arrayOf("B1", "B2", "B3", "C1", "C2", "C3", "D1", "D2", "D3")

fun main(args: Array<String>) {
    val nextPoint = minimaxDecision(Tree.root)
    println(nextPoint.state)
    println(counter)
}