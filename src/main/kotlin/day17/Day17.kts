package day17

import java.io.File

val jetPattern = File("day_17_input.txt").readLines()
    .first()
    .map { char ->
        when (char) {
            '<' -> JetDirection.LEFT
            '>' -> JetDirection.RIGHT
            else -> error("Unexpected jet direction char: $char")
        }
    }

enum class JetDirection { LEFT, RIGHT }

enum class FallingRock(val startingPoints: Set<Pair<Long, Long>>) {
    // ..####
    HLINE(setOf(2L to 0L, 3L to 0L, 4L to 0L, 5L to 0L)),
    // ...#
    // ..###
    // ...#
    PLUS(setOf(3L to 2L, 2L to 1L, 3L to 1L, 4L to 1L, 3L to 0L)),
    // ....#
    // ....#
    // ..###
    ANGLE(setOf(4L to 2L, 4L to 1L, 2L to 0L, 3L to 0L, 4L to 0L)),
    // ..#
    // ..#
    // ..#
    // ..#
    VLINE(setOf(2L to 3L, 2L to 2L, 2L to 1L, 2L to 0L)),
    // ..##
    // ..##
    SQUARE(setOf(2L to 1L, 3L to 1L, 2L to 0L, 3L to 0L)),
}

class CircularIterator<T>(private val backing: Collection<T>): Iterator<T> {
    private var currIdx = 0

    override fun hasNext(): Boolean = backing.isNotEmpty()
    override fun next(): T {
        val next = backing.elementAt(currIdx)
        currIdx = (currIdx + 1) % backing.size
        return next
    }
    
    fun reset() {
        currIdx = 0
    }
}

fun Set<Pair<Long, Long>>.translate(dX: Long, dY: Long) = map { (x, y) ->
    x + dX to y + dY
}.toSet()

fun Set<Pair<Long, Long>>.highestPoint() = if (isEmpty()) { 0L } else { maxOf { it.second } }

val maxX = 6L

fun jet(
    direction: JetDirection,
    fallingRock: Set<Pair<Long, Long>>,
    settledPoints: Set<Pair<Long, Long>>,
): Set<Pair<Long, Long>> {
    // Against left wall
    if (direction == JetDirection.LEFT && fallingRock.any { it.first == 0L }) return fallingRock
    // Against right wall
    if (direction == JetDirection.RIGHT && fallingRock.any { it.first == maxX }) return fallingRock
    // Pushing against settled blocks to the left...
    val settledToLeft = fallingRock.any { (fallingX, fallingY) ->
        settledPoints.contains(fallingX - 1 to fallingY)
    }
    if (direction == JetDirection.LEFT && settledToLeft) return fallingRock
    val settledToRight = fallingRock.any { (fallingX, fallingY) ->
        settledPoints.contains(fallingX + 1 to fallingY)
    }
    if (direction == JetDirection.RIGHT && settledToRight) return fallingRock
    return fallingRock.translate(
        dX = when (direction) {
            JetDirection.LEFT -> -1
            JetDirection.RIGHT -> 1
        },
        dY = 0,
    )
}

fun fallIfAble(
    fallingRock: Set<Pair<Long, Long>>,
    settledPoints: Set<Pair<Long, Long>>,
): Set<Pair<Long, Long>>? {
    // at bottom
    if (fallingRock.any { it.second == 1L }) return null
    val settledBelow = fallingRock.any { (fallingX, fallingY) ->
        settledPoints.contains(fallingX to fallingY - 1)
    }
    if (settledBelow) return null
    return fallingRock.translate(dX = 0L, dY = -1L)
}

val jetIter = CircularIterator(jetPattern)
val rockIter = CircularIterator(FallingRock.values().toList())

val settledPoints = mutableSetOf<Pair<Long, Long>>()
// Part 1

repeat(2022) {
    val shape = rockIter.next()
    var fallingRock = shape.startingPoints
        .translate(dX = 0, dY = settledPoints.highestPoint() + 4)
    while (true) {
        val nextJet = jetIter.next()
        fallingRock = jet(
            direction = nextJet,
            fallingRock = fallingRock,
            settledPoints = settledPoints,
        )
        val falling = fallIfAble(
            fallingRock = fallingRock,
            settledPoints = settledPoints
        )
        if (falling == null) {
            // It couldn't fall
            settledPoints.addAll(fallingRock)
            break
        } else {
            fallingRock = falling
        }
    }
}

println(settledPoints.highestPoint())

