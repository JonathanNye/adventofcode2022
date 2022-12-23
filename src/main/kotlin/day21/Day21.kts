import java.io.File


sealed interface Value {
    data class Number(val number: Long): Value, Expression {
        override fun evaluate(): Long = number

    }
    data class Reference(val id: String): Value
}

sealed interface Expression {
    fun evaluate(): Long
}

data class Math(
    val a: Value,
    val b: Value,
    val op: Operation,
) : Expression {
    override fun evaluate(): Long {
        val aNum = a as? Value.Number
        val bNum = b as? Value.Number
        if (aNum == null || bNum == null) {
            error("Can't evaluate yet: $this")
        }
        return op.evaluate(aNum.number, bNum.number)
    }

    fun evaluatable(): Boolean = a is Value.Number && b is Value.Number

    fun hasReference(id: String) = (a is Value.Reference && a.id == id) ||
            (b is Value.Reference && b.id == id)

    fun replacingReference(id: String, number: Value.Number) = copy(
        a = if (a == Value.Reference(id)) {
            number
        } else {
            a
        },
        b = if (b == Value.Reference(id)) {
            number
        } else {
            b
        }
    )
}

sealed interface Operation {
    abstract fun evaluate(a: Long, b: Long): Long
    object Add : Operation {
        override fun evaluate(a: Long, b: Long): Long = a + b
    }
    object Subtract : Operation {
        override fun evaluate(a: Long, b: Long): Long = a - b
    }
    object Multiply : Operation {
        override fun evaluate(a: Long, b: Long): Long = a * b
    }
    object Divide : Operation {
        override fun evaluate(a: Long, b: Long): Long = a / b
    }
}

data class Monkey(
    val id: String,
    var expr: Expression,
)

val lines = File("day_21_input.txt").readLines()
val unresolved: MutableList<String> = mutableListOf()
val monkeys: MutableList<Monkey> = mutableListOf()

lines.forEach { line ->
    val halves = line.split(": ")
    val id = halves[0]
    unresolved.add(id)
    val asNumber = halves[1].toLongOrNull()
    val expr = if (asNumber != null) {
         Value.Number(asNumber)
    } else {
        val exprTokens = halves[1].split(" ")
        val ref1 = Value.Reference(exprTokens[0])
        val ref2 = Value.Reference(exprTokens[2])
        val op = when(exprTokens[1][0]) {
            '+' -> Operation.Add
            '-' -> Operation.Subtract
            '*' -> Operation.Multiply
            '/' -> Operation.Divide
            else -> error("Bad operation for $line")
        }
        Math(ref1, ref2, op)
    }
    monkeys.add(Monkey(id, expr))
}

unresolved.remove("root")

while (unresolved.isNotEmpty()) {
    val toResolve = monkeys.first { it.expr is Value.Number }
    val resolvingNumber = toResolve.expr as Value.Number
    val resolvingId = toResolve.id
    monkeys.forEach { monkey ->
        val expr = monkey.expr
        if (expr is Math &&
            expr.hasReference(resolvingId)) {
            val newExpr = expr.replacingReference(resolvingId, resolvingNumber)
            monkey.expr = newExpr
        }
    }
    monkeys.forEach { monkey ->
        val expr = monkey.expr
        if (expr is Math && expr.evaluatable()) {
            val solvedExpr = Value.Number(expr.evaluate())
            monkey.expr = solvedExpr
        }
    }
    unresolved.remove(resolvingId)
    monkeys.remove(toResolve)
}

monkeys.first { it.id == "root" }.expr.let(::println)
