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
        var line = input[0]
        return line.sumOf { n ->
            countIteraitons(n, 25)
        }
//        (1..25).forEach { i ->
//            line = line.flatMap { n ->
//                when {
//                    n == 0L -> listOf(1L)
//                    n.toString().length % 2L == 0L -> listOf(
//                        n.toString().substring(0, n.toString().length / 2).toLong(),
//                        n.toString().substring(n.toString().length / 2).toLong(),
//                    )
//
//                    else -> listOf(n * 2024L)
//                }
//            }
//        }
//        return line.sumOf { 1L }
    }

    val cache = mutableMapOf<Pair<Long, Int>, Long>()

    private fun countIteraitons(n: Long, i: Int): Long {

        if (i == 0) return 1L
        if (cache.containsKey(n to i)) return cache[n to i]!!
        val ret = when {
            n == 0L -> countIteraitons(1L, i - 1)
            n.toString().length % 2L == 0L ->
                countIteraitons(n.toString().substring(0, n.toString().length / 2).toLong(), i - 1) +
                        countIteraitons(n.toString().substring(n.toString().length / 2).toLong(), i - 1)

            else -> countIteraitons(n * 2024L, i - 1)
        }
        cache.put(n to i, ret)
        return ret
    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var line = input[0]
        if (line.size == 2) return 0L
        return line.sumOf { n ->
            countIteraitons(n, 75)
        }
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
