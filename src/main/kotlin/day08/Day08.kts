import util.GridList
import java.io.File

val lines = File("day_08_input.txt")
    .readLines()
val width = lines.first().length
val height = lines.size

data class Tree(
    val height: Int,
    var visible: Boolean
)

val grid = lines
    .joinToString(separator = "")
    .map {
        Tree (
            height = it.digitToInt(),
            visible = false,
        )
    }
    .let { GridList(it, width, height) }

// Edges are always visible
grid.row(0).forEach { it.visible = true }
grid.row(height - 1).forEach { it.visible = true }
grid.column(0).forEach { it.visible = true }
grid.column(width - 1).forEach { it.visible = true }

(1 until width - 1).forEach { x ->
    (1 until height - 1).forEach { y ->
        val target = grid[x, y]
        val column = grid.column(x)
        val row = grid.row(y)
        val above = column.subList(0, y)
        val below = column.subList(y + 1, height)
        val left = row.subList(0, x)
        val right = row.subList(x + 1, width)
        target.visible = above.all { it.height < target.height } ||
                below.all { it.height < target.height } ||
                left.all { it.height < target.height } ||
                right.all { it.height < target.height }
    }
}

// Part 1
grid.count { it.visible }.let(::println)

fun GridList<Tree>.scenicScore(x: Int, y: Int): Int {
    val target = this[x, y]
    val column = this.column(x)
    val row = this.row(y)
    // reverse above and left because columns and row are normally top-to-bottom and left-to-right
    val above = column.subList(0, y).asReversed()
    val below = column.subList(y + 1, height)
    val left = row.subList(0, x).asReversed()
    val right = row.subList(x + 1, width)

    fun List<Tree>.directionScore(target: Tree): Int {
        val find = indexOfFirst { it.height >= target.height }
        return if (find == -1) {
            size // can see to the edge
        } else {
            find + 1
        }
    }

    return above.directionScore(target) *
            below.directionScore(target) *
            left.directionScore(target) *
            right.directionScore(target)
}

(1 until width - 1).asSequence()
    .flatMap { x ->
        (1 until height - 1).asSequence().map { y -> x to y }
    }
    .map { (x, y) ->
        grid.scenicScore(x, y)
    }
    .max()
    .let(::println)