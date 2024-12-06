package aoc2024.day06

import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Char> {
        return this.toList()
    }

    fun clean(input: List<String>): List<List<Char>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 41
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val guardMoves = listOf('<', '>', 'v', '^')
        val y = input.indices.find { i -> input[i].any { guardMoves.contains(it) } }!!
        val x = input[y].indices.find { i ->  guardMoves.contains(input[y][i]) }!!
        var guard = Point(x, y)
        val moves= mutableSetOf(guard)
        var direction=input[guard.y][guard.x]
        while (guard.x in input[0].indices && y in input.indices) {
            moves.add(guard)
            val newg= when(direction) {
                '^' -> guard.plus(Point(0,-1))
                'v' -> guard.plus(Point(0,1))
                '<' -> guard.plus(Point(-1,0))
                '>' -> guard.plus(Point(1,0))
                else -> throw Exception("wtf")
            }
            try {
                when (input[newg.y][newg.x]) {
                    '#' -> {
                        direction = when (direction) {
                            '^' -> '>'
                            'v' -> '<'
                            '<' -> '^'
                            '>' -> 'v'
                            else -> throw Exception("wtf")
                        }
                    }

                    else -> guard = newg
                }
            } catch (e:Exception) {
                break;
            }

        }

        return moves.size
    }

    val part2ExpectedResult = 0
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

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
