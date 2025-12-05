package aoc2025.day05

import sun.font.GlyphLayout.done
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Int
typealias Result2 = Long


class Puzzle {

    data class Game(
        val ranges: List<LongRange>,
        val ids: List<Long>
    )

    fun parseInput(rawInput: List<String>): Game {

        var insideRange = true
        val ranges = mutableListOf<LongRange>()
        val ids = mutableListOf<Long>()
        for ((index, string) in rawInput.withIndex()) {
            if (string.isEmpty()) {
                insideRange = false
            } else if (insideRange) {
                val r = string.split("-").map { it.toLong() }
                val r2 = (r[0]..r[1])
                ranges.add(r2)
            } else {
                ids.add(string.toLong())
            }
        }

        return Game(ranges, ids)
    }

    val part1ExpectedResult: Result1 = 3
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)

        var result = 0
        input.ids.forEach { id ->
            input.ranges.any { range -> id in range }.let { contained ->
                if (contained) {
                    result += 1
                }
            }
        }
        return result
    }

    val part2ExpectedResult: Result2 = 14L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)


        var ranges: List<LongRange> =input.ranges
        do {
            val prev=ranges.toList()
            ranges = merge(ranges)
        } while (ranges.size != prev.size)
        return ranges.sumOf { range -> range.endInclusive - range.start +1 }
    }

    private fun merge(ranges: List<LongRange>): List<LongRange> {
        val mutableRanges = mutableListOf<LongRange>()
        ranges.forEach { range ->
            var done = false
            for (mergedRange in mutableRanges) {

                if (range.first in mergedRange || range.last in mergedRange
                    || mergedRange.first in range || mergedRange.last in range
                ) {
                    println(range)
                    val newRange = (minOf(range.first, mergedRange.first)..maxOf(range.last, mergedRange.last))
                    mutableRanges.remove(mergedRange)
                    mutableRanges.add(newRange)
                    done = true
                    break;
                }
            }
            if (!done) {
                mutableRanges.add(range)
            }
        }


        return mutableRanges

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
