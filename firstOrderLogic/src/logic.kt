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
    operator fun not() = Statement { !state() }
    infix fun ergo(other: Statement) = not() or other
    infix fun iff(other: Statement) = (this ergo other) and (other ergo this)
}
val facts = mutableListOf<Statement>()

data class Pit(val x: Int, val y: Int): Statement()

data class Position(val x: Int, val y: Int)

class Adjacent(private val p1: Position, private val p2: Position): Statement({
    val (x, y) = p2
    p1 in arrayOf(Position(x-1, y), Position(x+1, y), Position(x, y-1), Position(x, y+1))
})

data class Breezy(val x: Int, val y: Int): Statement()

val positions = (1..4).map { x -> (1..4).map { y -> Position(x, y) } }.flatten()

fun each(s: (Int, Int) -> Statement) = positions.map { (x, y) -> s(x, y) }.onEach { println(it(facts)) }.reduce { s1, s2 -> s1 and s2 }

fun main(args: Array<String>) {
    facts.add(!Pit(1,1))
    facts.add(!Breezy(1, 1))
    val t0 = !Pit(1,1)
//    println(t0(facts))
    val a = 2; val b = 1
    facts.add(!Pit(a,b))
//    println((!Breezy(1,1) iff (Adjacent(Position(1,1), Position(a,b)) and !Pit(a, b)))(facts) )
//    facts.addAll(each { x, y -> !Breezy(x,y) iff (each { a, b -> Adjacent(Position(x,y), Position(a,b)) and !Pit(a, b)) })
    val t = each { x, y -> !Breezy(x,y) iff (Adjacent(Position(x,y), Position(a,b)) and !Pit(a, b)) }
    positions.filter { (x, y) -> (!Breezy(x,y) iff (Adjacent(Position(x,y), Position(a,b)) and !Pit(a, b)))(facts) }.forEach { println(it) }
//    println(t(facts))
    val f1 = !Breezy(3,3)
    println(f1(facts))
    val f2 = Adjacent(Position(3,3), Position(a,b))
    println(f2(facts))
    val f3 =!Pit(a, b)
    println(f3(facts))
    val f4 = f2 and f3
    println(f4(facts))
    val f5 = !f1 or f4
    println(f5(facts))
}