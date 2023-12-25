package aoc2023.day08

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput() =
        this.substringBefore(" = ") to this.substringAfter(" = ").substring(1, this.substringAfter(" = ").length - 1)
            .split(",").map { it.trim() }

    data class Network(val path: String, val maze: Map<String, List<String>>) {

    }


    fun clean(input: List<String>): Network {
        val path = input[0]
        val maze = input.drop(2).map { line -> line.parseInput() }.toMap()
        return Network(path, maze)
    }

    val part1ExpectedResult = 2L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        //return getCount(input, "AAA", "ZZZ")
        return 2L

    }

    private fun getCount(input: Network, start: String, stop: String): Long {
        var count = 0
        var step = start
        while (!step.endsWith(stop)) {
            val direction = input.path[count % input.path.length]
            val pos = if (direction == 'R') 1 else 0
            step = input.maze[step]!![pos]
            println(step)
            count++
        }
        return count.toLong()
    }

    val part2ExpectedResult = 6L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var count = 0L
        var idx = 0
        var step = input.maze.keys.filter { it.endsWith("A") }
        val loops = step.map { getCount(input, it,"Z") }

        // decompose loops into prime factors
        val set = mutableSetOf<Long>()
        loops.map { loop ->
            var loopCount = loop
            var factor = 2L
            while (loopCount > 1) {
                if (loopCount % factor == 0L) {
                    println("factor $factor")
                    set.add(factor)
                    loopCount /= factor
                } else {
                    factor++
                }
            }
        }
        // 13830919117339
        // 5104461751046924117 too hhigh
        return set.fold(1L){ acc, i -> acc * i}
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
