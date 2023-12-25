package aoc2022.day10

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

interface Instr {
    fun getDuration(): Int
    fun run(x: Int): Int
}

class Noop : Instr {
    override fun getDuration(): Int {
        return 1
    }

    override fun run(x: Int): Int {
        return x
    }
}

class AddX(val value: Int) : Instr {
    override fun getDuration(): Int {
        return 2
    }

    override fun run(x: Int): Int {
        return x + value
    }
}


data class Stack(val x: Int, val cycle: Int)
class Puzzle {
    fun clean(input: List<String>): List<Instr> {
        return input
            .filter { line -> true }
            .map { line ->
                val split = line.split(" ")
                when (split[0]) {
                    "noop" -> Noop()
                    "addx" -> AddX(split[1].toInt())
                    else -> TODO("not")
                }
            }
    }

    val part1ExpectedResult = 13140L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var result: Long = 0
        var nextCycleCheck = 20

        input.foldIndexed(Stack(1, 1)) { index, stack, instr ->

            val newX = instr.run(stack.x)
            val newCycle = stack.cycle + instr.getDuration()
            if (stack.cycle <= nextCycleCheck && newCycle > nextCycleCheck) {
                val strength = nextCycleCheck * stack.x
                result += strength
                nextCycleCheck += 40
            }
            Stack(newX, newCycle)
        }
        return result
    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val crt = mutableListOf<Char>()
        (1..240).forEach { crt.add('.') }
        input.foldIndexed(Stack(1, 1)) { index, stack, instr ->

            val newX = instr.run(stack.x)
            val newCycle = stack.cycle + instr.getDuration()
            for (i in stack.cycle..newCycle - 1) {
                if ((i - 1) % 40 in (stack.x - 1..stack.x + 1)) {
                    crt[i - 1] = '#'
                }
            }
            Stack(newX, newCycle)
        }

        crt.forEachIndexed { index, car ->
            if (index % 40 == 0) {
                println()
            }
            print(car)
        }
        return 0
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
