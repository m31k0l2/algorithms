import java.util.*

class Node(val id: String, val children: MutableSet<Node> = mutableSetOf(), var parents: MutableList<Node> = mutableListOf()) {
    var value = false
    lateinit var probability: (List<Boolean>) -> Double

    fun addChildren(vararg nodes: Node) {
        children.addAll(nodes)
        nodes.forEach { it.parents.add(this) }
    }

    fun p() = probability(parents.map { it.value })

    override fun toString() = "$id${if (parents.isNotEmpty()) " $parents" else ""}"
}

val cloudy = Node("Cloudy")
val sprinkler = Node("Sprinkler")
val rain = Node("Rain")
val wetGrass = Node("WetGrass")

fun init() {
    cloudy.addChildren(sprinkler, rain)
    sprinkler.addChildren(wetGrass)
    rain.addChildren(wetGrass)
    sprinkler.probability = { v -> when(v) {
        listOf(true) -> 0.1
        else -> 0.5
    }}
    rain.probability = { v -> when(v) {
        listOf(true) -> 0.8
        else -> 0.2
    }}
    wetGrass.probability = { v -> when(v) {
        listOf(true, true) -> 0.99
        listOf(true, false) -> 0.9
        listOf(false, true) -> 0.9
        else -> 0.0
    }}
    cloudy.probability = { _ -> 0.5 }
}

val counter = mutableMapOf<List<Boolean>, Int>()

private fun randomSelect(node: Node) {
    node.randomValue()
    node.children.forEach {
        if (it.parents.size == 1 || it.parents.indexOf(node) == it.parents.size-1)
            randomSelect(it)
    }
}

fun getModel() = listOf(cloudy.value, sprinkler.value, rain.value, wetGrass.value)

fun ask(cloudy: Boolean?=null, sprinkler: Boolean?=null, rain: Boolean?=null, wetGrass: Boolean?=null): Double {
    var filterCounter: Map<List<Boolean>, Int> = counter
    if (cloudy != null) filterCounter = filterCounter.filter { it.key[0] == cloudy }
    if (sprinkler != null) filterCounter = filterCounter.filter { it.key[1] == sprinkler }
    if (rain != null) filterCounter = filterCounter.filter { it.key[2] == rain }
    if (wetGrass != null) filterCounter = filterCounter.filter { it.key[3] == wetGrass }
    return filterCounter.values.sum().toDouble() / counter.values.sum()
}

fun priorSample(root: Node, n: Int) {
    for (i in 1..n) {
        randomSelect(root)
        val model = getModel()
        counter[model]?.let { counter[model] = it + 1 } ?: run {
            counter.put(model, 1)
        }
    }
}

fun main(args: Array<String>) {
    init()
    priorSample(cloudy, 1000000)
    val r1 = ask(null, true, true, true)
    val r2 = ask(null, true, false, true)
    println(listOf(r1, r2).normalize())
}

fun Node.randomValue() {
    value = Random().nextDouble() < p()
}
