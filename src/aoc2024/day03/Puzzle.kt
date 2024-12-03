package aoc2024.day03

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): Int {
        val matchResult = Regex("mul\\((\\d+),(\\d+)\\)").findAll(this).asSequence()
        return matchResult.map { it.destructured }.map { (a, b) -> a.toInt() * b.toInt() }.sum()
    }

    fun clean(input: List<String>): List<Int> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 161
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.sum()
    }

    val part2ExpectedResult = 48
    fun part2(rawInput: List<String>): Result {
        val a = if (rawInput.size == 1) {
            "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"
        } else {
            rawInput.joinToString("")
        }

        return Regex("do\\(\\)(.*?)don't\\(\\)")
            .findAll("do()" + a + "don't()").asSequence()
            .map { it.groupValues[1] }
            .sumOf { part1(listOf(it)) }
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
