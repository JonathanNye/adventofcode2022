import java.io.File

// A-Z 65-90, a-z 97-122
fun Char.priority(): Int = if (isUpperCase()) {
    code - 38
} else {
    code - 96
}

val lines = File("day_03_input.txt")
    .readLines()

// Part 1

val errorPriorities = lines
    .map {
        val firstCompartment = it.toCharArray(startIndex = 0, endIndex = it.length / 2).toSet()
        val secondCompartment = it.toCharArray(startIndex = it.length / 2, endIndex = it.length).toSet()
        firstCompartment to secondCompartment
    }
    .map { (first, second) ->
        first.intersect(second)
    }
    .flatten()
    .map { it.priority() }

println(errorPriorities.sum())

// Part 2
val badges = lines
    .map { it.toSet() }
    .windowed(size = 3, step = 3, partialWindows = false)
    .map { window ->
        val common = window[0].intersect(window[1]).intersect(window[2])
        common.elementAt(0)
    }

println(badges.sumOf { it.priority() })