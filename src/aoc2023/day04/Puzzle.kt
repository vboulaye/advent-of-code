package aoc2023.day04

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


data class Card(val idx: Int, val winning: List<Int>, val tirages: List<Int>) {
    fun winningCountPart1(): Int {
        return winning.intersect(tirages)
            .foldIndexed(0) { idx, acc, i ->
                if (idx == 0) {
                    acc + 1
                } else {
                    acc * 2
                }
            }
    }

    fun winingCount() = winning.intersect(tirages).size

}

class Puzzle {
    fun clean(input: List<String>): List<Card> {
        return input
            .filter { line -> true }
            .map { line ->
                val idx = line.substringBefore(":").substringAfter(" ").trim().toInt()
                val numbers = line.substringAfter(":").trim()
                val winning = numbers.substringBefore("|").trim().split(Regex(" +")).map { it.toInt() }
                val tirages = numbers.substringAfter("|").trim().split(Regex(" +")).map { it.toInt() }
                Card(idx, winning, tirages)
            }

    }

    val part1ExpectedResult = 13
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.map { it.winningCountPart1() }.sum()
    }

    val part2ExpectedResult = 30

    data class CardStack(var count: Int, val winningCount: Int)

    fun part2(rawInput: List<String>): Result {
        var input = (clean(rawInput))
            .map { card ->
                CardStack(1, card.winingCount())
            }
            .toMutableList()

        input.forEachIndexed() { idx, cardStack ->
            (1..cardStack.winningCount).forEach { i ->
                val c = input[idx + i]
                c.count += cardStack.count
            }
        }

        return input.map { it.count }.sum()
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
