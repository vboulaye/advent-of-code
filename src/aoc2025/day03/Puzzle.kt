package aoc2025.day03

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Int
typealias Result2 = Long


class Puzzle {


    fun parseInput(rawInput: List<String>): List<List<String>> {
        return rawInput
            .filter { line -> true }
            .map { line -> line.toList().map { it.toString() } }
    }

    val part1ExpectedResult: Result1 = 357
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)

        var result = 0
        for (chars in input) {
            val value: String = computeMaxValue(chars, 2)
            result += value.toInt()
        }
        return result
    }

    val part2ExpectedResult: Result2 = 3121910778619L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)

        var result = 0L
        for (chars in input) {
            val value: String = computeMaxValue(chars, 12)
            result += value.toLong()
        }
        return result
    }

    private fun computeMaxValue(chars: List<String>, totalDigitsToFind: Int): String {
        var result = ""

        var remainingChars = chars

        for (remainingBlocSize in (totalDigitsToFind - 1) downTo 0) {

            // look for the max in the sublist that allows to have enough remaining chars
            val searchRangeEnd = remainingChars.size - remainingBlocSize
            val maxDigit = remainingChars.subList(0, searchRangeEnd).maxOf { it }


            // update the work list starting from the max we found
            val indexOfMaxDigit = remainingChars.indexOf(maxDigit)
            remainingChars = remainingChars.subList(indexOfMaxDigit + 1, remainingChars.size)

            // append the found max digit to the result
            result += maxDigit
        }
        println(result)
        return result
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
