fun ask(x: Node, vararg dataSet: Pair<Node, Boolean>): List<Double> {
    return likelyHoodWeighting(x, cloudy, dataSet.map { (node, v) -> node.apply { value = v } }.toSet(), 100000)
}

fun likelyHoodWeighting(x: Node, root: Node, dataSet: Set<Node>, n: Int): List<Double> {
    val xW = mutableMapOf(true to 0.0, false to 0.0)
    for (i in 1..n) {
        val w = randomSelect(root, dataSet, 1.0)
        xW[x.value] = xW[x.value]!! + w
    }
    return xW.values.toList().normalize()
}

private fun randomSelect(node: Node, dataSet: Set<Node>, weight: Double): Double {
    var w = weight
    if (node !in dataSet) node.randomValue()
    else w *= node.p()
    node.children.forEach {
        if (it.parents.size == 1 || it.parents.indexOf(node) == it.parents.size-1)
            w = randomSelect(it, dataSet, w)
    }
    return w
}

fun main(args: Array<String>) {
    init()
    val answer = ask(rain, sprinkler to true, wetGrass to true)
    println(answer)
}