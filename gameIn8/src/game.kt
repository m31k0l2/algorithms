import kotlin.math.max
import kotlin.math.min

object GameIn8 {
    var state = (0..8).toList().shuffled()
    val goal = (0..8).toList()
    private val neighbors = mapOf(
            0 to intArrayOf(1, 3),
            1 to intArrayOf(0, 2, 4),
            2 to intArrayOf(1, 5),
            3 to intArrayOf(0, 4, 6),
            4 to intArrayOf(1, 3, 5),
            5 to intArrayOf(2, 4, 8),
            6 to intArrayOf(3, 7),
            7 to intArrayOf(4, 6, 8),
            8 to intArrayOf(5, 7, 8)
    )

    fun show(state: List<Int>) {
        for (y in 0 until 3) {
            val row = state.subList(y*3, (y+1)*3)
            println(row.joinToString(" "))
        }
    }

    fun nextStates(state: List<Int>): List<List<Int>> {
        val curPosOfEmptyField = state.indexOf(0)
        val neighbors = neighbors[curPosOfEmptyField]!!
        return neighbors.map {
            state.swap(curPosOfEmptyField, it)
        }
    }

    private fun List<Int>.swap(a: Int, b: Int): List<Int> {
        val list = this.toMutableList()
        list[a] = this[b]
        list[b] = this[a]
        return list
    }
}