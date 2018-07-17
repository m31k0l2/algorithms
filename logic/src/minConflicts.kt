fun initModel(): Model = symbols.map { it to false }.toMap()
fun conflicts(model: Model): Map<String, Int> {
    val count = { m: Model -> KB.map { if (it(m)) 0 else 1 }.sum() }
    val conflicts = mutableMapOf<String, Int>()
    model.forEach { s, b ->
        val newMap = model.toMutableMap()
        newMap[s] = !b
        conflicts[s] = count(newMap)
    }
    return conflicts
}

fun minConflicts(maxSteps: Int): Map<String, Boolean>? {
    val model = initModel().toMutableMap()
    for (i in 1..maxSteps) {
        val conflicts = conflicts(model)
        val symbol = conflicts.minBy { it.value } ?: return model
        val conflictCount = symbol.value
        if (conflictCount == 0) return model
        model[symbol.key] = !model[symbol.key]!!
    }
    return null
}

fun askWithMinConflicts(s: Sentence): Boolean {
    val model = minConflicts(10000) ?: throw Exception("достигнут предел просчета модели")
    return s(model)
}