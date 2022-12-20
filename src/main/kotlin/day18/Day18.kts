import java.io.File

val cubesPoints = File("day_18_input.txt")
    .readLines()
    .map { line ->
        val split = line.split(',')
        Triple(split[0].toInt(), split[1].toInt(), split[2].toInt())
    }
    .toHashSet()

val sideDeltas = setOf(
    Triple(-1, 0, 0),
    Triple(1, 0, 0),
    Triple(0, 1, 0),
    Triple(0, -1, 0),
    Triple(0, 0, -1),
    Triple(0, 0, 1),
)

operator fun Triple<Int, Int, Int>.plus(other: Triple<Int, Int, Int>): Triple<Int, Int, Int> =
    Triple(first + other.first, second + other.second, third + other.third)

fun Triple<Int, Int, Int>.neighbors(): Set<Triple<Int, Int, Int>> = sideDeltas
    .map { sideDelta ->
        this + sideDelta
    }.toSet()

fun Triple<Int, Int, Int>.openSides(
    others: Set<Triple<Int, Int, Int>>,
    openAir: Set<Triple<Int, Int, Int>>? = null,
): Int = this.neighbors()
    .count { neighbor ->
        if (openAir == null) {
            !others.contains(neighbor)
        } else {
            !others.contains(neighbor) && neighbor in openAir
        }
    }

// Part 1
cubesPoints
    .sumOf { cube ->
        cube.openSides(cubesPoints)
    }
    .let(::println)

// Part 2
// Tweaked openSides to optionally take set of known open air, check membership in that as well
// Figure out set of known open air
// Extent of our cubes, bumped out by one in every direction
val xRange = cubesPoints.map { it.first }.let { it.min() - 1 ..    it.max() + 1 }
val yRange = cubesPoints.map { it.second }.let { it.min() - 1 .. it.max() + 1 }
val zRange = cubesPoints.map { it.third }.let { it.min() - 1 .. it.max() + 1 }

val openAir = mutableSetOf<Triple<Int, Int, Int>>()
val unvisitedSet = mutableSetOf<Triple<Int, Int, Int>>()
// Know we can start here because we bumped out the extent, has to be open
unvisitedSet.add(Triple(xRange.first, yRange.first, zRange.first))

while (unvisitedSet.isNotEmpty()) {
    val curr = unvisitedSet.elementAt(0)
    unvisitedSet.remove(curr)
    openAir.add(curr)

    curr.neighbors()
        .filter { neighbor ->
            neighbor.first in xRange &&
                    neighbor.second in yRange &&
                    neighbor.third in zRange &&
                    neighbor !in cubesPoints &&
                    neighbor !in openAir
        }.forEach {
            unvisitedSet.add(it)
        }
}

cubesPoints
    .sumOf { cube ->
        cube.openSides(cubesPoints, openAir)
    }
    .let(::println)