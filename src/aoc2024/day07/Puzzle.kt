package aoc2024.day07

import utils.readInput
import utils.subListToEnd
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    data class Equation(val result: Long, val terms: List<Long>)

    fun String.parseInput(): Equation {
        val split: List<String> = this.split(": ")
        val result = split[0].toLong()
        val terms = split[1].split(' ').map { it.toLong() }
        return Equation(result, terms)
    }

    fun clean(input: List<String>): List<Equation> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 3749L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.filter { equation -> hasMatch(equation, listOf()) }.sumOf { it.result }
    }

    private fun hasMatch(equation: Equation, operators: List<Char>): Boolean {
        if (testWith('*', equation, operators)) return true
        if (testWith('+', equation, operators)) return true
        if (part == 2) {
            if (testWith('|', equation, operators)) return true
        }
        return false
    }

    var part = 1
    private fun testWith(operator: Char, equation: Equation, operators: List<Char>): Boolean {
        val (result, terms) = equation
        val atEnd = terms.size == 2

        val firstTerm = equation.terms[0]
        val nextTerm = equation.terms[1]

        val newValue = when (operator) {
            '+' -> firstTerm + nextTerm
            '*' -> firstTerm * nextTerm
            '|' -> (firstTerm.toString() + nextTerm.toString()).toLong()
            else -> error("unknown operator $operator")
        }
        if (atEnd) {
            return newValue == result
        }

        val newOperators = operators + operators
        val newTerms = listOf(newValue) + equation.terms.subListToEnd(2)

        return hasMatch(Equation(result, newTerms), newOperators)
    }

    val part2ExpectedResult = 11387L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        part = 2
        return input.filter { equation -> hasMatch(equation, listOf()) }.sumOf { it.result }
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
