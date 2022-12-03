package day01

import java.io.File

val caloriesOrNull = File("day_01_input.txt")
    .readLines()
    .map { it.toIntOrNull() }

val calorieTotals = mutableListOf<Int>()
var currentTotal = 0

for(calorieItem in caloriesOrNull) {
    when (calorieItem) {
        null -> {
            calorieTotals.add(currentTotal)
            currentTotal = 0
        }
        else -> {
            currentTotal += calorieItem
        }
    }
}

// Part 1
println(calorieTotals.max())

// Part 2
calorieTotals.sortedDescending().take(3).sum().let(::println)
