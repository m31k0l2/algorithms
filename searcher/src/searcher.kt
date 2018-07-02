import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

class Tree {
    private val state = mutableMapOf<Node, MutableList<Node>>()
    private val distanceMap = mutableMapOf<Pair<String, String>, Int>()

    data class Node(val id: String, private val tree: Tree) {
        fun expand() = tree.state[this]!!.toSet()
    }

    private fun add(from: String, to: String) {
        val fromNode = Node(from, this)
        val children = state[fromNode]
        if (children == null) state[fromNode] = mutableListOf()
        state[fromNode]!!.add(Node(to, this))
    }

    fun add(from: String, to: String, distance: Int) {
        add(from, to)
        add(to, from)
        distanceMap[Pair(from, to)] = distance
    }

    fun distanceBetween(from: String, to: String) = distanceMap[Pair(from, to)] ?: distanceMap[Pair(to, from)]

    fun makeNode(id: String) = Node(id, this)
}

val map = Tree().apply {
    add("Arad", "Zerind", 75)
    add("Arad", "Timisoara", 118)
    add("Arad", "Sibiu", 140)
    add("Zerind", "Oradea", 71)
    add("Oradea", "Sibiu", 151)
    add("Timisoara", "Lugoj", 111)
    add("Lugoj", "Mehadia", 70)
    add("Mehadia", "Drobeta", 75)
    add("Drobeta", "Craiova", 120)
    add("Craiova", "Rimnicu Vilcea", 146)
    add("Sibiu", "Rimnicu Vilcea", 80)
    add("Pitesti", "Rimnicu Vilcea", 97)
    add("Sibiu", "Fagaras", 99)
    add("Fagaras", "Bucharest", 211)
    add("Bucharest", "Pitesti", 101)
    add("Bucharest", "Giurgiu", 90)
    add("Bucharest", "Urziceni", 85)
    add("Urziceni", "Vaslui", 142)
    add("Vaslui", "Iasi", 92)
    add("Neamt", "Iasi", 87)
    add("Hirsova", "Urziceni", 98)
    add("Hirsova", "Eforie", 86)
    add("Craiova", "Pitesti", 138)
}
val distanceToGoal = mapOf(
        "Arad" to 366,
        "Bucharest" to 0,
        "Craiova" to 160,
        "Drobeta" to 242,
        "Eforie" to 161,
        "Fagaras" to 176,
        "Giurgiи" to 77,
        "Hirsova" to 151,
        "Iasi" to 226,
        "Lugoj" to 244,
        "Mehadia" to 241,
        "Neamt" to 234,
        "Oradea" to 380,
        "Pitesti" to 100,
        "Rimnicu Vilcea" to 193,
        "Sibiu" to 253,
        "Timisoara" to 329,
        "Urziceni" to 80,
        "Vaslui" to 199,
        "Zerind" to 374
)

fun main(args: Array<String>) {
    val r0 = measureTimeMillis {
        val solution = map.treeSearch("Arad", "Bucharest")
        println(solution)
    }
    val r1 = measureTimeMillis {
        val solution = map.depthLimitedSearch("Arad", "Bucharest", 3)
        println(solution)
    }
    val r2 = measureTimeMillis {
        val solution = map.iterativeDeepeningSearch("Arad", "Bucharest")
        println(solution)
    }
    val r3 = measureTimeMillis {
        val solution = map.recursiveBestFirstSearch("Arad", "Bucharest")
        println(solution)
    }
    println("$r0, $r1, $r2, $r3")
}

class TreeSearchException(msg: String): Exception(msg)

fun Node.isGoal(goalId: String) = goalId == state.id

data class Node(var state: Tree.Node, var parentNode: Node?, var pathCost: Int, var depth: Int, var f: Int = 0)

fun Node.getActions(): List<String> {
    val actions = mutableListOf<String>()
    var node = this
    while (true) {
        val action = node.state.id
        actions.add(action)
        node = node.parentNode ?: break
    }
    return actions.asReversed()
}

fun makeNode(state: Tree.Node, parentNode: Node?): Node {
    val cost = parentNode?.let { map.distanceBetween(it.state.id, state.id)!! } ?: 0
    return Node(state, parentNode, (parentNode?.pathCost ?: 0) + cost, parentNode?.let { it.depth + 1 } ?: 0)
}

fun Node.expand() = state.expand().filter { it.id != parentNode?.state?.id }.map { makeNode(it, this) }

// поиск в ширину
fun Tree.treeSearch(root: String, problem: String): List<String> {
    val fringe = LinkedList<Node>()
    fringe.add(makeNode(makeNode(root), null))
    while (true) {
        if (fringe.isEmpty()) throw TreeSearchException("Нет кандидатов на развёртывание")
        val node = fringe.poll()
        if (node.isGoal(problem)) return node.getActions()
        fringe.addAll(node.expand())
    }
}

// поиск в глубину с ограничением по пределу
fun Tree.depthLimitedSearch(root: String, problem: String, limit: Int): List<String>? {
    fun recursiveDLS(node: Node, goal: String, limit: Int): List<String>? {
        var cuttoffOccured = false
        when {
            node.isGoal(goal) -> return node.getActions()
            node.depth == limit -> return emptyList()
            else -> node.expand().forEach {
                val result = recursiveDLS(it, goal, limit)
                if (result == null) cuttoffOccured = true
                else if (result.isNotEmpty()) return result
            }
        }
        if (cuttoffOccured) return null
        return emptyList()
    }
    return recursiveDLS(makeNode(makeNode(root), null), problem, limit)
}

// поиск с интерактивным углублением
fun Tree.iterativeDeepeningSearch(root: String, problem: String): List<String>? {
    var limit = 0
    var result: List<String>?
    do {
        result = depthLimitedSearch(root, problem, limit++)
    } while (result != null && result.isEmpty())
    return result
}

fun Tree.recursiveBestFirstSearch(root: String, problem: String): List<String>? {
    fun RBFS(goal: String, node: Node, f_limit: Int): Node? {
        if (node.isGoal(goal)) return node
        val successors = node.expand()
        if (successors.isEmpty()) return null
        successors.forEach {
            val g = it.pathCost
            val h = distanceToGoal[it.state.id]!!
            it.f = max(g + h, node.f)
        }
        while (true) {
            val sortedSuccessors = successors.sortedBy { it.f }
            val best = sortedSuccessors.first()
            val alternative = if (sortedSuccessors.size == 1) best else successors[1]
            node.f = best.f
            if (best.f > f_limit) return null
            val result = RBFS(goal, best, min(f_limit, alternative.f))
            if (result != null) return result
        }
    }
    return RBFS(problem, makeNode(makeNode(root), null), Int.MAX_VALUE)?.getActions()
}