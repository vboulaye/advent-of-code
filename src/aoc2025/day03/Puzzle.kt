package aoc2025.day03

import org.jetbrains.kotlinx.multik.ndarray.complex.Complex.Companion.i
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
//        for (chars in input) {
//            var list= chars
//            var first = list.sorted().last()
//            while (chars.indexOf(first)==chars.size-1) {
//                list=list.take(list.size - 1)
//                first = list.sorted().last()
//            }
//            val value: String
//
//            val subList = chars.subList(chars.indexOf(first) + 1, chars.size)
//            var second = subList.sorted().last()
//            value=first+second
//            println(value)
//            result += value.toInt()
//        }

        for (chars in input) {
            var first = chars.subList(0, chars.size - 1).sorted().last()
            val value: String
            val subList = chars.subList(chars.indexOf(first) + 1, chars.size)
            var second = subList.sorted().last()
            value = first + second
            println(value)
            result += value.toInt()
        }
        return result
    }

    val part2ExpectedResult: Result2 = 3121910778619L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)

        var result = 0L
        for (chars in input) {
            var list2 = chars
            var ok = false
            var value: String = "x"
            while (!ok) {
                try {
                    var currentMax = list2.subList(0, list2.size - 11).sorted().last()
                    value = currentMax
                    var subList = list2
                    for (i in 10 downTo 0) {
                        subList = subList.subList(subList.indexOf(currentMax) + 1, subList.size)
                        currentMax = subList.subList(0, subList.size - i).sorted().last()
                        value += currentMax
                    }
                    ok = true
                } catch (e: Exception) {
                    list2 = list2.take(list2.size - 1)
                }
            }

//            var subList = chars.subList(chars.indexOf(first) + 1, chars.size)
//            var second = subList.subList(0, chars.size - 10).sorted().last()
//             subList = chars.subList(chars.indexOf(second) + 1, chars.size)
//            var third = subList.subList(0, chars.size - 9).sorted().last()
//              subList = chars.subList(chars.indexOf(third) + 1, chars.size)
//            value = first + second
            println(value)
            result += value.toLong()
        }
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
