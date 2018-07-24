class Predicate(s: () -> Boolean) {
    private val state: () -> Boolean = s
    private var terms: List<Term>? = null
    constructor(vararg x: Term, s: () -> Boolean) : this(s) {
        this.terms = x.toList()
    }
    constructor(vararg x: Term) : this(*x, s = { true })
    operator fun invoke() = state()
    infix fun and(predicate: Predicate) = Predicate { state() && predicate.state() }
    infix fun or(predicate: Predicate) = Predicate { state() || predicate.state() }
    operator fun not() = Predicate { !state() }
    infix fun ergo(other: Predicate) = not() or other
    infix fun iff(other: Predicate) = (this ergo other) and (other ergo this)
}

open class Term(private val name: String) {
    override fun toString() = name
}

val knows = { t1: Term, t2: Term -> listOf(t1, t2) }
val mother = { x: Term -> CompoundTerm(x, "Mother")}

class VarTerm(name: String): Term(name)
class CompoundTerm(var term: Term, val name: String): Term("") {
    override fun toString() = "$name($term)"
}

fun main(args: Array<String>) {
    val x = VarTerm("x")
    val y = VarTerm("y")
    val John = Term("John")
    val Jane = Term("Jane")
    val Bill = Term("Bill")
    println(mother(mother(Bill)))
    val u1 = unify(knows(John, x), knows(John, Jane))
    println(u1)
    val u2 = unify(knows(John, x), knows(y, Bill))
    println(u2)
    val u3 = unify(knows(John, x), knows(y, mother(mother(y))))
    println(u3)
    val u4 = unify(knows(John, x), knows(x, Bill))
    println(u4)
}

fun unify(t1: List<Term>, t2: List<Term>): Map<VarTerm, Term>? {
    if (t1.size != t2.size) return null
    val variables = mutableMapOf<VarTerm, Term>()
    for (i in 0 until t1.size) {
        if (t1[i] != t2[i] && (t1[i] !is VarTerm && t2[i] !is VarTerm)) return null
        if (t1[i] is VarTerm && t2[i] !is VarTerm) {
            val x = t1[i] as VarTerm
            if (variables.containsKey(x)) return null
            variables[x] = t2[i]
        }
        if (t2[i] is VarTerm && t1[i] !is VarTerm) {
            val x = t2[i] as VarTerm
            if (variables.containsKey(x)) return null
            variables[x] = t1[i]
        }
    }
    variables.forEach { _, term ->
        if (term is CompoundTerm) unifyVar(variables, term)
    }
    return variables
}

fun unifyVar(variables: Map<VarTerm, Term>, term: CompoundTerm) {
    val x = term.term
    if (x is VarTerm) {
        if (variables.containsKey(x)) {
            term.term = variables[x]!!
        }
    } else if (x is CompoundTerm) {
        unifyVar(variables, x)
    }
}
