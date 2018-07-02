import java.util.*

object Chess {
    val state = List(8) { Random().nextInt(8) }
    fun show() {
        state.map { pos ->
            List(8) { if (it == pos) "[#]" else "[ ]"}
        }.forEach {
            println(it.joinToString(""))
        }
    }
}

fun main(args: Array<String>) {
    println(Chess.state)
    Chess.show()
}