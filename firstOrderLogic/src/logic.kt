import kotlin.math.PI

open class Statement(state: (() -> Boolean)? = null) {
    companion object {
        private lateinit var model: List<Statement>
    }
    private val state = state ?: { this in model }
    operator fun invoke(model: List<Statement>): Boolean {
        Statement.model = model
        return state()
    }
    infix fun or(other: Statement) = Statement { state() || other.state() }
    infix fun and(other: Statement) = Statement { state() && other.state() }
    open operator fun not() = Statement { !state() }
    infix fun ergo(other: Statement) = not() or other
    infix fun iff(other: Statement) = (this ergo other) and (other ergo this)
}
val facts = mutableListOf<Statement>()

data class Pit(val x: Int, val y: Int): Statement()

data class Position(val x: Int, val y: Int)

class Adjacent(private val p1: Position, private val p2: Position): Statement({
    val (x, y) = p2
    p1 in arrayOf(Position(x-1, y), Position(x+1, y), Position(x, y-1), Position(x, y+1), p2)
})

data class Breezy(val x: Int, val y: Int): Statement()

val visited = mutableListOf(Position(1,1))

val kb = listOf(
        { positions: List<Position> ->
            { x: Int, y: Int -> !Breezy(x,y) iff !exist(positions) { a, b -> Adjacent(Position(x,y), Position(a,b)) and Pit(a, b) } }
        },
        { positions: List<Position> ->
            { x: Int, y: Int -> Breezy(x,y) iff exist(positions) { a, b -> Adjacent(Position(x,y), Position(a,b)) and Pit(a, b) } }
        }
)

fun each(positions: List<Position>, s: (Int, Int) -> Statement) = positions.map { (x, y) -> s(x, y) }.reduce { s1, s2 -> s1 and s2 }
fun exist(positions: List<Position>, s: (Int, Int) -> Statement)  = positions.map { (x, y) -> s(x, y) }.reduce { s1, s2 -> s1 or s2 }

//fun ask(positions: List<Position>, statement: Statement): Boolean {
//    val model = mutableListOf<Statement>()
//    model.addAll(facts)
//    model.add(statement)
//    model.addAll(kb.map { it(positions.filter { !Breezy(it.x, it.y)(model) }) })
//    println(positions)
//    return model.map { it(model) }.reduce { s1, s2 -> s1 && s2 }
//}

//fun isPit(x: Int, y: Int): Boolean? {
//    val positions = mutableListOf<Position>()
//    positions.addAll(visited)
//    positions.add(Position(x, y))
//    val isPit = ask(positions, Pit(x, y))
//    val noPit = ask(positions, !Pit(x, y))
//    return if (isPit xor noPit) isPit else null
//}

//val fields = listOf(1..4).map { x -> listOf(1..4).map { y -> Position(x, y)  } }

fun ask(curPosition: Position, nextPosition: Position, statement: Statement): Boolean {
    val positions = visited.union(listOf(nextPosition)).toList()
    val model = facts.union(kb.map { it(positions)(curPosition.x, curPosition.y) }).toMutableList()
    model.add(statement)
    val r = model.map { it(model) }
    return r.reduce { s1, s2 -> s1 and s2 }
}

fun isPit(curPosition: Position, nextPosition: Position) = ask(curPosition, nextPosition, Pit(nextPosition.x, nextPosition.y))
fun noPit(curPosition: Position, nextPosition: Position) = ask(curPosition, nextPosition, !Pit(nextPosition.x, nextPosition.y))
fun pit(curPosition: Position, nextPosition: Position) = isPit(curPosition, nextPosition) xor noPit(curPosition, nextPosition)

fun main(args: Array<String>) {
    facts.add(!Pit(1,1))
    facts.add(!Breezy(1, 1))
    println(isPit(Position(1, 1), Position(2, 1)))
    facts.add(!Pit(2,1))
    visited.add(Position(2,1))
    facts.add(Breezy(2,1))
    println(isPit(Position(2, 1), Position(3, 1)))
    println(noPit(Position(2, 1), Position(3, 1)))
    println(isPit(Position(2, 1), Position(2, 2)))
    println(isPit(Position(1, 1), Position(1, 2)))
    facts.add(!Pit(1,2))
    visited.add(Position(1,2))
    facts.add(!Breezy(1,2))
    println(isPit(Position(1, 2), Position(2, 2)))
    facts.add(!Pit(2,2))
    visited.add(Position(2,2))
    facts.add(!Breezy(2,2))
    println(isPit(Position(2, 2), Position(3, 2)))
    facts.add(!Pit(3,2))
    visited.add(Position(3,2))
    facts.add(Breezy(3,2))
    println(isPit(Position(3, 2), Position(4, 2)))
    println(isPit(Position(1, 2), Position(1, 3)))
    facts.add(!Pit(1,3))
    visited.add(Position(1,3))
    facts.add(!Breezy(1,3))
    println(isPit(Position(1, 3), Position(2, 3)))
    facts.add(!Pit(2,3))
    visited.add(Position(2,3))
    facts.add(Breezy(2,3))
    println(isPit(Position(2, 3), Position(3, 3)))
    println(isPit(Position(2, 3), Position(2, 4)))
    println(isPit(Position(1, 3), Position(1, 4)))
    facts.add(!Pit(1,4))
    visited.add(Position(1,4))
    facts.add(!Breezy(1,4))
    println(isPit(Position(1, 4), Position(2, 4)))
    facts.add(!Pit(2,4))
    visited.add(Position(2,4))
    facts.add(!Breezy(2,4))
    println(isPit(Position(2, 4), Position(3, 4)))
    facts.add(!Pit(3,4))
    visited.add(Position(3,4))
    facts.add(Breezy(3,4))
    println(noPit(Position(2, 3), Position(3, 3)))
    println(noPit(Position(3, 4), Position(4, 4)))

//    println(visited)
}