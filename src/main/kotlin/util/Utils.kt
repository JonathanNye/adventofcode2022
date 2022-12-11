package util

private val numberMatcher = Regex("""\d+""")

fun String.findInt() = numberMatcher.find(this)?.value?.toInt()
    ?: error("Couldn't find number in $this")

fun String.findLong() = numberMatcher.find(this)?.value?.toLong()
    ?: error("Couldn't find number in $this")

fun String.findInts() = numberMatcher.findAll(this)
    .map { it.value.toInt() }

fun String.findLongs() = numberMatcher.findAll(this)
    .map { it.value.toLong() }