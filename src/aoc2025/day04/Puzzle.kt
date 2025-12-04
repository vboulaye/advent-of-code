package aoc2025.day04

import utils.Point
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

    private fun getBalls(input: MutableList<MutableList<Char>>): MutableSet<Point> {
        val result1 = mutableSetOf<Point>()
        for (y in input.indices) {
            val line = input[y]
            for (x in line.indices) {
                if (input[y][x] == '@') {
                    //println("found at $y,$x")
                    result1 += Point(x, y)
                }
            }
        }
        return result1

    }

    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)
        val balls = getBalls(input)
        val result = getPossibleRolls(balls)
        return result.size
    }

    val part2ExpectedResult: Result2 = 43
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)

        val balls = getBalls(input)
        var total = 0
        do {
            val possibleRolls = getPossibleRolls(balls)
            balls.removeAll(possibleRolls)
            total += possibleRolls.size
        } while (possibleRolls.isNotEmpty())

        return total
    }

    private fun getPossibleRolls(input: Set<Point>): List<Point> {
        return input.filter { countSurroundingBalls(it, input) < 4 }
    }

    private fun countSurroundingBalls(point: Point, balls: Set<Point>): Int {
        var c = 0
        for (p in point.allNeighbours()) {
            if (p in balls) {
                c++
            }
        }
        return c
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
