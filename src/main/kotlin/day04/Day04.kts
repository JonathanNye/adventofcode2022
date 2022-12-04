package day04

import java.io.File

// In retrospect, IntRanges were probably overkill. Since we wrote our own intersect function, we probably could've
// just used Pair<Int,Int>
fun parseIntRangeLine(line: String): Pair<IntRange, IntRange> {
    fun parseIntRange(input: String): IntRange = input.split("-").let {
        it[0].toInt() .. it[1].toInt()
    }
    val rangeStrings = line.split(",")
    return Pair(
        parseIntRange(rangeStrings[0]),
        parseIntRange(rangeStrings[1]),
    )
}

// We could use the Collections built-ins with IntRange, but they allocate Sets with each shared element, so let's be
// more efficient for fun.
fun IntRange.intersect(other: IntRange): IntRange? = if (
    other.contains(this.first) ||
    other.contains(this.last) ||
    this.contains(other.first) ||
    this.contains(other.last)
) {
    this.first.coerceAtLeast(other.first)..this.last.coerceAtMost(other.last)
} else {
    null
}

val rangePairs = File("day_04_input.txt").readLines()
    .map(::parseIntRangeLine)

// Part 1
rangePairs
    .count { (first, second) ->
        val overlap = first.intersect(second)
        overlap == first || overlap == second
    }
    .let(::println)

// Part 2
rangePairs
    .count { (first, second) ->
        first.intersect(second) != null
    }
    .let(::println)