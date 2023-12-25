package aoc2023.day13

import utils.readInput
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    data class Grid(val grid: List<String>) {

        override fun toString(): String {
            return grid.map { it + "\n" }.joinToString("")
        }

        fun summarize(currentSummary: Int?): Int {
            val horizontalLine = (0..grid.size - 2).find { y ->
                if (currentSummary == computeHorizontalSummary(y)) return@find false
                val length = min(grid.size - y, y)
                val isok = (0..length).all { test ->
                    if (y - test < 0 && y + test + 1 >= grid.size) {
                        return@all false
                    }
                    if (y - test < 0 || y + test + 1 >= grid.size) {
                        return@all true
                    }
                    val top = grid[y - test]
                    val bottom = grid[y + test + 1]
                    top == bottom
                }
                isok
            }
            if (horizontalLine != null) {
                return computeHorizontalSummary(horizontalLine)
            }
            val width = grid[0].length
            val verticalLine = (0..width - 2).find { x ->
                if (currentSummary == computeVerticalSummary(x)) return@find false
                val length = min(width - x, x)
                (0..length).all { test ->
                    run testX@{
                        (0 until grid.size).all { y ->
                            if (x - test < 0 && x + test + 1 >= width) {
                                return@testX false
                            }
                            if (x - test < 0 || x + test + 1 >= width) {
                                return@testX true
                            }
                            val left = grid[y][x - test]
                            val right = grid[y][x + test + 1]
                            left == right
                        }
                    }
                }
            }

            if (verticalLine != null) {
                return computeVerticalSummary(verticalLine)
            }
            return 0
        }

        private fun computeHorizontalSummary(y: Int) = 100 * (y + 1)

        private fun computeVerticalSummary(verticalLine: Int) = verticalLine + 1

    }

    fun clean(input: List<String>): List<Grid> {
        return input.fold(mutableListOf(mutableListOf<String>())) { acc, line ->
            if (line.isEmpty()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(line)
            }
            acc
        }.map { Grid(it.toList()) }
    }

    val part1ExpectedResult = 405
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.mapIndexed { idx, it ->
            println(idx)
            it.summarize(null)
        }.sum()
    }

    val part2ExpectedResult = 400
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.mapIndexed { idx, it ->
            println(idx)
            val currentSummary = it.summarize(null)
            val otherSummary = run {
                (0 until it.grid.size).forEach { y ->
                    (0 until it.grid[0].length).forEach { x ->
                        val grid = it.grid.toMutableList()
                        grid[y] = grid[y].toCharArray().also { it[x] = if (it[x] == '.') '#' else '.' }.joinToString("")
                        val newGrid = Grid(grid)
                        val summary = newGrid.summarize(currentSummary)
                        if (summary > 0
                        //&& currentSummary != summary
                        ) {
                            println("found $summary")
                            return@run summary
                        }
                    }
                }
                //  currentSummary
                throw IllegalStateException("not found")
            }
            otherSummary
            // too low 21240
            // too high 38261
            //it.summarize()
        }.sum()
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
