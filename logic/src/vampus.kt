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
    var agent = Position(1, 1)
        private set

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

    fun moveTo(pos: Position) {
        agent = pos
    }

    val perceptions: List<Perception> get() {
        val result = mutableListOf<Perception>()
        val i = agent.x - 1
        val j = agent.y - 1
        val state = squares[j][i].state
        if (state == State.Wampus || state == State.Pit) return listOf(Perception.Death)
        if ( j-1 > -1 && squares[j-1][ i ].state == State.Pit ||
             j+1 <  4 && squares[j+1][ i ].state == State.Pit ||
             i-1 > -1 && squares[ j ][i-1].state == State.Pit ||
             i+1 <  4 && squares[ j ][i+1].state == State.Pit ) result.add(Perception.Breeze)
        if ( j-1 > -1 && squares[j-1][ i ].state == State.Wampus ||
             j+1 <  4 && squares[j+1][ i ].state == State.Wampus ||
             i-1 > -1 && squares[ j ][i-1].state == State.Wampus ||
             i+1 <  4 && squares[ j ][i+1].state == State.Wampus ) result.add(Perception.Stench)
        if (state == State.Gold) result.add(Perception.Glitter)
        return result
    }
}

data class Symbol(val name: String, var value: Boolean?=null)
operator fun Symbol.not(): Boolean? {
    value?.let { return !value!! } ?: return null
}
typealias Model = Map<String, Boolean>
typealias AtomSentence = (Model) -> Boolean
class Sentence(private val a: AtomSentence) {
    operator fun not() = Sentence { !a(it) }
    infix fun ergo(b: Sentence) = not() or b
    infix fun iff(b: Sentence) = (this ergo b) and (b ergo this)
    infix fun or(b: Sentence) = Sentence { a(it) || b.a(it) }
    infix fun and(b: Sentence) =Sentence { a(it) && b.a(it) }
    operator fun invoke(m: Model) = a(m)
}

val KB = mutableSetOf<Sentence>()