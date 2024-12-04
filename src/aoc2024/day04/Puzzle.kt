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

    fun horiz(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        val height = input.size
        val width = input[0].size
        val map = XMAS.indices
            .map { x + it to y }
            .filter { (x, y) -> x < width && x >= 0 && y < height && y >= 0 }
            .map { input[it.second][it.first] }
        return map
    }


    fun vert(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        val height = input.size
        val width = input[0].size
        val map = XMAS.indices
            .map { x to y + it }
            .filter { (x, y) -> x < width && x >= 0 && y < height && y >= 0 }
            .map { input[it.second][it.first] }
        return map
    }

    fun diag1(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        val height = input.size
        val width = input[0].size
        val map = XMAS.indices
            .map { x + it to y + it }
            .filter { (x, y) -> x < width && x >= 0 && y < height && y >= 0 }
            .map { input[it.second][it.first] }
        return map
    }

    fun diag2(input: List<List<Char>>, x: Int, y: Int): List<Char> {
        val height = input.size
        val width = input[0].size
        val map = XMAS.indices
            .map { x + it to y - it }
            .filter { (x, y) -> x < width && x >= 0 && y < height && y >= 0 }
            .map { input[it.second][it.first] }
        return map
    }
//    fun diag3(input: List<List<Char>>, x: Int, y: Int): List<Char> {
//        val height = input.size
//        val width = input[0].size
//        val map = XMAS.indices
//            .map { x-it to  y+it}
//            .filter { (x,y) -> x < width && x>=0 && y<height &&y>=0}
//            .map { input[it.second][it.first] }
//        return map
//    }
//    fun diag4(input: List<List<Char>>, x: Int, y: Int): List<Char> {
//        val height = input.size
//        val width = input[0].size
//        val map = XMAS.indices
//            .map { x+it to  y-it}
//            .filter { (x,y) -> x < width && x>=0 && y<height &&y>=0}
//            .map { input[it.second][it.first] }
//        return map
//    }

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val height = input.size
        val width = input[0].size
//        val horiz = input.sumOf { line ->
//            (0..width - XMAS.length - 1).count() {
//                line.subList(it, it + XMAS.length) == XMAS.toList()
//            }
//        }
        val totla = (0..height - 1).sumOf { y ->
            (0..width - 1).sumOf { x ->
                var count = 0
                if (isok(horiz(input, x, y))) count++
                if (isok(vert(input, x, y))) count++
                if (isok(diag1(input, x, y))) count++
                if (isok(diag2(input, x, y))) count++
//                if (isok(diag3(input, x, y))) count++
//                if (isok(diag4(input, x, y))) count++
                count
            }

        }
        return totla
    }

    private fun isok(horiz: List<Char>) = horiz.equals(XMAS_LIST) || horiz.equals(XMAS_LIST_REVERSED)

    val part2ExpectedResult = 9
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val height = input.size
        val width = input[0].size

        val totla = (1..height - 2).sumOf { y ->
            (1..width - 2)
                .count { x ->
                    input[y][x] == 'A'
                            && (
                            (input[y - 1][x - 1] == 'M' && input[y + 1][x + 1] == 'S' && input[y - 1][x + 1] == 'M' && input[y + 1][x - 1] == 'S')
                                    || (input[y - 1][x - 1] == 'M' && input[y + 1][x + 1] == 'S' && input[y - 1][x + 1] == 'S' && input[y + 1][x - 1] == 'M')
                                    || (input[y - 1][x - 1] == 'S' && input[y + 1][x + 1] == 'M' && input[y - 1][x + 1] == 'S' && input[y + 1][x - 1] == 'M')
                                    || (input[y - 1][x - 1] == 'S' && input[y + 1][x + 1] == 'M' && input[y - 1][x + 1] == 'M' && input[y + 1][x - 1] == 'S')
                            )
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
