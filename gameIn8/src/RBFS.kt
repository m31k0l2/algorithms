import kotlin.math.max
import kotlin.math.min

class Tree {
    private val state = mutableMapOf<Node, MutableList<Node>>()

    init {
        add(GameIn8.state)
    }

    data class Node(val state: List<Int>, private val tree: Tree) {
        fun expand(): Set<Node> {
            val children = tree.state[this]
            if (children != null) return children.toSet()
            tree.add(state)
            return tree.state[this]!!.toSet()
        }

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

fun Node.isGoal() = treeNode.state == GameIn8.goal

data class Node(var treeNode: Tree.Node, var parentNode: Node?, var g: Int) {
    var f = treeNode.h() + g
}

fun Node.getActions(): List<List<Int>> {
    val actions = mutableListOf<List<Int>>()
    var node = this
    while (true) {
        val action = node.treeNode.state
        actions.add(action)
        node = node.parentNode ?: break
    }
    return actions.asReversed()
}

fun makeNode(treeNode: Tree.Node, parentNode: Node?) = Node(treeNode, parentNode, parentNode?.g?.plus(1) ?: 0)

fun Node.expand() = treeNode.expand().filter { it.state != parentNode?.treeNode?.state }.map { makeNode(it, this) }

fun Tree.recursiveBestFirstSearch(): List<List<Int>>? {
    fun RBFS(node: Node, f_limit: Int): Node? {
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

fun main(args: Array<String>) {
    val actions = Tree().recursiveBestFirstSearch()
    var step = 0
    actions?.forEach {
        println("step ${step++}\n---")
        GameIn8.show(it)
        println()
    }
}