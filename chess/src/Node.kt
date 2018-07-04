class Node(val state: List<Int>) {
    private fun next(x: Int, y: Int): Node {
        val next = state.toMutableList()
        next[y] = x
        return Node(next)
    }

    val value = Chess.h(state)

    val children: Lazy<List<Node>> = lazy {
        (0..7).flatMap {
                y -> (0..7).mapNotNull { x ->
                if (state[y] != x) next(x, y) else null
            }
        }
    }
}