import java.io.File
import kotlin.math.abs

val linePattern = Regex("""^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$""")

val sensorsAndBeacons = File("day_15_input.txt")
    .readLines()
    .map { line ->
        val (sensorX, sensorY, closestX, closestY) = linePattern.matchEntire(line)?.destructured
            ?: error("Couldn't parse $line")
        (sensorX.toInt() to sensorY.toInt()) to (closestX.toInt() to closestY.toInt())
    }

fun Pair<Int, Int>.manhattanDist(other: Pair<Int, Int>): Int =
    abs(this.first - other.first) + abs(this.second - other.second)

val allSensorsAndBeacons = sensorsAndBeacons
    .flatMap { listOf(it.first, it.second) }
    .toSet()

// Part 1
fun part1() {
//    val checkY = 10
    val checkY = 2_000_000 // 10 for sample

    val relevantSensorsAndBeacons = sensorsAndBeacons
        .filter { (sensor, closestBeacon) ->
            val dist = sensor.manhattanDist(closestBeacon)
            abs(sensor.second - checkY) <= dist
        }

    val relevantSensorXExtents = relevantSensorsAndBeacons
        .map { (sensor, closestBeacon) ->
            val dist = sensor.manhattanDist(closestBeacon)
            sensor.first - dist to sensor.first + dist
        }

    val minCheckX = relevantSensorXExtents.minBy { it.first }.first
    val maxCheckX = relevantSensorXExtents.maxBy { it.second }.second

    (minCheckX..maxCheckX).count { x ->
        val checkPoint = x to checkY
        (checkPoint !in allSensorsAndBeacons) &&
                relevantSensorsAndBeacons.any { (sensor, closestBeacon) ->
                    sensor.manhattanDist(checkPoint) <= sensor.manhattanDist(closestBeacon)
                }

    }.let(::println)
}

fun part2() {
    // maybe: row-by-row, check each X... find nearest beacon, skip just out of its exclusion zone
//    val maxCheck = 20
    val maxCheck = 4_000_000 // 20 for sample
    for (y in (0 .. maxCheck)) {
        var x = 0
        while (x < maxCheck) {
            // find nearest sensor + beacon
//            val (nearestSensor, nearestBeacon) = sensorsAndBeacons.minBy { (sensor, beacon) ->
//                (x to y).manhattanDist(sensor)
//            }
            val furthestCoveringSensor = sensorsAndBeacons
                //.sortedByDescending { (sensor, beacon) -> (x to y).manhattanDist(sensor) }
                .firstOrNull {(sensor, beacon) ->
                    val hereDist = (x to y).manhattanDist(sensor)
                    val sensorBeaconDist = sensor.manhattanDist(beacon)
                    hereDist <= sensorBeaconDist
                }
            if (furthestCoveringSensor == null) {
                // no sensor covered us
                println("$x, $y")
                println(x * 4_000_000 + y) // not 1700577764
                x += 1
                break
            }
            val (coveringSensor, nearestBeacon) = furthestCoveringSensor
            val sensorBeaconDist = coveringSensor.manhattanDist(nearestBeacon)
            // jump to the other side, x-wise
            val sensorDy = abs(coveringSensor.second - y)
            x += (sensorBeaconDist - sensorDy) - (x - coveringSensor.first) + 1
            if (x > maxCheck) break // went too far, continue to next line
        }
    }
}


part1()
part2()
