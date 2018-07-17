val symbols = listOf(
        (1..4).map { y -> (1..4).map { "P[$y,$it]" } }.flatten(),
        (1..4).map { y -> (1..4).map { "W[$y,$it]" } }.flatten(),
        (1..4).map { y -> (1..4).map { "B[$y,$it]" } }.flatten(),
        (1..4).map { y -> (1..4).map { "S[$y,$it]" } }.flatten()
).flatten()

val P = { x: Int, y: Int -> Sentence { model: Model -> model["P[$x,$y]"] ?: false } }
val B = { x: Int, y: Int -> Sentence { model: Model -> model["B[$x,$y]"] ?: false } }
val W = { x: Int, y: Int -> Sentence { model: Model -> model["W[$x,$y]"] ?: false } }
val S = { x: Int, y: Int -> Sentence { model: Model -> model["S[$x,$y]"] ?: false } }
val model = listOf(
    !P(1,1),
    B(1,1) iff (P(1,2) or P(2, 1)),
    B(2,1) iff (P(1,1) or P(2,2) or P(3,1)),
    B(3,1) iff (P(2,1) or P(3,2) or P(4,1)),
    B(4,1) iff (P(3,1) or P(4,2)),
    B(1,2) iff (P(2,2) or P(1,3)),
    B(2,2) iff (P(1,2) or P(2,1) or P(3,2) or P(2,3)),
    B(3,2) iff (P(2,2) or P(4,2) or P(3,3) or P(3,1)),
    B(4,2) iff (P(3,2) or P(4,3) or P(4,1)),
    B(1,3) iff (P(2,3) or P(1,4) or P(1,2)),
    B(2,3) iff (P(1,3) or P(2,2) or P(3,3) or P(2,4)),
    B(3,3) iff (P(2,3) or P(4,3) or P(3,4) or P(3,2)),
    B(4,3) iff (P(3,3) or P(4,4) or P(4,2)),
    B(1,4) iff (P(2,4) or P(1,3)),
    B(2,4) iff (P(1,4) or P(2,3) or P(3,4)),
    B(3,4) iff (P(2,4) or P(4,4) or P(3,3)),
    B(4,4) iff (P(3,4) or P(4,3)),
    !W(1,1),
    W(1,1) iff (S(1,2) or S(2, 1)),
    W(2,1) iff (S(1,1) or S(2,2) or S(3,1)),
    W(3,1) iff (S(2,1) or S(3,2) or S(4,1)),
    W(4,1) iff (S(3,1) or S(4,2)),
    W(1,2) iff (S(2,2) or S(1,3)),
    W(2,2) iff (S(1,2) or S(2,1) or S(3,2) or S(2,3)),
    W(3,2) iff (S(2,2) or S(4,2) or S(3,3) or S(3,1)),
    W(4,2) iff (S(3,2) or S(4,3) or S(4,1)),
    W(1,3) iff (S(2,3) or S(1,4) or S(1,2)),
    W(2,3) iff (S(1,3) or S(2,2) or S(3,3) or S(2,4)),
    W(3,3) iff (S(2,3) or S(4,3) or S(3,4) or S(3,2)),
    W(4,3) iff (S(3,3) or S(4,4) or S(4,2)),
    W(1,4) iff (S(2,4) or S(1,3)),
    W(2,4) iff (S(1,4) or S(2,3) or S(3,4)),
    W(3,4) iff (S(2,4) or S(4,4) or S(3,3)),
    W(4,4) iff (S(3,4) or S(4,3))
)

fun tell(s: Sentence) { KB.add(s) }
//private fun ask(s: Sentence) = ttEntails(s)
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
    return nv.shuffled().firstOrNull { (x, y) -> ask(!P(x,y) and !W(x,y) ) } ?: v.shuffled().first()
}

fun main(args: Array<String>) {
    KB.addAll(model)
    for (i in 1..2) {
        val (x, y) = World.agent
        if (Perception.Breeze in World.perceptions) {
            tell(B(x, y))
        }
        else tell(!B(x,y))
        if (Perception.Scream in World.perceptions) {
            tell(S(x, y))
        }
        else tell(!S(x,y))
        val next = selectMove(getNeighbors(x, y))
        tell(!P(x,y) and !W(x,y))
        println(next)
        World.moveTo(next)
        visited.add(next)
        World.print()
        println()
    }
}

fun ttEntails(a: Sentence) = ttCheckAll(symbols, emptyMap(), a)

fun Model.validate(): Boolean {
    val r = KB.map { it(this) }.reduce { a, b -> a && b }
    if (r) {
        this.forEach { println(it) }
        println()
    }
    return r
}

fun extend(symbol: String, value: Boolean, model: Model): Model {
    val newModel = model.toMutableMap()
    newModel[symbol] = value
    return newModel
}

fun ttCheckAll(symbols: List<String>, model: Model, a: Sentence): Boolean {
    if (symbols.isEmpty()) {
        return if (model.validate()) a(model) else true
    }
    val symbol = symbols.first()
    val rest = symbols.toList().subList(1, symbols.size)
    return ttCheckAll(rest, extend(symbol, true, model), a) &&
            ttCheckAll(rest, extend(symbol, false, model), a)
}