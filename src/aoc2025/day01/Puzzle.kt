package aoc2025.day01

import utils.readInput
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int
typealias Result1 = Result
typealias Result2 = Result


class Puzzle {

    fun parseInput(rawInput: List<String>): List<Int> {
        return rawInput
            .filter { line -> true }
            .map { line ->
                when {
                    line.startsWith("L") -> -line.removePrefix("L").toInt()
                    line.startsWith("R") -> +line.removePrefix("R").toInt()
                    else -> throw IllegalArgumentException("Unexpected line $line")
                }
            }
    }

    val part1ExpectedResult: Result1 = 3
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)
        var direction = 50
        var key = 0
        for (mov in input) {
            // println(direction)
            direction = (direction + mov + 100) % 100
            if (direction == 0) {
                key++
            }
        }
        return key
    }

    val part2ExpectedResult: Result2 = 6
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)
        var direction = 50
        var key = 0
        for (mov in input) {
            val newDir = (direction + mov%100 + 100) % 100
            val counter = abs(mov) / 100
            if (counter > 0) {
                key += counter
            }
            if (newDir == 0) {
                println("" + direction + " " + mov + " -> " + newDir + " = " + 0)
                key++
            } else if ((direction != 0 && direction + mov%100 > 100) || (direction != 0 && direction + mov%100 < 0)) {
                println("" + direction + " " + mov + " -> " + newDir + " = " + 0)
                key++
            } else {
                println("" + direction + " " + mov + " -> " + newDir)
            }
            //2502 too low
            // 6825 too high
            // 6777 too high
            direction = newDir

        }
        return key
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
//            if (testResult != expectedTestResult) {
//                throw IllegalStateException("$testResult != $expectedTestResult")
//            }
        }
        val fullDuration = measureTime {
            val fullResult = partEvaluator(input)
            println("${part}: $fullResult")
        }
        println("${part}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
    }

    runPart<Result1>("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart<Result2>("part2", puzzle.part2ExpectedResult, puzzle::part2)


}
