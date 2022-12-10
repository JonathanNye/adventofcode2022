import java.io.File

sealed class Instruction {
    object Noop : Instruction()
    data class AddX(val value: Int) : Instruction()
}

class Cpu {
    private var xRegister: Int = 1
    // Keep track of x for every cycle
    private val states = mutableListOf<Int>()
    val crtPixels = mutableListOf<Char>()

    fun perform(instruction: Instruction) {
        when (instruction) {
            Instruction.Noop -> {
                processCycle()
            }
            is Instruction.AddX -> {
                processCycle()
                processCycle()
                xRegister += instruction.value // Value isn't incremented until _after_ the cycles
            }
            else -> Unit // Compiler wants this and thinks I'm not being exhaustive?
        }
    }

    private fun processCycle() {
        val drawCycle = states.size // Drawing is _during_ the cycle so get the size before we add the state
        val drawX = drawCycle % 40
        states.add(xRegister)
        val spritePositions = xRegister - 1 .. xRegister + 1
        if (drawX in spritePositions) {
            crtPixels.add('█')
        } else {
            crtPixels.add(' ')
        }
    }

    fun xAtCycle(cycle: Int) = states[cycle - 1] // 1st cycle is 0th in list
}

val instructions = File("day_10_input.txt")
    .readLines()
    .map { line ->
        if (line == "noop") {
            Instruction.Noop
        } else {
            Instruction.AddX(line.split(" ")[1].toInt())
        }
    }

val checkCycles = intArrayOf(20, 60, 100, 140, 180, 220)

val cpu = Cpu()
instructions.forEach {
    cpu.perform(it)
}
// Part 1
checkCycles.map { cycle ->
    println("Cycle=$cycle, x=${cpu.xAtCycle(cycle)}, str=${cpu.xAtCycle(cycle) * cycle}")
    cpu.xAtCycle(cycle) * cycle
}.sum().let(::println)

// Part 2
cpu.crtPixels
    .windowed(size = 40, step = 40)
    .map { it.joinToString(separator = "") }
    .forEach(::println)
