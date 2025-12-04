package aoc2025.day04

import org.jetbrains.kotlinx.multik.ndarray.complex.Complex.Companion.i
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Int
typealias Result2 = Int


class Puzzle {


    fun parseInput(rawInput: List<String>): MutableList<MutableList<Char>> {
        return rawInput
            .filter { line -> true }
            .map { line -> line.toMutableList() }.toMutableList()
    }

    val part1ExpectedResult: Result1 = 13
    private fun count(x: Int, y: Int, input: List<List<Char>>): Int {

        var c = 0
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                var nx = x + dx
                var ny = y + dy
                if (ny in input.indices && nx in input[ny].indices) {
                    if (input[ny][nx] == '@') {
                        c++
                    }
                }
            }
        }
        return c
    }

    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)
        var result = 0
        for (y in input.indices) {
            val line = input[y]
            // Implement the logic for part 1 here
            for (x in line.indices) {
                if (input[y][x] == '@' && count(x, y, input) < 4) {
                    println("found at $y,$x")
                    result++
                }
            }
        }
        return result
    }

    val part2ExpectedResult: Result2 = 43
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)
        var result = 0
        var round: Int=-1
        while(round!=0) {
            round = removePossible(input)
            result+=round
        }
        return result
    }

    private fun removePossible(input: MutableList<MutableList<Char>>): Int {
        var result1 = 0
        for (y in input.indices) {
            val line = input[y]
            // Implement the logic for part 1 here
            for (x in line.indices) {
                if (input[y][x] == '@' && count(x, y, input) < 4) {
                    println("found at $y,$x")
                    input[y][x]= 'x'
                    result1++
                }
            }
        }
        return result1
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
