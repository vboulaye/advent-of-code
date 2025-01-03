package utils

import kotlin.reflect.KFunction1
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

abstract class AbstractPuzzle {
    abstract fun part1(input: List<String>): Long
    abstract fun part2(input: List<String>): Long

    var insidePart: Int = 1

    @Target(
        AnnotationTarget.FUNCTION,
    )
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestResult(
        val result: Long
    )

    @OptIn(ExperimentalTime::class)
    fun run() {
        val puzzle = this
        println(this::class)

        val testInput = readInput("00test", this::class)
        val input = readInput("zzdata", this::class)


        fun runPart(part: Int, expectedTestResult: Long, partEvaluator: (List<String>) -> Long) {
            insidePart = part
            val testDuration = measureTime {
                val testResult = partEvaluator(testInput)
                check(testResult == expectedTestResult)
                println("test part ${insidePart}: $testResult == ${expectedTestResult}")
            }
            val fullDuration = measureTime {
                val fullResult = partEvaluator(input)
                println("part ${insidePart}: $fullResult")
            }
            println("part ${insidePart}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
        }

        runPart(
            1,
            parseLambdaForTestResult(puzzle::part1),
            puzzle::part1
        )
        runPart(
            2,
            parseLambdaForTestResult(puzzle::part2),
            puzzle::part2
        )

    }

    private fun parseLambdaForTestResult(function: KFunction1<List<String>, Long>) =
        (function.annotations.find { it is TestResult }!! as TestResult).result
}
