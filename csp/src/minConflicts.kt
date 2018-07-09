fun initVariables(): Map<State, Color> {
    val variables = mutableMapOf<State, Color>()
    State.values().forEach {
        variables[it] = Color.values().random()
    }
    return variables
}

fun conflicts(variables: Map<State, Color>) = variables.mapNotNull { (state, color) ->
    val count = statesMap[state]!!.count { variables[it] == color }
    if (count > 0) state to count else null
}.toMap()

fun minConflictsValue(variables: Map<State, Color>, state: State, curConflictCount: Int): Color {
    val colors = Color.values().filter { it != variables[state] }
    val next = variables.toMutableMap()
    val (newColor, newConflictCount) = colors.map { next[state] = it; it to conflicts(next).values.sum() }.minBy { it.second }!!
    return if (curConflictCount < newConflictCount) variables[state]!! else newColor
}

fun minConflicts(maxSteps: Int): Map<State, Color>? {
    val variables = initVariables().toMutableMap()
    for (i in 1..maxSteps) {
        val conflicts = conflicts(variables)
        val conflictCount = conflicts.values.sum()
        if (conflictCount == 0) return variables
        val state = conflicts.keys.random()
        variables[state] = minConflictsValue(variables, state, conflictCount)
    }
    return null
}

fun main(args: Array<String>) {
    initVariables()
    minConflicts(1000)?.let {
        println(it)
    } ?: println("FAIL")
}


