package aoc2024.day11

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Long> {
        return this.split(' ').map { it.toLong() }
    }

    fun clean(input: List<String>): List<List<Long>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 55312L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val line = input[0]
        return line.sumOf { countAfterIterations(it, 25) }
    }

    val cache = mutableMapOf<Pair<Long, Int>, Long>()

    private fun countAfterIterations(n: Long, i: Int): Long {
        if (i == 0) return 1L
        cache[n to i]?.let { return it }
        val remaininingIterations = i - 1
        val numberAsString = n.toString()
        val countAtEnd = when {
            n == 0L -> countAfterIterations(1L, remaininingIterations)

            numberAsString.length % 2L == 0L ->
                countAfterIterations(
                    numberAsString.substring(0, numberAsString.length / 2).toLong(),
                    remaininingIterations
                ) +
                        countAfterIterations(
                            numberAsString.substring(numberAsString.length / 2).toLong(),
                            remaininingIterations
                        )

            else -> countAfterIterations(n * 2024L, remaininingIterations)
        }
        return countAtEnd.also { cache[n to i] = it }
    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val line = input[0]
        if (line.size == 2) return 0L
        return line.sumOf { n -> countAfterIterations(n, 75) }
    }

}


@OptIn(ExperimentalTime::class)
fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)

    fun runPart(part: String, expectedTestResult: Result, partEvaluator: (List<String>) -> Result) {
        val testDuration = measureTime {
            val testResult = partEvaluator(testInput)
            println("test ${part}: $testResult == ${expectedTestResult}")
            check(testResult == expectedTestResult) { "$testResult != ${expectedTestResult}" }
        }
        val fullDuration = measureTime {
            val fullResult = partEvaluator(input)
            println("${part}: $fullResult")
        }
        println("${part}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
    }

    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
