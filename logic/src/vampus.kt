enum class Perception { Breeze, Stench, Glitter, Bump, Scream, Death }
enum class State { Wampus, Gold, Pit, Agent }

data class Square(var state: State?=null)

data class Position(var x: Int, var y: Int)

object World {
    private val squares = listOf(
            listOf(Square(),                Square(),           Square(State.Pit),  Square()),
            listOf(Square(),                Square(),           Square(),           Square()),
            listOf(Square(State.Wampus),    Square(State.Gold), Square(State.Pit),  Square()),
            listOf(Square(),                Square(),           Square(),           Square(State.Pit))
    )
    private var agent = Position(1, 1)

    fun print() {
        squares.asReversed().forEachIndexed { j, row ->
            val y = 4 - j
            row.forEachIndexed { i, square ->
                val x = i + 1
                if (agent == Position(x, y)) print("|A")
                else when {
                    square.state == null -> print("| ")
                    square.state == State.Pit -> print("|P")
                    square.state == State.Gold -> print("|G")
                    square.state == State.Wampus -> print("|W")
                }
            }
            println("|")
        }
    }

    fun move(dx: Int, dy: Int) {
        agent = Position(agent.x + dx, agent.y + dy)
    }

    val perceptions: List<Perception> get() {
        val result = mutableListOf<Perception>()
        val i = agent.x - 1
        val j = agent.y - 1
        val state = squares[j][i].state
        if (state == State.Wampus || state == State.Pit) return listOf(Perception.Death)
        if ( j-1 > -1 && squares[j-1][ i ].state == State.Pit ||
                j+1 <  5 && squares[j+1][ i ].state == State.Pit ||
                i-1 > -1 && squares[ j ][i-1].state == State.Pit ||
                i+1 <  5 && squares[ j ][i+1].state == State.Pit ) result.add(Perception.Breeze)
        if ( j-1 > -1 && squares[j-1][ i ].state == State.Wampus ||
                j+1 <  5 && squares[j+1][ i ].state == State.Wampus ||
                i-1 > -1 && squares[ j ][i-1].state == State.Wampus ||
                i+1 <  5 && squares[ j ][i+1].state == State.Wampus ) result.add(Perception.Stench)
        if (state == State.Gold) result.add(Perception.Glitter)
        return result
    }
}

data class Symbol(val name: String, var value: Boolean?=null)
operator fun Symbol.not(): Boolean? {
    value?.let { return !value!! } ?: return null
}
typealias Sentence = () -> Boolean?

val KB = mutableListOf<Sentence>()
val model = mutableMapOf<String, Boolean>()

fun main(args: Array<String>) {
    World.print()
    println()
    World.move(1, 0)
    World.print()
    (1..4).forEach { y -> (1..4).forEach { x -> model["P[$x,$y]"] = false } }
    (1..4).forEach { y -> (1..4).forEach { x -> model["W[$x,$y]"] = false } }
    (1..4).forEach { y -> (1..4).forEach { x -> model["B[$x,$y]"] = false } }
    (1..4).forEach { y -> (1..4).forEach { x -> model["S[$x,$y]"] = false } }
    (1..4).forEach { y -> (1..4).forEach { x -> model["G[$x,$y]"] = false } }
    val P = {x: Int, y: Int -> model["P[$x,$y]"]!!}
    val B = {x: Int, y: Int -> model["B[$x,$y]"]!!}
    // (a <=> b) = ( (a => b) л (b => a) ) устранение двухсторонней импликации
    // (а => b) = (—а v b) устранение импликации
    // ( (—а v b) л (—b v a) )
    KB.add { !P(1,1) }
    KB.add { ( !B(1,1) || P(1,2) || P(2,1) ) &&  ( B(1,1) || !P(1,2) && !P(2,1) )}
    KB.add { (!B(2,1) || P(1,1) || P(2,2) || P(3,1)) && (B(2,1) || !P(1,1) && !P(2,2) && !P(3,1)) }
    KB.add { !B(1,1) }
    KB.add { B(2,1) }
    val results = KB.map { it()!! }
    println(results)
    println(results.reduce { a, b -> a && b })

    ttCheckAll(model.keys) { P(2, 2) }
}

/*
function TT-Entails?(KB, a) returns значение true или false
    inputs: KB, база знаний - высказывание в пропозициональной логике
            а, запрос - высказывание в пропозициональной логике
    symbols <— список пропозициональных символов в КБ и a
    return TT-Check-All(KB, a, symbols, [])

function TT-Check-All(KB, a, symbols, model) returns значение true или false
    if Empty?(symbols) then
        if PL-True?(KB, model) then return PL-True?(a, model)
        else return true
    else do
        P <— First(symbols); rest <— Rest(symbols)
        return TT-Check-All(KB, a, rest, Extend(P, true, model) and
            TT-Check-All(KB, a, rest, Extend(P, false, model)
 */

fun validateModel() = KB.map { it()!! }.reduce { a, b -> a && b }

fun ttCheckAll(symbols: Set<String>, a: Sentence): Boolean {
    if (symbols.isEmpty()) {
        return if (validateModel()) a()!! else true
    }
    val symbol = symbols.first()
    val rest = symbols.toList().subList(1, symbols.size)
//    return ttCheckAll(rest)
    TODO()
}