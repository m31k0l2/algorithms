fun build1(name: String) = { x: Term -> Predicate(name, x) }
fun build2(name: String) = { x: Term, y: Term -> Predicate(name, x, y) }
fun build3(name: String) = { x: Term, y: Term, z: Term -> Predicate(name, x, y, z) }

fun main(args: Array<String>) {
    val x = VarTerm("x")
    val y = VarTerm("y")
    val z = VarTerm("z")
    val nono = Term("Nono")
    val m1 = Term("m1")
    val west = Term("West")
    val america = Term("America")

    val american = build1("american")
    val weapon = build1("weapon")
    val hostile = build1("hostile")
    val criminal = build1("criminal")
    val missile = build1("missile")
    val owns = build2("owns")
    val enemy = build2("enemy")
    val sells = build3("sells")

    val f1 = american(x) and weapon(y) and sells(x, y, z) and hostile(z) ergo criminal(x) // 9.3
    val f2 = owns(nono, m1) // 9.4
    val f3 = missile(m1) // 9.5
    val f4 = missile(x) and owns(nono, x) ergo sells(west, x, nono) // 9.6
    val f5 = missile(x) ergo weapon(x) // 9.7
    val f6 = enemy(x, america) ergo hostile(x) // 9.8
    val f7 = american(west) // 9.9
    val f8 = enemy(nono, america) // 9.10

    val kb = listOf(f1, f2, f3, f4, f5, f6, f7, f8)
    println(f1.a)
    println(f1.b)

    println(f1.a!!.a)
    println(f1.a!!.b)
}