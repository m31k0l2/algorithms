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
    B(2,1) iff (P(2,2) or P(3,1)),
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

fun ttEntails(a: Sentence) = ttCheckAll(symbols, emptyMap(), a)

fun Model.validate() = KB.map { it(this) }.reduce { a, b -> a && b }

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