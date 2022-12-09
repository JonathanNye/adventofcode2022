import java.io.File
import kotlin.math.abs
import kotlin.math.sign

val headMoveDirections = File("day_09_input.txt").readLines()
    .flatMap { line ->
        val direction = when (line[0]) {
            'U' -> Direction.UP
            'D' -> Direction.DOWN
            'L' -> Direction.LEFT
            'R' -> Direction.RIGHT
            else -> throw Exception("Unexpected direction on $line")
        }
        val times = line.split(" ")[1].toInt()
        buildList {
            repeat(times) {
                add(direction)
            }
        }
    }

enum class Direction(
    val dX: Int, val dY: Int
) { UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0) }

fun Pair<Int, Int>.onOrAdjacent(other: Pair<Int, Int>): Boolean {
    val dX = first - other.first
    val dY = second - other.second
    if (abs(dX) > 2 || abs(dY) > 2) {
        println("uh oh, onOrAdjacent too far away")
    }
    return abs(dX) <= 1 && abs(dY) <= 1
}

fun Pair<Int, Int>.dragToward(other: Pair<Int, Int>): Pair<Int, Int> {
    if (other.onOrAdjacent(this)) return this // don't need to move
    if (this.first == other.first) { // directly up or down
        return if (this.second < other.second) { // drag up
            this.first to this.second + 1
        } else {
            this.first to this.second - 1 // drag down
        }
    }
    if (this.second == other.second) { // directly left or right
        return if (this.first < other.first) { // drag right
            this.first + 1 to this.second
        } else {
            this.first - 1 to this.second // drag left
        }
    }

    // Diagonals
    return when {
        other.first < this.first && other.second > this.second -> { // up-left
            this.first - 1 to this.second + 1
        }
        other.first > this.first && other.second > this.second -> { // up-right
            this.first + 1 to this.second + 1
        }
        other.first < this.first && other.second < this.second -> { // down-left
            this.first - 1 to this.second - 1
        }
        else -> { // down-right
            this.first + 1 to this.second - 1
        }
    }
}

// Part 1
fun tailVisitedForRope(ropeLength: Int): Int {
    val tailVisited = mutableSetOf<Pair<Int, Int>>()
    val ropeXs = IntArray(ropeLength) { 0 }
    val ropeYs = IntArray(ropeLength) { 0 }
    headMoveDirections.forEach { direction ->
        ropeXs[0] += direction.dX
        ropeYs[0] += direction.dY
        (1 until ropeLength).forEach { idx ->
            val prevPoint = ropeXs[idx - 1] to ropeYs[idx - 1]
            val currPoint = ropeXs[idx] to ropeYs[idx]
            val newCurr = currPoint.dragToward(prevPoint)
            ropeXs[idx] = newCurr.first
            ropeYs[idx] = newCurr.second
        }
        tailVisited.add(ropeXs.last() to ropeYs.last())
    }
    return tailVisited.size
}

println(tailVisitedForRope(ropeLength = 2))
println(tailVisitedForRope(ropeLength = 10))
