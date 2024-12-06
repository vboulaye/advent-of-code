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
        val guardDir = getGuardDirection(input)
        val moves = getMoves(guardDir, input)

        return moves.size
    }

    data class Guard(val position: Point, val direction: Char)

    private fun getGuardDirection(input: List<List<Char>>): Guard {
        val guardMoves = setOf('<', '>', 'v', '^')
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] in guardMoves) {
                    val guard = Point(x, y)
                    val direction = input[y][x]
                    return Guard(guard, direction)
                }
            }
        }
        throw IllegalArgumentException("Guard not found")
    }

    private fun getMoves(
        guardDir: Guard,
        input: List<List<Char>>,

        ): MutableSet<Point> {
        var (currentPosition, currentDirection) = guardDir
        val moves = mutableSetOf<Point>()
        while (true) {
            moves.add(currentPosition)
            val newPosition = when (currentDirection) {
                '^' -> currentPosition + Point(0, -1)
                'v' -> currentPosition + Point(0, 1)
                '<' -> currentPosition + Point(-1, 0)
                '>' -> currentPosition + Point(1, 0)
                else -> throw Exception("wtf")
            }
            if (newPosition.y !in input.indices
                || newPosition.x !in input[newPosition.y].indices
            ) {
                return moves
            }
            when (input[newPosition.y][newPosition.x]) {
                '#' -> {
                    currentDirection = when (currentDirection) {
                        '^' -> '>'
                        'v' -> '<'
                        '<' -> '^'
                        '>' -> 'v'
                        else -> throw Exception("wtf")
                    }
                }

                else -> currentPosition = newPosition
            }

        }
    }

    val part2ExpectedResult = 6
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val guardDir = getGuardDirection(input)
        val moves = getMoves(guardDir, input)

        return moves
            .filter { bloc -> bloc != guardDir.position }
            .filter { bloc -> getMoves2(bloc, guardDir, input) }
            .count()
    }

    private fun getMoves2(
        bloc: Point,
        guardDir: Guard,
        inputRaw: List<List<Char>>,

        ): Boolean {

        val input = inputRaw.map { it.toMutableList() }
        input[bloc.y][bloc.x] = 'O'
        var (currentPosition, currentDirection) = guardDir
        val moves = mutableSetOf<Pair<Point, Char>>()
        while (true) {
            if (!moves.add(currentPosition to currentDirection)) {
                return true
            }
            val newPosition = when (currentDirection) {
                '^' -> currentPosition + Point(0, -1)
                'v' -> currentPosition + Point(0, 1)
                '<' -> currentPosition + Point(-1, 0)
                '>' -> currentPosition + Point(1, 0)
                else -> throw Exception("wtf")
            }
            if (newPosition.y !in input.indices
                || newPosition.x !in input[newPosition.y].indices
            ) {
                return false
            }
            when (input[newPosition.y][newPosition.x]) {
                '#', 'O' -> {
                    currentDirection = when (currentDirection) {
                        '^' -> '>'
                        'v' -> '<'
                        '<' -> '^'
                        '>' -> 'v'
                        else -> throw Exception("wtf")
                    }
                }

                else -> currentPosition = newPosition
            }


        }
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
