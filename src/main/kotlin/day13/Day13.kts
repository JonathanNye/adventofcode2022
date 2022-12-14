import Day13.PacketData.PacketList
import Day13.PacketData.PacketValue
import java.io.File

val packets = File("day_13_input.txt").readLines()
    .filter { it.isNotEmpty() }
    .map { PacketData.parse(it) }

sealed class PacketData: Comparable<PacketData> {
    companion object {
        fun parse(line: String): PacketData = parse(ArrayDeque(line.map { it }))
        private fun parse(stack: ArrayDeque<Char>): PacketData {
            return when(stack.first()) {
                '[' -> {
                    PacketList.parse(stack)
                }
                else -> PacketValue.parse(stack)
            }
        }
    }

    data class PacketValue(val value: Int) : PacketData() {
        companion object {
            fun parse(stack: ArrayDeque<Char>): PacketValue {
                val numberChars = mutableListOf<Char>()
                while (stack.first().isDigit()) {
                    numberChars.add(stack.removeFirst())
                }
                return PacketValue(numberChars.joinToString(separator = "").toInt())
            }
        }

        override fun compareTo(other: PacketData): Int = when (other) {
            is PacketValue -> value.compareTo(other.value)
            is PacketList -> PacketList(this).compareTo(other)
        }

        override fun toString(): String = value.toString()
    }
    data class PacketList(val items: List<PacketData>) : PacketData() {
        constructor(vararg items: PacketData) : this(items.toList())
        companion object {
            fun parse(stack: ArrayDeque<Char>): PacketList {
                stack.removeFirst() // drop [
                // parse list contents
                val items = mutableListOf<PacketData>()
                while (stack.first() != ']') {
                    items.add(PacketData.parse(stack))
                    if (stack.first() == ',') {
                        stack.removeFirst() // drop ,
                    }
                }
                stack.removeFirst() // drop ]
                return PacketList(items)
            }
        }

        override fun compareTo(other: PacketData): Int {
            return when (other) {
                is PacketValue -> return this.compareTo(PacketList(other))
                is PacketList -> {
                    this.items.zip(other.items).forEach { (left, right) ->
                        val comp = left.compareTo(right)
                        if (comp != 0) {
                            return comp
                        }
                    }
                    return this.items.size - other.items.size
                }
            }
        }

        override fun toString(): String = "[${items.joinToString(separator = ",")}]"
    }
}

// Part 1
packets
    .windowed(size = 2, step = 2) {
        it[0] to it[1]
    }
    .mapIndexed { idx, (first, second) ->
        if (first < second) {
            idx + 1
        } else {
            0
        }
    }
    .sum()
    .let(::println)

// Part 2
val dividerPacketOne = PacketList(PacketList(PacketValue(2))) // [[2]]
val dividerPacketTwo = PacketList(PacketList(PacketValue(6))) // [[6]]

packets
    .plus(listOf(dividerPacketOne, dividerPacketTwo))
    .sorted()
    .let {
        println((it.indexOf(dividerPacketOne) + 1) * (it.indexOf(dividerPacketTwo) + 1))
    }
