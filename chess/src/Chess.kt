import java.util.*
import kotlin.math.max
import kotlin.math.min

data class Place(val x: Int, val y: Int) {
    override fun toString() = "[$x, $y]"
}

object Chess {
    val state = generateState()

    fun generateState() = List(8) { Random().nextInt(8) }

    fun show(state: List<Int>) {
        state.map { pos ->
            List(8) { if (it == pos) "[#]" else "[ ]"}
        }.forEach {
            println(it.joinToString(""))
        }
    }

    fun h(state: List<Int>): Int {
        fun getSteps(place: Place): Set<Place> {
            val a = List(min(place.x, place.y)) { place.x - (1+it) to place.y - (1+it) }
            val b = List(7-max(place.x, place.y)) { place.x + (1+it) to place.y + (1+it) }
            val c = List(min(7-place.x, place.y)) { place.x + (1+it) to place.y - (1+it) }
            val d = List(min(place.x, 7-place.y)) { place.x - (1+it) to place.y + (1+it) }
            val e = List(place.x) { place.x - (1+it) to place.y }
            val f = List(7-place.x) { place.x + (1+it) to place.y }
            val g = List(place.y) { place.y to place.y - (1+it) }
            val h = List(7-place.y) { place.x to place.y + (1+it) }
            return a.union(b).union(c).union(d).union(e).union(f).union(g).union(h).map { (x, y) -> Place(x, y) }.toSet()
        }
        var counter = 0
        val queenPositions = state.mapIndexed { y, x -> Place(x, y) }.toMutableList()
        do {
            val cur = queenPositions.removeAt(0)
            val steps = getSteps(cur)
            counter += queenPositions.count { it in steps }
        } while (queenPositions.isNotEmpty())
        return counter
    }
}