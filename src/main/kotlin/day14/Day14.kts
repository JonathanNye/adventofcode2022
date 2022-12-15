import java.io.File
import kotlin.math.max
import kotlin.math.min

val structures = File("day_14_input.txt")
    .readLines()
    .map { line ->
        line.split(" -> ")
            .map { pointString ->
                pointString.split(",")
                    .map { it.toInt() }
                    .let { it[0] to it[1] }
            }
    }

val rockCells = structures
    .flatMap { structurePoints ->
        structurePoints
            .zipWithNext { (startX, startY), (endX, endY) ->
                if (startX == endX) {
                    (min(startY, endY) .. max(startY, endY)).map {
                        startX to it
                    }
                } else if (startY == endY) {
                    (min(startX, endX) .. max(startX, endX)).map {
                        it to startY
                    }
                } else error("Non-cardinal direction point pair $startX,$startY -> $endX,$endY")
            }
            .flatten()
    }
    .toHashSet()

val maxRockY = rockCells
    .map { it.second }
    .max()

// Settled sand has its own collection so its easy to count, but is duplicated to settled rocks + sand so it's easy to
// look up
val settledSand = HashSet<Pair<Int, Int>>()
val settledCells = HashSet<Pair<Int, Int>>()
settledCells.addAll(rockCells)

fun Pair<Int, Int>.nextPositionIfAble(settledCells: Set<Pair<Int, Int>>, floorY: Int?): Pair<Int, Int>? = when {
    floorY != null && this.second == floorY - 1 -> null // floor is below, definitely can't move
    !settledCells.contains(this.first to this.second + 1) -> // straight down
        this.first to this.second + 1
    !settledCells.contains(this.first - 1 to this.second + 1) -> // down left
        this.first - 1 to this.second + 1
    !settledCells.contains(this.first + 1 to this.second + 1) -> // down right
        this.first + 1 to this.second + 1
    else -> null // can't move
}

var fallingSand = 500 to 0
// Part 1
while (true) {
    val next = fallingSand.nextPositionIfAble(settledCells, floorY = null)
    if (next == null) { // can't move, settle and reset
        settledCells.add(fallingSand)
        settledSand.add(fallingSand)
        fallingSand = 500 to 0
    } else { // can move, keep going...
        fallingSand = next
        if (fallingSand.second > maxRockY) { // unless we'd just fall down into the abyss
            break
        }
    }
}

println(settledSand.size)

// Part 2
settledSand.clear()
settledCells.clear()
settledCells.addAll(rockCells)
while (true) {
    if (settledCells.contains(500 to 0)) {
        break
    }
    val next = fallingSand.nextPositionIfAble(settledCells, floorY = maxRockY + 2)
    if (next == null) {
        settledCells.add(fallingSand)
        settledSand.add(fallingSand)
        fallingSand = 500 to 0
    } else {
        fallingSand = next
    }
}

println(settledSand.size)
