import java.io.File

val lines = File("day_07_input.txt").readLines().drop(1)

sealed class Command {
    companion object {
        fun parse(input: String): Command = when {
            input == "$ ls" -> ListContents
            input == "$ cd .." -> ChangeDirectoryUp
            input.startsWith("$ cd ") -> {
                ChangeDirectoryDown(input.substring(startIndex = 5))
            }
            else -> throw Exception("Expected command, got: $input")
        }
    }

    data class ChangeDirectoryDown(val target: String): Command()
    object ChangeDirectoryUp : Command()
    object ListContents : Command()
}

sealed interface Filelike {

    companion object {
        private val dirPattern = Regex("""^dir (\S+)$""")
        private val filePattern = Regex("""^(\d+) (\S+)""")
        fun parse(input: String): Filelike? {
            val dirMatch = dirPattern.matchEntire(input)
            dirMatch?.destructured?.let { (dirName) ->
                return Directory(name = dirName)
            }
            val fileMatch = filePattern.matchEntire(input)
            fileMatch?.destructured?.let { (size, name) ->
                return File(name, size.toInt())
            }
            return null
        }
    }
    val name: String
    val size: Int

    fun find(predicate: (Filelike) -> Boolean): List<Filelike>

    data class File(override val name: String, override val size: Int) : Filelike {

        override fun find(predicate: (Filelike) -> Boolean): List<Filelike> =
            if (predicate(this)) listOf(this) else emptyList()

    }

    data class Directory(override val name: String) : Filelike {
        var parent: Directory? = null
        val contents = mutableSetOf<Filelike>()

        override val size: Int
            get() = contents.sumOf { it.size }

        override fun find(predicate: (Filelike) -> Boolean): List<Filelike> {
            val foundContents = contents.flatMap {
                it.find(predicate)
            }
            return if (predicate(this)) {
                foundContents + this
            } else {
                foundContents
            }
        }
    }
}

val root = Filelike.Directory(name = "/")
var currentDir = root
var cmdIdx = 0

while (cmdIdx < lines.size - 1) {
    val command = Command.parse(lines[cmdIdx])
    when (command) {
        is Command.ListContents -> {
            while (true) {
                cmdIdx += 1
                if (cmdIdx >= lines.size) {
                    break
                }
                val fileLike = Filelike.parse(lines[cmdIdx])
                if (fileLike == null) {
                    // it's our next command
                    break
                } else {
                    if (fileLike is Filelike.Directory) { // blegh
                        fileLike.parent = currentDir
                    }
                    currentDir.contents.add(fileLike)
                }
            }
        }
        is Command.ChangeDirectoryUp -> {
            currentDir = currentDir.parent ?: throw Exception("Can't cd .. from ${currentDir.name}")
            cmdIdx += 1
        }
        is Command.ChangeDirectoryDown -> {
            currentDir = currentDir.contents.firstOrNull {
                it is Filelike.Directory && it.name == command.target
            } as? Filelike.Directory ?: throw Exception("No subdirectory ${command.target} in ${currentDir.name}")
            cmdIdx += 1
        }
        else -> { /* hmm */ }
    }
}

root.find { it is Filelike.Directory && it.size <= 100000 }
    .sumOf { it.size }
    .let(::println)

val totalSpace = 70000000
val neededSpace = 30000000
val targetUtilization = totalSpace - neededSpace
val currentUtilization = root.size
val minimumDeletionSize = currentUtilization - targetUtilization

root.find { it is Filelike.Directory && it.size >= minimumDeletionSize }
    .map { it.size }
    .sorted()
    .first()
    .let(::println)
