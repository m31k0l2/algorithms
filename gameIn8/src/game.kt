import kotlin.math.max
import kotlin.math.min

object GameIn8 {
    var state = (0..8).toList().shuffled()
    val goal = (0..8).toList()
    private val neighbors = mapOf(
            0 to intArrayOf(1, 3),
            1 to intArrayOf(0, 2, 4),
            2 to intArrayOf(1, 5),
            3 to intArrayOf(0, 4, 6),
            4 to intArrayOf(1, 3, 5),
            5 to intArrayOf(2, 4, 8),
            6 to intArrayOf(3, 7),
            7 to intArrayOf(4, 6, 8),
            8 to intArrayOf(5, 7, 8)
    )

    fun show(state: List<Int>) {
        for (y in 0 until 3) {
            val row = state.subList(y*3, (y+1)*3)
            println(row.joinToString(" "))
        }
    }

    fun nextStates(state: List<Int>): List<List<Int>> {
        val curPosOfEmptyField = state.indexOf(0)
        val neighbors = neighbors[curPosOfEmptyField]!!
        return neighbors.map {
            state.swap(curPosOfEmptyField, it)
        }
    }

    private fun List<Int>.swap(a: Int, b: Int): List<Int> {
        val list = this.toMutableList()
        list[a] = this[b]
        list[b] = this[a]
        return list
    }
}



fun main(args: Array<String>) {
    val actions = Tree8().recursiveBestFirstSearch()
    var step = 0
    actions?.forEach {
        println("step ${step++}\n---")
        GameIn8.show(it)
        println()
    }
}

class Tree8 {
    private val state = mutableMapOf<Node, MutableList<Node>>()

    init {
        add(GameIn8.state)
    }

    data class Node(val state: List<Int>, private val tree: Tree8) {
        fun expand(): Set<Node> {
            val children = tree.state[this]
            if (children != null) return children.toSet()
            tree.add(state)
            return tree.state[this]!!.toSet()
        }
//
//        fun h(): Int {
//            var h = 9
//            for (i in 0..8) {
//                if (state.indexOf(i) == i) h--
//            }
//            return h
//        }

        private val e1  = when(state.indexOf(1)) { 1 -> 0; 0,2,4 -> 1; 3,5,7 -> 2; else -> 3 }
        private val e2  = when(state.indexOf(2)) { 2 -> 0; 1,5 -> 1; 0,4,8 -> 2; else -> 3 }
        private val e3  = when(state.indexOf(3)) { 3 -> 0; 0,4,6 -> 1; 1,5,7 -> 2; else -> 3 }
        private val e4  = when(state.indexOf(4)) { 4 -> 0; 1,3,5,7 -> 1; else -> 2 }
        private val e5  = when(state.indexOf(5)) { 5 -> 0; 2,4,8 -> 1; 1,3,7 -> 2; else -> 3 }
        private val e6  = when(state.indexOf(6)) { 6 -> 0; 3,7 -> 1; 0,4,8 -> 2; else -> 3 }
        private val e7  = when(state.indexOf(7)) { 7 -> 0; 4,6,8 -> 1; 1,3,5 -> 2; else -> 3 }
        private val e8  = when(state.indexOf(8)) { 8 -> 0; 5,7 -> 1; 2,4,6 -> 2; else -> 3 }

        fun h() = e1+e2+e3+e4+e5+e6+e7+e8
    }

    private fun add(nodeState: List<Int>) {
        val node = Node(nodeState, this)
        val children = state[node]
        if (children == null) state[node] = mutableListOf()
        GameIn8.nextStates(nodeState).forEach {
            state[node]!!.add(Node(it, this))
        }
    }

    fun makeNode(state: List<Int>): Node {
        val node = Node(state, this)
        GameIn8.nextStates(state).forEach { add(it) }
        return node
    }
}

fun Node8.isGoal() = treeNode.state == GameIn8.goal

data class Node8(var treeNode: Tree8.Node, var parentNode: Node8?, var g: Int) {
    var f = treeNode.h() + g
}

fun Node8.getActions(): List<List<Int>> {
    val actions = mutableListOf<List<Int>>()
    var node = this
    while (true) {
        val action = node.treeNode.state
        actions.add(action)
        node = node.parentNode ?: break
    }
    return actions.asReversed()
}

fun makeNode(treeNode: Tree8.Node, parentNode: Node8?) = Node8(treeNode, parentNode, parentNode?.g?.plus(1) ?: 0)

fun Node8.expand() = treeNode.expand().filter { it.state != parentNode?.treeNode?.state }.map { makeNode(it, this) }

fun Tree8.recursiveBestFirstSearch(): List<List<Int>>? {
    fun RBFS(node: Node8, f_limit: Int): Node8? {
        if (node.isGoal()) return node
        val successors = node.expand()
        if (successors.isEmpty()) return null
        successors.forEach {
            it.f = max(it.f, node.f)
        }
        while (true) {
            val sortedSuccessors = successors.sortedBy { it.f }
            val best = sortedSuccessors.first()
            val alternative = if (sortedSuccessors.size == 1) best else successors[1]
            node.f = best.f
            if (best.f > f_limit) return null
            val result = RBFS(best, min(f_limit, alternative.f))
            if (result != null) return result
        }
    }
    return RBFS(makeNode(makeNode(GameIn8.state), null), 30)?.getActions()
}