class Predicate(s: () -> Boolean) {
    var isImplicative = false
    private val state: () -> Boolean = s
    var a: Predicate? = null
    var b: Predicate? = null
    var terms: List<Term> = emptyList()
    var name: String = ""
    var id = ""
    val isFact: Boolean get() = id != "" && terms.none { term -> term is VarTerm }

    constructor(vararg x: Term, s: () -> Boolean) : this(s) {
        this.terms = x.toList()
    }

    constructor(name: String, vararg x: Term) : this({ true }) {
        id = name
        this.name = name + x.joinToString(",", "(", ")")
        this.terms = x.toList()
    }

    operator fun invoke() = state()

    infix fun and(predicate: Predicate) = (Predicate(*terms.union(predicate.terms).toTypedArray()) { state() && predicate.state() }).also {
        it.a = this
        it.b = predicate
        it.name = "$name and ${predicate.name}"
    }

    infix fun or(predicate: Predicate) = Predicate(*terms.union(predicate.terms).toTypedArray()) { state() || predicate.state() }.also {
        it.a = this
        it.b = predicate
        it.name = "$name or ${predicate.name}"
    }

    operator fun not() = Predicate(*terms.toTypedArray()) { !state() }.apply { name = "!$name" }

    infix fun ergo(predicate: Predicate) = (not() or predicate).apply { isImplicative = true }.also {
        it.a = this
        it.b = predicate
        it.name = "$name ergo ${predicate.name}"
    }

    infix fun iff(predicate: Predicate) = ((this ergo predicate) and (predicate ergo this)).also {
        it.a = this
        it.b = predicate
        it.name = "$name iff ${predicate.name}"
    }

    fun extract(inputAtomicPredicates: List<Predicate> = emptyList()): List<Predicate> {
        val atomicPredicates = inputAtomicPredicates.toMutableList()
        if (a == null) {
            atomicPredicates.add(this)
        } else {
            atomicPredicates.addAll(a!!.extract())
            atomicPredicates.addAll(b!!.extract())
        }
        return atomicPredicates
    }

    override fun toString() = name
    fun subst(variables: Map<VarTerm, Term>): Predicate {
        return Predicate(id, *(terms.map { if (it in variables.keys) variables[it]!! else it }.toTypedArray()))
    }

    override fun hashCode() = name.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Predicate
        if (name != other.name) return false
        return true
    }
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
