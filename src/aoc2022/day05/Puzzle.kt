package aoc2022.day05

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = String


class Stacks(val stacks: List<List<Char>>) {

}

class Ctx(val stacks: Stacks, val moves: List<Move>) {
    fun move(): Stacks {
        return stacks

        // return moves.reduce { acc, move ->  }
    }

    fun applyMoves(): Stacks {
        val stacks1: Stacks = moves.fold(stacks) { stacks, move ->
            Stacks(stacks.stacks.mapIndexed { idx, stack ->
                when {
                    idx == move.from-1 -> stack.subList(0, stack.size - move.qty)
                    idx == move.to-1 -> {
                        val source = stacks.stacks[move.from-1]
                        stack + source.subList(source.size - move.qty, source.size)
                            .reversed()
                    }

                    else -> stack
                }
            })

        }
        return stacks1
    }

    fun applyMoves2(): Stacks {
        val stacks1: Stacks = moves.fold(stacks) { stacks, move ->
            Stacks(stacks.stacks.mapIndexed { idx, stack ->
                when {
                    idx == move.from-1 -> stack.subList(0, stack.size - move.qty)
                    idx == move.to-1 -> {
                        val source = stacks.stacks[move.from-1]
                        stack + source.subList(source.size - move.qty, source.size)
                    }

                    else -> stack
                }
            })

        }
        return stacks1
    }
}

class Move(val qty: Int, val from: Int, val to: Int)
class Puzzle {
    fun clean(input: List<String>): Ctx {

        val colsDef = input.filter { !it.startsWith("move") && it.contains("[") }
            .reversed()

//        println("colsDef:$colsDef")
        val colsNumber = input.filter { it.startsWith(" 1 ") }
            .first().length / 4 + 1

//        println("colsNumber:$colsNumber")
        val stack = List<List<Char>>(size = colsNumber) { idx ->
            colsDef
                .filter { it.length >= idx * 4 }
                .map { it[idx * 4 + 1] }
                .filter { it != ' ' }
                .toList()
        }

//        println("stack:$stack")

        val moves = input.filter { it.startsWith("move") }
            .map {
                val (qty, from, to) = Regex("""move (\d+) from (\d+) to (\d+)""").matchEntire(it)!!.destructured
                Move(qty.toInt(), from.toInt(), to.toInt())
            }
        return Ctx(Stacks(stack), moves)
    }

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val finalPos = input.applyMoves()
        return finalPos.stacks.map { it[it.size-1] }
            .joinToString("")
    }

    val part1ExpectedResult = "CMZ"

    val part2ExpectedResult = "MCD"
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val finalPos = input.applyMoves2()
        return finalPos.stacks.map { it[it.size-1] }
            .joinToString("")
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
