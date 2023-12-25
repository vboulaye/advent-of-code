package aoc2022.day06

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {
    fun clean(input: List<String>): String {
        return input
            .filter { line -> true }
            .map { line -> line }             [0]
    }

    val part1ExpectedResult = 7
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val blockSize = 4
        return parse(input, blockSize)
    }

    private fun parse(input: String, blockSize: Int): Int {
        input.forEachIndexed { index, _ ->
            val win = input.toList().subList(index, index + blockSize)
            if (win.distinct().size == blockSize) {
                return index + blockSize
            }
        }
        return -1
    }

    val part2ExpectedResult = 19
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val blockSize = 14
        return parse(input, blockSize)
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
