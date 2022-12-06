import java.io.File

val input = File("day_06_input.txt").readText()

// names are hard
fun String.endIndexOfSubstringWithNoRepeatedCharsOfLength(length: Int): Int =
    asSequence()
        .windowed(size = length, step = 1, partialWindows = false)
        .indexOfFirst { window ->
            window.toSet().size == length // all unique
        }
        .let { if (it == -1) throw Exception("Couldn't find unique substring") else it + length }

println(input.endIndexOfSubstringWithNoRepeatedCharsOfLength(4))
println(input.endIndexOfSubstringWithNoRepeatedCharsOfLength(14))