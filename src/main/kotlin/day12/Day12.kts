import util.GridList
import java.io.File
import java.util.*

// Recycled heavily from Day 15 2021 :)
data class Node(val id: Int, val elevation: Int, val cost: Int = 1) : Comparable<Node> {

    companion object {

        fun fromChar(id: Int, char: Char): Node {
            val elevation = when (char) {
                'S' -> 0
                'E' -> 25
                else -> char.code - 'a'.code
            }
            if (elevation < 0 || elevation > 25) error("Unexpected terrain $char")
            return Node(
                id = id,
                elevation = elevation
            ).apply {
                isStart = char == 'S'
                isExit = char == 'E'
            }
        }
    }

    var isStart = false
    var isExit = false
    var runningCost: Int = Int.MAX_VALUE
    var neighbors: Set<Node> = emptySet()
    override fun compareTo(other: Node): Int {
        return runningCost.compareTo(other.runningCost)
    }
}

val lines = File("day_12_input.txt").readLines()
val width = lines.first().length
val height = lines.size
val nodes = lines
    .joinToString(separator = "")
    .mapIndexed { idx, char ->
        Node.fromChar(idx, char)
    }

val grid = GridList(nodes, width, height)

// Build out graph
grid.forEachIndexed { index, node ->
    node.neighbors = grid.neighbors(index, includeDiagonals = false)
        .filter {
            it.elevation <= node.elevation + 1
        }
        .toSet()
}

// Returns cost of fastest path from start to end...
// or, if end is null, populates cost of every reachable Node and returns -1
fun costOfTraversal(
    start: Node,
    end: Node?,
    graph: List<Node>,
    //computeWholeGraph: Boolean,
): Int {
    if (start !in graph || (end != null && end !in graph)) { throw Error("start and end must be in graph") }
    graph.forEach { it.runningCost = Int.MAX_VALUE }
    start.runningCost = 0

    val unvisitedSet = PriorityQueue<Node>()
    unvisitedSet.add(start)
    while (unvisitedSet.isNotEmpty()) {
        val current = unvisitedSet.first()
        unvisitedSet.remove(current)

        if (current == end) {
            // Can only happen if end is provided
            return current.runningCost
        }
        current.neighbors
            .forEach { neighbor ->
                val newRunningCost = current.runningCost + neighbor.cost
                if (newRunningCost < neighbor.runningCost) {
                    neighbor.runningCost = newRunningCost
                    // have to remove and re-add neighbor to make the priority queue work
                    unvisitedSet.remove(neighbor)
                    unvisitedSet.add(neighbor)
                }
            }
    }
    if (end != null) {
        throw Error("Couldn't compute cost of reaching end")
    }
    return -1
}
// Part one
println(
    costOfTraversal(
        start = grid.first { it.isStart },
        end = grid.first { it.isExit},
        graph = nodes
    )
)

// Part 2
// Rebuild the graph so it's reversed
grid.forEachIndexed { index, node ->
    node.neighbors = grid.neighbors(index, includeDiagonals = false)
        .filter {
            it.elevation >= node.elevation - 1
        }
        .toSet()
}
// Compute travel cost for everything from end
costOfTraversal(
    start = grid.first { it.isExit },
    end = null,
    graph = nodes,
)
nodes
    .filter { it.elevation == 0 }
    .minOfOrNull { it.runningCost }
    .let(::println)
