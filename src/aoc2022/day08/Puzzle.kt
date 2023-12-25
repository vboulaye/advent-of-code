package aoc2022.day08

import utils.readInput
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {
    fun clean(input: List<String>): List<List<Int>> {
        return input
            .filter { line -> true }
            .map { line -> line.toCharArray().map { it.digitToInt() }.toList() }
    }

    val part1ExpectedResult = 21L
    fun part1(rawInput: List<String>): Result {

        val grid = clean(rawInput)

        println()
        grid.forEach {
            it.forEach {
                print(it)
            }
            println()
        }
        println()

        var count: Long = 0
        grid.forEachIndexed { y, lines ->
            lines.forEachIndexed { x, tree ->
                if (isReallyVisible(grid, y, x) > 0) {
                    count++
                    print('X')
                } else {
                    print('.')
                }
            }
            println()
        }
        return count
    }

    private fun isReallyVisible(grid: List<List<Int>>, y: Int, x: Int): Long {
        val size = grid.size - 1
        if (x == 0 || y == 0 || x == size || y == size) return 1
        val current = grid[y][x]
        var visibleTop = 0
        for (test in 0..y - 1) {
            if (grid[test][x] >= current) {
                visibleTop = 0
                break
            }
            visibleTop++
        }
        var visibleBottom = 0

        for (test in size downTo y + 1) {
            if (grid[test][x] >= current) {
                visibleBottom = 0
                break
            }
            visibleBottom++

        }
        var visibleLeft = 0

        for (test in 0..x - 1) {
            if (grid[y][test] >= current) {
                visibleLeft = 0
                break
            }
            visibleLeft++

        }
        var visibleRight = 0
        for (test in size downTo x + 1) {
            if (grid[y][test] >= current) {
                visibleRight = 0
                break
            }
            visibleRight++
        }
        val visibleTrees = listOf(visibleLeft, visibleBottom, visibleTop, visibleRight).filter { it > 0 }
        if (visibleTrees.isEmpty()) return 0
        return visibleTrees
            .reduce { acc: Int, i: Int -> acc * i }
            .toLong()
    }


    private fun computeScore(grid: List<List<Int>>, y: Int, x: Int): Long {
        val size = grid.size - 1
        if (x == 0 || y == 0 || x == size || y == size) return 1
        val current = grid[y][x]
        var visibleTop = 0
        for (test in y - 1 downTo 0) {
            visibleTop++
            if (grid[test][x] >= current) {
                break
            }
        }
        var visibleBottom = 0
        for (test in y + 1..size) {
            visibleBottom++
            if (grid[test][x] >= current) {
                break
            }

        }
        var visibleLeft = 0

        for (test in x - 1 downTo 0) {
            visibleLeft++
            if (grid[y][test] >= current) {
                break
            }

        }
        var visibleRight = 0
        for (test in x + 1..size) {
            visibleRight++
            if (grid[y][test] >= current) {
                break
            }
        }
        val visibleTrees = listOf(visibleLeft, visibleBottom, visibleTop, visibleRight).filter { it > 0 }
        if (visibleTrees.isEmpty()) return 0
        return visibleTrees
            .reduce { acc: Int, i: Int -> acc * i }
            .toLong()
    }


    val part2ExpectedResult = 8L
    fun part2(rawInput: List<String>): Result {
        val grid = clean(rawInput)

        var count: Long = 0
        grid.forEachIndexed { y, lines ->
            lines.forEachIndexed { x, tree ->


                if (isReallyVisible(grid, y, x) > 0) {
                    val score = computeScore(grid, y, x)
                    if (score > count)
                        count = score
                }

            }
            println()
        }
        return count
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
