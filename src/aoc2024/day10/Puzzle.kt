package aoc2024.day10

import utils.Point
import utils.containsPoint
import utils.getPoint
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Int> {
        return this.toList()
            .map { it.code - '0'.code }
    }

    fun clean(input: List<String>): List<List<Int>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 36
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.indices.flatMap { y ->
            input[y].indices.flatMap { x ->
                val value = input[y][x]
                if (value != 0) {
                    listOf()
                } else {
                    val paths = mutableSetOf<Point>()
                    buildPaths(Point(x, y), input, paths)
                    paths
                }
            }
        }.count()

    }

    private fun buildPaths(point: Point, input: List<List<Int>>, paths: MutableCollection<Point>) {
        val value = input.getPoint(point)
        point.neighbours()
            .filter { neighbour ->
                input.containsPoint(neighbour)
                        && input.getPoint(neighbour) == value + 1
            }
            .forEach { neighbour ->
                if (input.getPoint(neighbour)  == 9) {
                    paths.add( neighbour)
                } else {
                    buildPaths(neighbour, input, paths)
                }
            }
    }

    val part2ExpectedResult = 81
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.indices.flatMap { y ->
            input[y].indices.flatMap { x ->
                val value = input[y][x]
                if (value != 0) {
                    listOf()
                } else {
                    val paths = mutableListOf<Point>()
                    buildPaths(Point(x, y), input, paths)
                    paths
                }
            }
        }.count()
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
