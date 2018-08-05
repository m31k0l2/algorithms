class Event(var id: String, var value: Boolean = false) {
    val parents = mutableListOf<Event>()
    var probability = 0.0
    infix operator fun plus(event: Event) =  Event("$id + ${event.id}").also {
        it.parents.add(this)
        it.parents.add(event)
    }

    override fun toString() = "$id${if (parents.isEmpty()) "=$value" else " $parents"}${if (probability > 0) ", $probability" else ""}"
}

data class BayesNode(val id: String, val values: List<Event>, var value: Boolean = false) {
    private val parents = mutableListOf<BayesNode>()
    private val children = mutableListOf<BayesNode>()
    val probability: Double
        get() {
            return if (parents.isEmpty() || values.map { it.parents.isEmpty() }.reduce { a, b -> a && b }) values.first { it.value == value }.probability
            else {
                val parentsValues = parents.map { it.value }
                val p = values.first { event -> event.parents.map { it.value } == parentsValues }.probability
                if (value) p else 1 - p
            }
        }

    fun addParents(vararg parents: BayesNode): Boolean {
        parents.forEach { it.children.add(this) }
        return this.parents.addAll(parents.toList())
    }
}

fun event(id: String, value: Boolean, p: Double) = Event(id, value).apply { probability=p }

fun burglary(value: Boolean, p: Double) = event("burglary", value, p)
fun earthquake(value: Boolean, p: Double) = event("earthquake", value, p)
fun alarm(burglary: Boolean, earthquake: Boolean, p: Double) = (burglary(burglary, 0.0) + earthquake(earthquake, 0.0)).apply { id = "alarm" }.apply { probability=p }
fun johnCalls(value: Boolean, p: Double) = event("johnCalls", value, p).apply { parents.add(Event("alarm", value)) }
fun maryCalls(value: Boolean, p: Double) = event("maryCalls", value, p).apply { parents.add(Event("alarm", value)) }

class Network(private val nodes: List<BayesNode>) {
    private val probability: Double
        get() = nodes.map { it.probability }.reduce { p1, p2 -> p1*p2 }

    private fun setState(vararg x: Boolean) {
        nodes.take(x.size).forEachIndexed { index, node ->
            node.value = x[index]
        }
    }

    fun probability(vararg x: Boolean): Double {
        setState(*x)
        return probability
    }

    fun ask(data: Map<String, Boolean?>): List<Double> {
        val root = ProbabilityGraphNode(1.0)
        var cur = listOf(root)
        nodes.forEach { bayesNode ->
            val next = mutableListOf<ProbabilityGraphNode>()
            val value = data[bayesNode.id]
            if (value == true || value == null) {
                next.addAll(cur.map { setState(*it.state.toBooleanArray(), true)
                    ProbabilityGraphNode(bayesNode.probability, true, it) })
            }
            if (value == false || value == null) {
                bayesNode.value = false
                next.addAll(cur.map { setState(*it.state.toBooleanArray(), false)
                    ProbabilityGraphNode(bayesNode.probability, false, it) })
            }
            cur = next
        }
        return normalize(root.children.map { it.value })
    }

    private fun normalize(x: List<Double>): List<Double> {
        val sum = x.sum()
        return x.map { it / sum }
    }

    class ProbabilityGraphNode(val p: Double, value: Boolean? = null, parent: ProbabilityGraphNode? = null) {
        val children = mutableListOf<ProbabilityGraphNode>()
        val state: List<Boolean>
        val value: Double
            get() =  if (children.isEmpty()) p else children.map { it.value }.sum()*p
        init {
            parent?.children?.add(this)
            state = if (value != null) {
                parent?.state?.toMutableList().apply { this?.add(value) } ?: listOf(value)
            } else emptyList()
        }
    }
}

val burglaryNode = BayesNode("burglary", listOf(
        burglary(true, 0.001),
        burglary(false, 0.999)
))
val earthquakeNode = BayesNode("earthquake", listOf(
        earthquake(true, 0.002),
        earthquake(false, 0.998)
))
val alarmNode = BayesNode("alarm", listOf(
        alarm(false, false, 0.001),
        alarm(false, true, 0.29),
        alarm(true, false, 0.94),
        alarm(true, true, 0.95)
)).apply { addParents(burglaryNode, earthquakeNode) }
val johnCallsNode = BayesNode("johnCalls", listOf(
        johnCalls(true, 0.9),
        johnCalls(false, 0.05)
)).apply { addParents(alarmNode) }
val maryCallsNode = BayesNode("maryCalls", listOf(
        maryCalls(true, 0.7),
        maryCalls(false, 0.01)
)).apply { addParents(alarmNode) }

fun main(args: Array<String>) {
    val nw = Network(listOf(burglaryNode, earthquakeNode, alarmNode, johnCallsNode, maryCallsNode))
    val data = mapOf(
            "burglary" to null,
            "earthquake" to null,
            "alarm" to null,
            "johnCalls" to true,
            "maryCalls" to true
    )
    println(nw.ask(data))
}