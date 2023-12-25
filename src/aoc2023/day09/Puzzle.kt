package aoc2023.day09

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    data class Diff(val values: List<Int>) {
        fun buildNext(): Diff {
            return Diff(this.values.zipWithNext().map { (a, b) -> b - a })
        }

        fun isZero(): Boolean {
            return values.all { it == 0 }
        }

    }

    data class DiffList(val values: List<Diff>) {
        fun nextValue(): Int {
            return values.map { it.values.last() }.reduceRight { diff, acc -> acc + diff }
        }

        fun previousValue(): Int {
            return values.map { it.values.first() }.reduceRight { diff, acc -> diff - acc }
        }
    }

    data class History(val values: List<Int>) {
        fun toDiffs(): DiffList {
            return DiffList(buildList {
                add(Diff(values))
                while (!last().isZero()) {
                    add(last().buildNext())
//                    println("curr:"+curr)
                }
            })
        }

    }

    fun String.acceptInput() = true

    fun String.parseInput() = this.split(" ").map { it.trim().toInt() }

    fun clean(input: List<String>): List<History> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> History(line.parseInput()) }
    }

    val part1ExpectedResult = 114
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.sumOf { line -> line.toDiffs().nextValue() }
    }

    val part2ExpectedResult = 2
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.sumOf { line -> line.toDiffs().previousValue() }
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
