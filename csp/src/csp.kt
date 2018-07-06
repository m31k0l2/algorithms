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

fun selectState(curState: State?): State {
    if (curState == null) return State.values()[Random().nextInt(State.values().size)]
    val neighbors = statesMap[curState]!!
    val usedStates = Tree.branch.map { it.state }
    var freeStates = neighbors.filter { it !in usedStates }
    if (freeStates.isEmpty()) {
        freeStates = State.values().filter { it !in usedStates }
    }
    return freeStates[Random().nextInt(freeStates.size)]
}

fun selectColors(state: State): List<Color> {
    val neighbors = statesMap[state]!!
    val usedColors = Tree.branch.filter { it.state in neighbors }.map { it.color }.toSet()
    return Color.values().filter { it !in usedColors }
}

fun madeChildren(node: Node, colors: List<Color>) = colors.map { Node(node.state, it) }

fun selectNextNode(node: Node?): Node {
    if (node == null) {
        val allStates = State.values()
        val allColors = Color.values()
        val state = allStates[Random().nextInt(allStates.size)]
        val color = allColors[Random().nextInt(allColors.size)]
        return Node(state, color)
    }
    return node.children.removeAt(node.children.size-1)
}

fun recursiveBackTracking(parent: Node?): Node? {
//    val node = selectNextNode(parent)
    val nextState = selectState(parent?.state)
    val availableColors = selectColors(nextState)
    return if (availableColors.isEmpty()) {
        parent?.parent
    } else {
        val node = Node(nextState, availableColors[Random().nextInt(availableColors.size)])
        node.parent = parent
        node.children.addAll(madeChildren(node, availableColors.filter { it != node.color }))
        Tree.branch.add(node)
        recursiveBackTracking(node)
    }
}


fun backtrackingSearch() {
    recursiveBackTracking(null)
}

fun main(args: Array<String>) {
    backtrackingSearch()
}