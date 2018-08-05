enum class Events {
    toothache,
    notToothache,
    catch,
    notCatch,
    cavity,
    notCavity
}

val table = mutableMapOf<Triple<Events, Events, Events>, Double>().apply {
    put(Triple(Events.cavity, Events.catch, Events.toothache), 0.108)
    put(Triple(Events.cavity, Events.notCatch, Events.toothache), 0.012)
    put(Triple(Events.cavity, Events.catch, Events.notToothache), 0.072)
    put(Triple(Events.cavity, Events.notCatch, Events.notToothache), 0.008)
    put(Triple(Events.notCavity, Events.catch, Events.toothache), 0.016)
    put(Triple(Events.notCavity, Events.notCatch, Events.toothache), 0.064)
    put(Triple(Events.notCavity, Events.catch, Events.notToothache), 0.144)
    put(Triple(Events.notCavity, Events.notCatch, Events.notToothache), 0.576)
}

fun marginalProbabilityAND(events: List<Events>): Double {
    var t = table.toMap()
    val filter = { x: Events -> t.filter { it.key.toList().contains(x) } }
    events.forEach {
       t = filter(it)
    }
    return t.values.sum()
}

fun marginalProbabilityOR(events: List<Events>): Double {
    val t = table.filter { (e, _) -> events.sumBy { if (e.toList().contains(it)) 1 else 0 } == 1 }
    return t.values.sum() + marginalProbabilityAND(events)
}

fun marginalProbabilityAND(vararg events: Events) = marginalProbabilityAND(events.toList())
fun marginalProbabilityOR(vararg events: Events) = marginalProbabilityOR(events.toList())

fun conditionalProbability(vararg events: Events, condition: List<Events>)  = marginalProbabilityAND(events.union(condition).toList()) / marginalProbabilityAND(condition)

fun main(args: Array<String>) {
    println(marginalProbabilityAND(Events.cavity))
    println(marginalProbabilityOR(Events.cavity, Events.toothache))
    println(conditionalProbability(Events.cavity, condition = listOf(Events.toothache)))
    println(conditionalProbability(Events.cavity, condition = listOf(Events.toothache, Events.catch)))
}