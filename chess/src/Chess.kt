import java.util.*
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

data class Place(val x: Int, val y: Int) {
    override fun toString() = "[$x, $y]"
}

typealias Individual = List<Int>

object Chess {
    val state = generateState()

    fun generateState() = List(8) { Random().nextInt(8) }

    fun show(state: List<Int>) {
        state.map { pos ->
            List(8) { if (it == pos) "[#]" else "[ ]"}
        }.forEach {
            println(it.joinToString(""))
        }
    }

    fun h(state: List<Int>): Int {
        fun getSteps(place: Place): Set<Place> {
            val a = List(min(place.x, place.y)) { place.x - (1+it) to place.y - (1+it) }
            val b = List(7-max(place.x, place.y)) { place.x + (1+it) to place.y + (1+it) }
            val c = List(min(7-place.x, place.y)) { place.x + (1+it) to place.y - (1+it) }
            val d = List(min(place.x, 7-place.y)) { place.x - (1+it) to place.y + (1+it) }
            val e = List(place.x) { place.x - (1+it) to place.y }
            val f = List(7-place.x) { place.x + (1+it) to place.y }
            val g = List(place.y) { place.y to place.y - (1+it) }
            val h = List(7-place.y) { place.x to place.y + (1+it) }
            return a.union(b).union(c).union(d).union(e).union(f).union(g).union(h).map { (x, y) -> Place(x, y) }.toSet()
        }
        var counter = 0
        val queenPositions = state.mapIndexed { y, x -> Place(x, y) }.toMutableList()
        do {
            val cur = queenPositions.removeAt(0)
            val steps = getSteps(cur)
            counter += queenPositions.count { it in steps }
        } while (queenPositions.isNotEmpty())
        return counter
    }
}

class Node(val state: List<Int>) {
    private fun next(x: Int, y: Int): Node {
        val next = state.toMutableList()
        next[y] = x
        return Node(next)
    }

    val value = Chess.h(state)

    val children: Lazy<List<Node>> = lazy {
        (0..7).flatMap {
            y -> (0..7).mapNotNull { x ->
                if (state[y] != x) next(x, y) else null
            }
        }
    }
}

// восхождение к вершине
fun hillClimbing(problem: List<Int>): List<Int> {
    var current = Node(problem)
    while (true) {
        val neighbor = current.children.value.minBy { it.value }!!
        if (neighbor.value >= current.value) return current.state
        current = neighbor
    }
}

// поиск с имуляцией отжига
fun simulatedAnnealing(problem: List<Int>, schedule: (t: Int) -> Double): List<Int> {
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

// Генетический алгоритм
fun geneticAlgorithm(population: List<Individual>, fitnessFn: (Individual) -> Int): Individual {
    fun fitPopulation(population: List<Individual>): List<Pair<Double, Individual>> {
        val generation = population.map { fitnessFn(it) to it }
        val sum = generation.map { it.first }.sum().toDouble()
        return generation.map { it.first/sum to it.second }.sortedBy { it.first }
    }
    fun randomSelection(generation: List<Pair<Double, Individual>>): Individual {
        val rnd = Random().nextDouble()
        var sum = 0.0
        generation.forEach { (rate, individual) ->
            sum += rate
            if (sum > rnd) return individual
        }
        return generation.last().second
    }

    fun reproduce(x: Individual, y: Individual): Individual {
        val n = x.size
        val c = Random().nextInt(n)
        return listOf(x.subList(0, c), y.subList(c, n)).flatten()
    }

    fun mutate(child: Individual): Individual {
        val next = child.toMutableList()
        next[Random().nextInt(next.size)] = Random().nextInt(8)
        return next
    }

    fun doEpoch(population: List<Individual>): List<Individual> {
        val mutantRatio = 0.5
        val generation = fitPopulation(population)
        return (0 until population.size).map {
            val x = randomSelection(generation)
            val y = randomSelection(generation)
            var child = reproduce(x, y)
            if (Random().nextDouble() < mutantRatio) {
                child = mutate(child)
            }
            child
        }
    }

    var curPopulation = population
    var time = 0
    var bestIndividual: Individual
    do {
        curPopulation = doEpoch(curPopulation)
        val (rate, best) = curPopulation.map { fitnessFn(it) to it }.maxBy { it.first }!!
        bestIndividual = best
    } while (time++ < 1000 || rate == 28)
    return bestIndividual
}

fun main(args: Array<String>) {
//    val best = simulatedAnnealing(Chess.state) { t -> exp(-0.05*Math.pow(t.toDouble(), 0.9))}
    val population = List(40) { Chess.generateState() }
    val best = geneticAlgorithm(population) { 28-Chess.h(it) }
    Chess.show(best)
    println(Chess.h(best))
}