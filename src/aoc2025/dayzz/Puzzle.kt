package aoc2025.dayzz

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Long
typealias Result2 = Long


class Puzzle {


    fun parseInput(rawInput: List<String>): List<List<String>> {
        return rawInput[0].split(",")
            .filter { line -> true }
            .map { it.split("-") }
    }

    val part1ExpectedResult: Result1 = 1227775554
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)

        var result = 0L
        for (interval in input) {
            println(interval)
            for (i in interval[0].toLong()..interval[1].toLong()) {
                if (isInvalid(i)) {
                    println("*"+i)
                    result += i
                }
            }
        }
        return result
    }

    private fun isInvalid(i: Long): Boolean {
        val stringPattern = i.toString()
        if(stringPattern.length%2 == 1) {return false}
        return stringPattern.substring(0,stringPattern.length/2) ==
                stringPattern.substring(stringPattern.length/2,stringPattern.length)
    }

    val part2ExpectedResult: Result2 = 0L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)

        return 0
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
