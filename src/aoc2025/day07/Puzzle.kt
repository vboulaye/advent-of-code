package aoc2025.day07

import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle
import org.jetbrains.kotlinx.multik.ndarray.complex.Complex.Companion.i
import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Long
typealias Result2 = Long


class Puzzle {


    fun parseInput(rawInput: List<String>): List<List<Char>> {
        return rawInput
            .filter { line -> true }
            .map { line -> line.toList() }
    }

    val part1ExpectedResult: Result1 = 21L
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)

        var splitter = points(input)

        return splitter.size.toLong()
    }

    private fun points(input: List<List<Char>>): MutableSet<Point> {
        var splitter = mutableSetOf<Point>()
        var rays = listOf(input[0].indexOfFirst { it == 'S' })
        for ((index, row) in input.drop(1).withIndex()) {
//            println("$index: $row")
//            println("$rays")
            rays = rays.flatMap { x ->
                if (row[x] == '^') {
                    splitter.add(Point(x, index))
                    listOf(x - 1, x + 1)
                } else {
                    listOf(x)
                }
            }
                .distinct()
//            println(rays)
//            println()
        }
        return splitter
    }

    val part2ExpectedResult: Result2 = 40L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)

        var result = 1L
        var splitter1 = mutableSetOf<Point>()
        var rays: Map<Int, Long> = listOf(input[0].indexOfFirst { it == 'S' }).map { it to 1L }.toMap()
        var raysHistory = mutableListOf<List<Int>>()
//        rays = rays.map { it.key to 0 }.toMap().toMutableMap()
        for ((index, row) in input.drop(1).withIndex()) {
            println("$index: ${row.joinToString("")}")
//            println("$rays")
            var hasSplit = false
//            var newRays = mutableListOf<Int>()
            val newPoints = rays.flatMap { (x: Int, count: Long) ->
                if (row[x] == '^') {
                    splitter1.add(Point(x, index))
                    hasSplit = true
//                    newRays.add(x - 1)
//                    newRays.add( x + 1)
                    result++
                    //val newcount = count + 1
                    val newcount = count
                    listOf(x - 1 to newcount, x + 1 to newcount)
                } else {
                    listOf(x to count)
                }
            }
                .groupBy { it.first }
                .mapValues { entry -> entry.value.sumOf { it.second } }
//                .distinct()
            println("$newPoints")
            rays = newPoints
//
//            if (hasSplit) {
//                //result += newRays.distinct().size.toLong()
//                result += newPoints.sumOf { rays[it] }.toLong()
//                // println("counter: $result < ${rays.size}")
//            }
//            rays = newPoints.groupingBy { it }.eachCount()

//            raysHistory.add(rays)
            println(index)
            println(rays.size)
            println()
        }

        result = rays.map { it.value }.sum().toLong()

//        for (y in raysHistory.size - 1 downTo 1) {
//            result += (raysHistory[y].size - raysHistory[y - 1].size).toLong()
//           // val count = splitter1.filter { it.y == y }.flatMap { listOf(it.x - 1, it.x + 1) }.distinct().count()
//        }
        var splitter = splitter1

//        for ((index, row) in input.drop(1).withIndex()) {
//            val count = splitter.filter { it.y == index }.flatMap { listOf(it.x - 1, it.x + 1) }.distinct().count()
//            if (count > 0) {
//                result *= count
//            }
//        }
        //3099
        // 413655020 low
        // 29893386035180 low
        // 1786
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
