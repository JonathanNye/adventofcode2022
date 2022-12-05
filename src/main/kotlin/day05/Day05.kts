package day05

import java.io.File
import java.util.regex.Pattern

val lines = File("day_05_input.txt").readLines()

val emptyLineIdx = lines.indexOfFirst { it == "" }
val initialStateLines = lines.subList(0, emptyLineIdx)
val moveLines = lines.subList(emptyLineIdx + 1, lines.size)

fun parseInitialState(lines: List<String>): List<ArrayDeque<Char>> {
    val tallestStackSize = lines.size - 1 // Last line is stack "legend"
    val stackIndices = lines.last().mapIndexedNotNull { index, c ->
        if (c == ' ') null else index
    }
    return stackIndices.map { stackIdx ->
        ArrayDeque<Char>().apply {
            ((tallestStackSize - 1) downTo 0).forEach { stackPosition ->
                val crateChar = lines[stackPosition].getOrNull(stackIdx)
                if (crateChar != null && crateChar.isLetter()) {
                    addLast(crateChar)
                }
            }
        }
    }
}

data class Move(
    val count: Int,
    val source: Int,
    val destination: Int,
)

fun parseMoves(lines: List<String>): List<Move> {
    val pattern = Regex("""^move (\d+) from (\d+) to (\d+)$""")
    return lines.map { line ->
        pattern.matchEntire(line)?.let { result ->
            val (count, source, dest) = result.destructured
            Move(count.toInt(), source.toInt() - 1, dest.toInt() - 1)
        } ?: throw IllegalArgumentException("Couldn't match on $line")
    }
}

val stacks = parseInitialState(initialStateLines)
val moves = parseMoves(moveLines)

// Part 1
moves.forEach { (count, source, destination) ->
    repeat(count) {
        val moving = stacks[source].removeLast()
        stacks[destination].addLast(moving)
    }
}

stacks.map { it.last() }.joinToString(separator = "").let(::println)

val stacks2 = parseInitialState(initialStateLines)
// Part 2
moves.forEach { (count, source, destination) ->
    val moving = stacks2[source].takeLast(count)
    repeat(count) { stacks2[source].removeLast() }
    stacks2[destination].addAll(moving)
}

stacks2.map { it.last() }.joinToString(separator = "").let(::println)
