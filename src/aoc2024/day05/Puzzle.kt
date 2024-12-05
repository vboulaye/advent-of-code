package aoc2024.day05

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    data class Pages(val pageOrders: Map<Int, List<Int>>, val pageUpdates: List<List<Int>>) {
        val sortedPageUpdates = pageUpdates.map {
            it.sortedWith({ o1, o2 ->
                when {
                    pageOrders[o1]?.contains(o2) == true -> -1
                    else -> 1
                }
            })
        }
    }

    fun clean(input: List<String>): Pages {
        val pageOrders = input
            .filter { line -> "|" in line }
            .map { line -> line.split("|").map { it.toInt() } }
            .groupBy({ it[0] }, { it[1] })

        val pageUpdates = input
            .filter { line -> "," in line }
            .map { line -> line.split(",").map { it.toInt() } }

        return Pages(pageOrders, pageUpdates)
    }

    val part1ExpectedResult = 143
    fun part1(rawInput: List<String>): Result {

        val input = clean(rawInput)

        val okUpdates = input.pageUpdates
            .filterIndexed { i, pageUpdateLine ->
                pageUpdateLine == input.sortedPageUpdates[i]
            }
        return okUpdates.sumOf { getMiddle(it) }
    }

    private fun getMiddle(it: List<Int>) = it[it.size / 2]

    val part2ExpectedResult = 123
    fun part2(rawInput: List<String>): Result {

        val input = clean(rawInput)

        return input.sortedPageUpdates
            .filterIndexed { i, pageUpdateLine ->
                pageUpdateLine != input.pageUpdates[i]
            }
            .sumOf { getMiddle(it)  }
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
