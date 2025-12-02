package aoc2025.day02

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Long
typealias Result2 = Long


class Puzzle {


    fun parseInput(rawInput: List<String>): List<List<String>> {
        return rawInput[0]
            .split(",")
            .map { it.split("-") }
    }

    val part1ExpectedResult: Result1 = 1227775554
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)
        return input.fold(0L) { result, interval ->
            result + (interval[0].toLong()..interval[1].toLong())
                .filter { isInvalid(it) }
                .sum()
        }
    }

    private fun isInvalid(i: Long): Boolean {
        val stringPattern = i.toString()
        if (stringPattern.length % 2 == 1) {
            return false
        }
        return stringPattern.take(stringPattern.length / 2) ==
                stringPattern.substring(stringPattern.length / 2)
    }

    private fun isInvalid2(i: Long): Boolean {
        val stringPattern = i.toString()
//        for (l in 1..<stringPattern.length) {
//            if (stringPattern.length % l == 0) {
//                if (stringPattern.chunked(l).toSet().size == 1) {
//                    return true
//                }
//            }
//        }
        for (l in 1..stringPattern.length / 2) {
            if (stringPattern.length % l == 0) {
                val block = stringPattern.take(l)
                if (block.repeat(stringPattern.length / l) == stringPattern) {
                    return true
                }
            }
        }
//        (1..stringPattern.length / 2).forEach {
//            if (stringPattern.length % it == 0) {
//                val block = stringPattern.take(it)
//                if (block.repeat(stringPattern.length / it) == stringPattern) {
//                    return true
//                }
//            }
//        }
        return false
    }

    val part2ExpectedResult: Result2 = 4174379265L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)
        return input.fold(0L) { result, interval ->
            result + (interval[0].toLong()..interval[1].toLong())
                .filter { isInvalid2(it) }
                .sum()
        }
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
