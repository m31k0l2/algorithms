import java.util.*
import kotlin.math.exp

// поиск с имуляцией отжига
fun simulatedAnnealing(problem: Model, schedule: (t: Int) -> Double): Model {
    var current = Node(problem)
    var t = 0
    while (true) {
        val T = schedule(t++)
        if (T == 0.0) return current.state
        val next = current.children.value[Random().nextInt(56)]
        val dE = current.value - next.value
        if (dE > 0) current = next
        else if (Random().nextDouble() < exp(dE/T)) current = next
    }
}

data class Node(val state: Model) {
    private val h = { m: Model -> KB.map { if (it(m)) 0 else 1 }.sum() }
    val value = h(state)

    val children: Lazy<List<Node>> = lazy {
        state.map { (s, v) ->
            val next = state.toMutableMap()
            next[s] = !v
            Node(next)
        }
    }
}

fun tell(s: Sentence) { KB.add(s) }
fun ask(m: Model, s: Sentence): Boolean {
    val best = simulatedAnnealing(m) { t -> exp(-0.05*Math.pow(t.toDouble(), 0.9)) }
    return s(best)
}

val visited = mutableListOf(Position(1,1))

fun getNeighbors(x: Int, y: Int) = listOf(
        Position(x - 1, y),
        Position(x + 1, y),
        Position(x, y - 1),
        Position(x, y + 1)
).filter { (x, y) -> x > 0 && y > 0 && x < 5 && y < 5 }

fun selectMove(positions: List<Position>): Position {
    val v = positions.filter { it in visited }
    val nv = positions.filter { it !in visited }
    var m = initModel()
    nv.forEach { (x, y) ->
        println("[$x,$y]?")
        val danger = P(x,y) or W(x,y)
        var validated = false
        for (i in 1..10) {
            m = simulatedAnnealing(m) { t -> exp(-0.05*Math.pow(t.toDouble(), 0.9)) }
            val isValid = m.validate()
            if (isValid && danger(m)) {
                println("danger: true")
                return@forEach
            } else println("danger: false")
            if (isValid) validated = true
        }
        println("validate: $validated")
        if (!validated) return@forEach
        println("[$x, $y] - ok")

        if (!danger(m)) return Position(x, y)
    }
    return v.shuffled().first()
}

fun main(args: Array<String>) {
    KB.addAll(model)
    World.print()
    println()
    while (true) {
        val (x, y) = World.agent
        if (Perception.Breeze in World.perceptions) {
            tell(B(x, y))
        }
        else tell(!B(x,y))
        if (Perception.Scream in World.perceptions) {
            tell(S(x, y))
        }
        else tell(!S(x,y))
        if (Perception.Death in World.perceptions) {
            println("Game Over!")
            System.exit(0)
        }
        if (Perception.Glitter in World.perceptions) {
            println("Win!")
            System.exit(0)
        }
        val next = selectMove(getNeighbors(x, y))
        tell(!P(x,y))
        tell(!W(x,y))
        println(next)
        World.moveTo(next)
        visited.add(next)
        World.print()
        println()
    }
}