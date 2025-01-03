package aoc2024.day02

import utils.readInput
import utils.removeAt
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {

    fun String.acceptInput() = true

    fun String.parseInput(): List<Int> {
        return this.split(" ").map { it.toInt() }
    }

    fun clean(input: List<String>): List<List<Int>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 2
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.count { isSafeReport(it) }
    }

    private fun isSafeReport(it: List<Int>): Boolean {
        val progressions = it.zipWithNext { a, b -> b - a }
        val allPlus = progressions.all { it in 1..3 }
        val allMoins = progressions.all { it in -3..-1 }
        return allMoins || allPlus
    }

    val part2ExpectedResult = 4
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.count { line -> line.indices.any { i -> isSafeReport(line.removeAt(i)) } }
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
