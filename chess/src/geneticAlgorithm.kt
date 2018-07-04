import java.util.*

typealias Individual = List<Int>

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
        val mutantRatio = 0.005
        val generation = fitPopulation(population)
        return (0 until population.size).map {
            val x = randomSelection(generation)
            val y = randomSelection(generation)
            var child = reproduce(x, y)
            if (Random().nextDouble() < mutantRatio) {
                child = mutate(child)
            }
            child
        }.toList()
    }

    var curPopulation = population
    var time = 0
    var bestIndividual: Individual
    do {
        curPopulation = doEpoch(curPopulation)
        val (rate, best) = curPopulation.map { fitnessFn(it) to it }.maxBy { it.first }!!
        bestIndividual = best
        if (rate == 28) return best
    } while (time++ < 10000)
    return bestIndividual
}

fun main(args: Array<String>) {
    val population = List(1000) { Chess.generateState() }
    val best = geneticAlgorithm(population) { 28-Chess.h(it) }
    Chess.show(best)
    println(Chess.h(best))
}