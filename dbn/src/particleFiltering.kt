import java.util.*

data class Individual(var value: Boolean)

fun createPopulation(p: Double, n: Int) = (1..n).map { Individual(Random().nextDouble() <= p) }

object DBN {
    fun not(e: (Boolean) -> Double) = { v: Boolean -> 1.0 - e(v) }
    val transition = { v: Boolean -> if (v) 0.7 else 0.3 }
    val evidence = { v: Boolean -> if (v) 0.9 else 0.2 }
    fun weighting(e: Boolean) = { x: Individual -> if (e) DBN.evidence(x.value) else  DBN.not(DBN.evidence)(x.value) }
}

fun main(args: Array<String>) {
    val population = createPopulation(0.7, 100000)
//    println(population)
    population.forEach {
        it.value = Random().nextDouble() <= DBN.transition(it.value)
    }
//    println(population)
    val e = false
    val weights = population.map { it.value to DBN.weighting(e)(it) }
//    println(weights)
    val nextPopulation = weights.map { (v, w) -> if (Random().nextDouble() <= w) v else !v }
//    println(nextPopulation)
    val res = nextPopulation.count { it } to nextPopulation.count { !it }
//    println(res)
    val p = ((res.first*100.0 / population.size)*100).toInt() / 100.0
    println("Дождь: p = $p %")
}