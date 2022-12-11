import util.findInt
import util.findLong
import util.findLongs
import java.io.File

class Monkey(
    val heldItems: MutableList<Long>,
    val inspectOperation: (Long) -> Long,
    val testDivisor: Long,
    val trueTargetIdx: Int,
    val falseTargetIdx: Int,
    var inspectionCount: Long = 0L,
) {
    companion object {
        private const val WORRY_DECAY_DIVISOR = 3L



        fun parse(lines: List<String>) = Monkey(
            heldItems = lines[1].findLongs().toMutableList(),
            inspectOperation = when {
                lines[2].contains("old * old") -> { n -> n * n }
                lines[2].contains("*") -> { n -> n * lines[2].findLong() }
                lines[2].contains("+") -> { n -> n + lines[2].findLong() }
                else -> error("Unexpected operation: ${lines[2]}")
            },
            testDivisor = lines[3].findLong(),
            trueTargetIdx = lines[4].findInt(),
            falseTargetIdx = lines[5].findInt(),
        )
    }

    fun inspectItems(worryDecay: Boolean, testDivisorProduct: Long) {
        inspectionCount += heldItems.size
        heldItems.replaceAll { item ->
            val inspected = inspectOperation(item)
            val decayed = if (worryDecay) {
                inspected / WORRY_DECAY_DIVISOR
            } else {
                inspected
            }
            // Can keep values from getting too big, thanks James!
            decayed % testDivisorProduct
        }
    }

    fun throwItems(monkeys: List<Monkey>) {
        heldItems.forEach { item ->
            val targetIdx = if (item % testDivisor == 0L) {
                trueTargetIdx
            } else {
                falseTargetIdx
            }
            monkeys[targetIdx].heldItems.add(item)
        }
        heldItems.clear()
    }
}

fun readInput() = File("day_11_input.txt")
    .readLines()
    .chunked(7)
    .map { lines -> Monkey.parse(lines) }

fun List<Monkey>.monkeyBusiness() = sortedByDescending { it.inspectionCount }
    .let { it[0].inspectionCount * it[1].inspectionCount }

fun List<Monkey>.doRound(worryDecay: Boolean, testDivisorProduct: Long) = forEach { monkey ->
    // The puzzle says items are thrown after each individual inspection, but it makes no difference to separate them
    monkey.inspectItems(worryDecay, testDivisorProduct)
    monkey.throwItems(this)
}

// Part 1
var monkeys = readInput()
val testDivisorProduct = monkeys
    .map { it.testDivisor }
    .reduce { acc, divisor -> acc * divisor}

repeat(20) {
    monkeys.doRound(worryDecay = true, testDivisorProduct)
}
println(monkeys.monkeyBusiness())

// Part 2
monkeys = readInput()
repeat(10_000) {
    monkeys.doRound(worryDecay = false, testDivisorProduct)
}
println(monkeys.monkeyBusiness())
