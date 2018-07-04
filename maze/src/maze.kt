import java.util.*

object Maze {
    private val state = mapOf(
        0 to listOf(1),
        1 to listOf(0, 4),
        2 to listOf(5),
        3 to listOf(6),
        4 to listOf(1, 7),
        5 to listOf(2, 8),
        6 to listOf(3, 7),
        7 to listOf(4, 6, 8),
        8 to listOf(5, 7)
    )
    const val exit = 2
    var pos = 6
        private set
    private const val length = 3
    val actions: Actions
        get() {
            return state[pos]!!
        }

    private fun square(pos: Int): Map<Int, List<String>> {
        val square = mutableMapOf<Int, MutableList<String>>()
        square[0] = mutableListOf("#", "#", "#")
        square[1] = mutableListOf("|", " ", "|")
        square[2] = mutableListOf("#", "#", "#")
        if (pos == exit) square[1]!![1] = "X"
        if (pos == this.pos) square[1]!![1] = "@"
        val right = state[pos+1]?.takeIf { (pos+1) % length != 0 }
        val left = state[pos-1]?.takeIf { pos % length != 0 }
        val up = state[pos-length]?.takeIf { pos > length }
        val down = state[pos+length]?.takeIf { pos < length*(length-1) }
        left?.let {
            if (pos in it) square[1]!![0] = " "
        }
        right?.let {
            if (pos in it) square[1]!![2] = " "
        }
        down?.let {
            if (pos in it) square[2]!![1] = " "
        }
        up?.let {
            if (pos in it) square[0]!![1] = " "
        }
        return square
    }

    fun show() {
        for (l in 0 until length) {
            val line = (l*length until (l+1)*length).map { square(it) }
            val k = if (l < length-1) 1 else 2
            (0..k).map { n ->
                line.mapIndexed { x, square ->
                    val m = if (x < length-1) 2 else 3
                    square[n]!!.subList(0, m)
                }.flatten().joinToString("")
            }.forEach {
                println(it)
            }
        }
    }

    fun go(action: Action) {
        pos = action
    }
}
typealias Action = Int
typealias Actions = List<Action>
typealias Position = Int

private fun <E> LinkedList<E>.pollFirstAction(actions: List<E>): E {
    val index = indexOfFirst { it in actions }
    val element = get(index)
    for (i in size-1 downTo index) removeAt(i)
    return element
}

fun onlineAgent() {
    val states = mutableMapOf<Position, Actions>()
    val backStates = LinkedList<Position>()
    var counter = 0
    do {
        val actions = Maze.actions
        val curPos = Maze.pos
        states[curPos] = actions
        val nextAction = actions.firstOrNull { states[it] == null } ?: backStates.pollFirstAction(actions)
        backStates.add(curPos)
        Maze.go(nextAction)
        Maze.show()
        counter++
    } while (Maze.pos != Maze.exit)
    println(counter)
}

fun randomWalk() {
    val states = mutableMapOf<Position, Actions>()
    val backStates = LinkedList<Position>()
    var counter = 0
    do {
        val actions = Maze.actions
        val curPos = Maze.pos
        states[curPos] = actions
        val availableActions: Actions? = actions.filter { states[it] == null }.takeIf { it.isNotEmpty() }
        val nextAction = availableActions?.get(Random().nextInt(availableActions.size)) ?: backStates.pollFirstAction(actions)
        backStates.add(curPos)
        Maze.go(nextAction)
        Maze.show()
        counter++
    } while (Maze.pos != Maze.exit)
    println(counter)
}

fun main(args: Array<String>) {
    Maze.show()
    randomWalk()
}