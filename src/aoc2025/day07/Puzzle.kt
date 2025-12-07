package aoc2025.day07

import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Long
typealias Result2 = Long


class Puzzle {


    fun parseInput(rawInput: List<String>): List<List<Char>> {
        return rawInput
            .filter { line -> true }
            .map { line -> line.toList() }
    }

    val part1ExpectedResult: Result1 = 21L
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)
        val splitters = getSplitters(input)
        return splitters.size.toLong()
    }

    private fun getSplitters(input: List<List<Char>>): MutableSet<Point> {
        val splitters = mutableSetOf<Point>()
        var rays = listOf(input[0].indexOfFirst { it == 'S' })
        for ((index, row) in input.drop(1).withIndex()) {
            rays = rays.flatMap { x ->
                if (row[x] == '^') {
                    splitters.add(Point(x, index))
                    listOf(x - 1, x + 1)
                } else {
                    listOf(x)
                }
            }
                .distinct()
        }
        return splitters
    }

    val part2ExpectedResult: Result2 = 40L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)

        val startCol = input[0].indexOfFirst { it == 'S' }
        var rays: Map<Int, Long> = listOf(startCol).associateWith { 1L }

        for (row in input.drop(1)) {
            rays = rays.flatMap { (x: Int, count: Long) ->
                if (row[x] == '^') {
                    listOf(x - 1 to count, x + 1 to count)
                } else {
                    listOf(x to count)
                }
            }
                .groupBy { it.first }
                .mapValues { entry -> entry.value.sumOf { it.second } }
        }

        return rays.map { it.value }.sum()
    }

}


@OptIn(ExperimentalTime::class)
fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)

    fun <R> runPart(part: String, expectedTestResult: R, partEvaluator: (List<String>) -> R) {
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
