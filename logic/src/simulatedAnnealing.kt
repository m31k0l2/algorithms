import java.util.*
import kotlin.math.exp
import kotlin.streams.toList

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

fun getValidModels(count: Int): List<Model> {
    var m = initModel()
    return (1..count).toList().parallelStream().map {
        m = simulatedAnnealing(m) { t -> exp(-0.05*Math.pow(t.toDouble(), 0.9)) }
        if (m.validate()) m else null
    }.toList().filterNotNull()
}

fun selectMove(positions: List<Position>): Position {
    val v = positions.filter { it in visited }
    val nv = positions.filter { it !in visited }
    val models = getValidModels(5)
    if (models.isNotEmpty()) {
        for ((x, y) in nv) {
            print("[$x,$y]")
            val danger = P(x, y) or W(x, y)
            val m = models.firstOrNull { danger(it) }
            if (m != null) {
                println(" - danger")
                continue
            }
            println(" - ok")
            return Position(x, y)
        }
    }
    if (v.isEmpty()) return positions.first()
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