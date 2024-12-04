package aoc2024.day04

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Char> {
        return this.map { it }
    }

    fun clean(input: List<String>): List<List<Char>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val XMAS = "XMAS"
    val XMAS_LIST = "XMAS".toList()
    val XMAS_LIST_REVERSED = "XMAS".toList().reversed()

    val part1ExpectedResult = 18


    fun buildSequence(
        input: List<List<Char>>,
        x: Int, y: Int,
        transformations: List<Pair<Int, Int>>
    ): List<Char> {
        val height = input.size
        val width = input[0].size
        return transformations
            .map { (dx, dy) -> x + dx to y + dy }
            .filter { (nx, ny) ->
                nx in 0 until width
                        && ny in 0 until height
            }
            .map { (nx, ny) -> input[ny][nx] }
    }

    fun horiz(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        return buildSequence(input, x, y, XMAS.indices.map { it to 0 })
    }


    fun vert(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        return buildSequence(input, x, y, XMAS.indices.map { 0 to it })
    }

    fun diag1(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        return buildSequence(input, x, y, XMAS.indices.map { it to it })
    }

    fun diag2(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        return buildSequence(input, x, y, XMAS.indices.map { it to -it })
    }

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val height = input.size
        val width = input[0].size
        val total = (0 until height).sumOf { y ->
            (0 until width).sumOf { x ->
                listOf(
                    horiz(input, x, y),
                    vert(input, x, y),
                    diag1(input, x, y),
                    diag2(input, x, y),
                )
                    .count { isXmasString(it) }
            }

        }
        return total
    }

    private fun isXmasString(horiz: List<Char>) = horiz == XMAS_LIST || horiz == XMAS_LIST_REVERSED

    val part2ExpectedResult = 9
    fun isBranch(branch: Pair<Char, Char>): Boolean {
        return (branch.first == 'M' && branch.second == 'S') || (branch.first == 'S' && branch.second == 'M')
    }

    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val height = input.size
        val width = input[0].size

        val totla = (1 until height - 1).sumOf { y ->
            (1 until width - 1)
                .count { x ->
                    input[y][x] == 'A'
                            && isBranch(input[y - 1][x - 1] to input[y + 1][x + 1])
                            && isBranch(input[y - 1][x + 1] to input[y + 1][x - 1])
                }
        }
        return totla
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
