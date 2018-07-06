import java.util.*

enum class State { WA, NT, Q, SA, NSW, V, T }
enum class Color { Red, Green, Blue }
val statesMap = mapOf(
    State.WA to listOf(State.NT, State.SA),
    State.NT to listOf(State.WA, State.SA, State.Q),
    State.SA to listOf(State.NT, State.WA, State.Q, State.NSW, State.V),
    State.Q to listOf(State.NT, State.SA, State.NSW),
    State.NSW to listOf(State.Q, State.SA, State.V),
    State.V to listOf(State.NSW, State.SA),
    State.T to emptyList()
)

data class Node(val state: State, val color: Color) {
    val children = mutableListOf<Node>()
    var parent: Node? = null
}

object Tree {
    val branch = LinkedList<Node>()
}

val badWays = mutableMapOf<List<Node>, Node>()

fun selectState(curState: State): State? {
    val neighbors = statesMap[curState]!!
    val usedStates = Tree.branch.map { it.state }
    val freeStates = State.values().filter { it !in usedStates }
    if (freeStates.isEmpty()) return null
    if (neighbors.isEmpty()) return freeStates.random()
    val freeNeighbors = freeStates.filter { it in neighbors }
    if (freeNeighbors.isEmpty()) return freeStates.random()
    return freeNeighbors.random()
}

fun selectColors(state: State): List<Color> {
    val neighbors = statesMap[state]!!
    val usedColors = Tree.branch.filter { it.state in neighbors }.map { it.color }.toSet()
    return Color.values().filter { it !in usedColors }
}

fun madeChildren(state: State, colors: List<Color>, node: Node) = colors.mapNotNull {
    val child = Node(state, it)
    if (badWays[Tree.branch] == child)  null
    else child.apply { parent = node }
}

fun recursiveBackTracking(parent: Node): Node? {
    if (isGoal()) return parent
    val node = explore(parent)
    if (node == null) {
        Tree.branch.removeAt(Tree.branch.size-1)
        badWays[Tree.branch] = parent
        return recursiveBackTracking(Tree.branch.last)
    }
    Tree.branch.add(node)
    return recursiveBackTracking(node)
}


fun backtrackingSearch(): Map<State, Color> {
    val root = Node(State.values().random(), Color.values().random())
    Tree.branch.add(root)
    var node = recursiveBackTracking(root)
    val result = mutableMapOf<State, Color>()
    while (node != null) {
        result[node.state] = node.color
        node = node.parent
    }
    return result
}

fun isGoal() = Tree.branch.size == State.values().size

fun explore(parent: Node): Node? {
    val nextState = selectState(parent.state) ?: return null
    val freeColors = selectColors(nextState)
    if (freeColors.isEmpty()) return null
    parent.children.addAll(madeChildren(nextState, freeColors, parent))
    return parent.children.random()
}

fun main(args: Array<String>) {
    println(backtrackingSearch())
}

private fun <T> Array<T>.random() = get(Random().nextInt(size))

private fun <E> List<E>.random() = get(Random().nextInt(size))
