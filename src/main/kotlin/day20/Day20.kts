import java.io.File

val input = File("day_20_input.txt")
    .readLines()
    .mapIndexed { index, line ->
        index to line.toLong()
    }

fun mix(input: List<Pair<Int, Long>>, toMix: MutableList<Pair<Int, Long>>): List<Pair<Int, Long>> {
    input.forEach { itemToMix ->
        val newIndex = (toMix.indexOf(itemToMix) + itemToMix.second).mod(input.size - 1)
        toMix.remove(itemToMix)
        toMix.add(newIndex, itemToMix)
    }
    return toMix
}

fun List<Pair<Int, Long>>.coordinate(): Long {
    val idxOfZero = indexOfFirst { it.second == 0L }
    return listOf(1000, 2000, 3000).sumOf { offset ->
        this[(idxOfZero + offset) % size].second
    }
}

// Part 1
val mixed = input.toMutableList()
mix(input, mixed)
println(mixed.coordinate())


// Part 2
val input2 = input.map { it.first to it.second * 811589153 }
val mixed2 = input2.toMutableList()
repeat(10) {
    mix(input2, mixed2)
}
println(mixed2.coordinate())
