package aoc2024.day17

import utils.readInput
import java.lang.Math.pow
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = String


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    data class Program(var a: Int, var b: Int, var c: Int, var program: List<Char>)

    fun clean(input: List<String>): Program {
        var p: Program = Program(0, 0, 0, emptyList())


        input
            .filter { line -> line.acceptInput() }
            .map { line ->
                when {
                    line.startsWith("Register A") -> p.a = line.split(": ")[1].toInt()
                    line.startsWith("Register B") -> p.b = line.split(": ")[1].toInt()
                    line.startsWith("Register C") -> p.c = line.split(": ")[1].toInt()
                    line.startsWith("Program:") -> {
                        val split = line.split(": ")[1].split(",")
                        p.program = split.map { it[0] }
                    }
                }
            }
        return p
    }

    val part1ExpectedResult = "4,6,3,5,6,3,5,2,1,0"
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val result = mutableListOf<Int>()
        var index = 0
        while (index < input.program.size ) {
            println(index)
            val instruction = input.program[index]
            val operand = input.program[index + 1]
            val literal = input.program[index + 1].code-'0'.code

            println(input.toString() + " " + instruction + " " + operand+ " "+literal)
            // 6,5,4,7,2,1,0,5,4
            // 6,5,4,7,2,1,0,5,4
            when (instruction) {
                '0' -> input.a = input.a / pow(2.toDouble(), getOpValue(operand, input).toDouble()).toInt()
                '1' -> input.b = input.b xor literal
                '2' -> input.b = getOpValue(operand, input) % 8
                '3' -> when {
                    input.a == 0 -> instruction
                    else -> {
                        index = literal - 2
                    }
                }

                '4' -> input.b = input.b xor input.c
                '5' -> result.add(getOpValue(operand, input) % 8)
                '6' -> input.b = input.a / pow(2.toDouble(), getOpValue(operand, input).toDouble()).toInt()
                '7' -> input.c = input.a / pow(2.toDouble(), getOpValue(operand, input).toDouble()).toInt()
                else -> throw IllegalArgumentException("Invalid operand $operand")
            }
            index += 2
        }
        return result.joinToString(",")
    }

    private fun getOpValue(operand: Char, input: Program) = when (operand) {
        '0' -> 0
        '1' -> 1
        '2' -> 2
        '3' -> 3
        '4' -> input.a
        '5' -> input.b
        '6' -> input.c
        else -> throw IllegalArgumentException("Invalid operand $operand")
    }

    val part2ExpectedResult = ""
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return ""
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
