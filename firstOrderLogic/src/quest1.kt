fun build1() = { x: Term -> Predicate(x) }
fun build2() = { x: Term, y: Term -> Predicate(x, y) }
fun build3() = { x: Term, y: Term, z: Term -> Predicate(x, y, z) }

fun main(args: Array<String>) {
    val x = VarTerm("x")
    val y = VarTerm("y")
    val z = VarTerm("z")
    val nono = Term("Nono")
    val m1 = Term("m1")
    val west = Term("West")
    val america = Term("America")

    val american = build1()
    val weapon = build1()
    val hostile = build1()
    val criminal = build1()
    val missile = build1()
    val owns = build2()
    val enemy = build2()
    val sells = build3()

    val f1 = american(x) and weapon(y) and sells(x, y, z) and hostile(z) ergo criminal(x)
    val f2 = owns(nono, m1)
    val f3 = missile(m1)
    val f4 = missile(x) and owns(nono, x) ergo sells(west, x, nono)
    val f5 = missile(x) ergo weapon(x)
    val f6 = enemy(x, america) ergo hostile(x)
    val f7 = american(west)
    val f8 = enemy(nono, america)

    val kb = listOf(f1, f2, f3, f4, f5, f6, f7, f8)
}