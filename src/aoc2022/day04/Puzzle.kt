package aoc2022.day04

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Assignment(val from: Int, val to: Int) {
    companion object {
        fun build(s: String): Assignment {
            val split = s.split('-')
            return Assignment(split[0].toInt(), split[1].toInt())
        }
    }


}

class Pair(
    val a: Assignment,
    val b: Assignment,
) {
    fun overlaps(): Boolean {
        return (a.from <= b.from && b.to <= a.to)
                || (b.from <= a.from && a.to <= b.to)
    }

    fun overlapsAtAll(): Boolean {
        return (a.from <= b.from && b.from <= a.to)
                ||(a.from <= b.to && b.to <= a.to)
                || (b.from <= a.from && a.from <= b.to)
                || (b.from <= a.to && a.to <= b.to)
    }
}

class Puzzle {
    fun clean(input: List<String>): List<Pair> {
        return input
            .filter { line -> true }
            .map {
                val pairs = it.split(",")
                Pair(Assignment.build(pairs[0]), Assignment.build(pairs[1]))
            }
    }

    val part1ExpectedResult = 2L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.filter { it.overlaps() }.count().toLong()
    }

    val part2ExpectedResult = 4L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.filter { it.overlapsAtAll() }.count().toLong()
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
