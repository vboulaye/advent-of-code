package aoc2022.day03

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

class Line(
    val left: List<Char>,
    val right: List<Char>,
) {

    fun intersect():Char {
        val common = this.left.toSet().intersect(this.right)
        return common.iterator().next()
    }
}

class Line2(
    val a: List<Char>,
    val b: List<Char>,
    val c: List<Char>,
) {

    fun intersect():Char {
        val common = this.a.toSet().intersect(this.b).intersect(this.c)
        println("common:$common")
        return common.iterator().next()
    }
}


class Puzzle {
    fun clean(input: List<String>): List<List<Char>> {
        return input
            .filter {  line ->  true }
            .map { line -> line.toCharArray().toList() }
    }

    val part1ExpectedResult = 157L
    fun part1(rawInput: List<String>): Result {
        val lines = clean(rawInput)
        return lines
            .map {
//                println("line: $it")
                Line(
                    it.subList(0, it.size / 2),
                    it.subList(it.size / 2, it.size)
                )
            }
            .map {
                it.intersect()
            }
            .map { val priority = toPriority(it)
//                println("priority: $priority");
                priority
            }
            .sum().toLong()
    }

    private fun toPriority(next: Char): Int {
        if (next.code > 'a'.code) {
            return 1+next.code - 'a'.code
        }
        if (next.code > 'A'.code) {
            return 27+next.code - 'A'.code
        }
        throw IllegalStateException()
    }

    val part2ExpectedResult = 70L
    fun part2(rawInput: List<String>): Result {
        val lines = clean(rawInput)
        return lines.windowed(3,3)
            .stream()
            .map {
               println("line: $it")
                Line2(
                    it[0],
                    it[1],
                    it[2],
                )
            }
            .map {
                it.intersect()
            }
            .map { val priority = toPriority(it)
                println("priority: $priority");
                priority
            }
            .mapToLong { it.toLong() }
            .sum()
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
