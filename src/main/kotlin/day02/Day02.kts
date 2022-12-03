import java.io.File

enum class RoundResult(val pointValue: Int) {
    WIN(6), LOSS(0), DRAW(3);

    companion object {
        fun fromInput(input: Char): RoundResult = when(input) {
            'X' -> LOSS
            'Y' -> DRAW
            'Z' -> WIN
            else -> throw IllegalArgumentException("Unexpected result input: $input")
        }
    }
}
enum class RpsThrow(val pointValue: Int) {
    ROCK(1), PAPER(2), SCISSORS(3);

    companion object {
        fun fromInput(input: Char): RpsThrow = when(input) {
            'A' -> ROCK
            'B' -> PAPER
            'C' -> SCISSORS
            'X' -> ROCK
            'Y' -> PAPER
            'Z' -> SCISSORS
            else -> throw IllegalArgumentException("Unexpected throw input: $input")
        }
    }

    fun resultVersus(other: RpsThrow): RoundResult = when {
        this == other -> RoundResult.DRAW
        other == this.winsAgainst() -> RoundResult.WIN
        else -> RoundResult.LOSS
    }

    fun losesTo(): RpsThrow = when (this) {
        ROCK -> PAPER
        PAPER -> SCISSORS
        SCISSORS -> ROCK
    }

    fun winsAgainst(): RpsThrow = when (this) {
        ROCK -> SCISSORS
        PAPER -> ROCK
        SCISSORS -> PAPER
    }
}

class RpsRound(
    val opponentThrow: RpsThrow,
    val myThrow: RpsThrow,
) {
    constructor(opponentThrow: RpsThrow, roundResult: RoundResult): this(
        opponentThrow = opponentThrow,
        myThrow = when (roundResult) {
            RoundResult.WIN -> opponentThrow.losesTo()
            RoundResult.LOSS -> opponentThrow.winsAgainst()
            RoundResult.DRAW -> opponentThrow
        }
    )

    val pointValue = myThrow.pointValue + myThrow.resultVersus(opponentThrow).pointValue
}

// Part 1, tokens indicate opponent + self throws
val inputChars = File("day_02_input.txt")
    .readLines()
    .map { it[0] to it[2] }

val rounds = inputChars
    .map {
        RpsRound(
            opponentThrow = RpsThrow.fromInput(it.first),
            myThrow = RpsThrow.fromInput(it.second),
        )
    }

rounds.sumOf { it.pointValue }.let(::println)

// Part 2, tokens indicate opponent + desired result
val rounds2 = inputChars
    .map {
        RpsRound(
            opponentThrow = RpsThrow.fromInput(it.first),
            roundResult = RoundResult.fromInput(it.second),
        )
    }

rounds2.sumOf { it.pointValue }.let(::println)