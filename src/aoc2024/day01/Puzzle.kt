package aoc2024.day01

import utils.readInput
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Int> {
        return this.split(" +".toRegex()).map { it.toInt() }
    }

    fun clean(input: List<String>): List<List<Int>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 11
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val (left, right) = input.map { it[0] to it[1] }.unzip()
        return left.sorted().zip(right.sorted()) { l, r -> abs(l - r) }.sum()
    }

    val part2ExpectedResult = 31
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val (left, right) = input.map { it[0] to it[1] }.unzip()
        val rightIndex = right.groupingBy { it }.eachCount()
        return left.sumOf { it * (rightIndex[it] ?: 0) }
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
