import java.io.File
import java.util.*

val lineRegex = Regex("""^Valve ([A-Z]+) has flow rate=(\d+); tunnels? leads? to valves? (.+)$""")

data class Valve(
    val id: String,
    val rate: Int,
    val adjacentIds: Collection<String>,
) : Comparable<Valve> {
    var runningCost: Int = Int.MAX_VALUE

    override fun compareTo(other: Valve) = runningCost.compareTo(other.runningCost)
}

val valves = File("day_16_input.txt").readLines()
    .map { line ->
        val match = lineRegex.matchEntire(line) ?: error("Unexpected line: $line")
        val (id, rate, adjacent) = match.destructured
        val adjacentIds = adjacent.split(", ")
        Valve(id, rate.toInt(), adjacentIds)
    }
val startValve = valves.find { it.id == "AA" } ?: error("Couldn't find start valve")

val openableValves = valves.filter { it.rate > 0 }

val openableValveRates = valves
    .filter { it.rate > 0}
    .map { it.id to it.rate }
    .toTypedArray()
    .let { hashMapOf(*it) }

val traversalCosts = (openableValves + startValve).flatMap { fromValve ->
    (openableValves + startValve).mapNotNull { toValve ->
        if (fromValve == toValve) null else
            (fromValve.id to toValve.id) to costOfTraversal(fromValve, toValve, valves)
    }
}.toMap()

fun costOfTraversal(
    start: Valve,
    end: Valve,
    graph: List<Valve>,
): Int {
    if (start !in graph || (end !in graph)) { throw Error("start and end must be in graph") }
    graph.forEach { it.runningCost = Int.MAX_VALUE }
    start.runningCost = 0

    val unvisitedSet = PriorityQueue<Valve>()
    unvisitedSet.add(start)
    while (unvisitedSet.isNotEmpty()) {
        val current = unvisitedSet.first()
        unvisitedSet.remove(current)

        if (current == end) {
            // Can only happen if end is provided
            return current.runningCost
        }
        current.adjacentIds
            .map { id -> valves.first { it.id == id } }
            .forEach { neighbor ->
                val newRunningCost = current.runningCost + 1
                if (newRunningCost < neighbor.runningCost) {
                    neighbor.runningCost = newRunningCost
                    // have to remove and re-add neighbor to make the priority queue work
                    unvisitedSet.remove(neighbor)
                    unvisitedSet.add(neighbor)
                }
            }
    }
    error("Couldn't computer cost of ${start.id} to ${end.id}")
}

fun openableValveRoutes(routes: MutableList<List<Pair<String, Int>>>, visited: List<Pair<String, Int>>, currCost: Int, unvisited: List<String>) {
    if (unvisited.isEmpty()) {
        routes.add(visited)
        return
    }
    if (currCost > 30) return
    if (visited.isNotEmpty() && unvisited.all { currCost + 1 + traversalCosts[visited.last().first to it]!! >= 30 }) { // end of the line
        routes.add(visited)
    }
    unvisited.forEach { toVisit ->
        val fromId = if (visited.isEmpty()) { "AA" } else visited.last().first
        val lastEventualFLow = if (visited.isEmpty()) { 0 } else visited.last().second
        val nextRate = openableValveRates[toVisit]!!
        val traversalCost = traversalCosts[fromId to toVisit]!!
        val nextEventualFlow = lastEventualFLow + ((maxTime - currCost - traversalCost - 1) * nextRate)
        val newVisited = visited + (toVisit to nextEventualFlow)
        val newUnvisited = unvisited - toVisit
        openableValveRoutes(routes, newVisited, currCost + 1 + traversalCost, newUnvisited)
    }
}

val openableValveIds = openableValves
    .map { it.id }

val maxTime = 30

val routes = mutableListOf<List<Pair<String, Int>>>()
openableValveRoutes(routes, emptyList(), 0, openableValveIds)

routes
    .maxOf { it.last().second }
    .let(::println)