package aoc2024.day21

import utils.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {

    val digiboard = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf('#', '0', 'A'),
    )

    val arrowboard = listOf(
        listOf('#', '^', 'A'),
        listOf('<', 'v', '>'),
    )

    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): List<List<Char>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.toList() }
    }

    val part1ExpectedResult = 0
    fun part1(rawInput: List<String>): Result {
        val codes = clean(rawInput)


        val dijkstra = initDijkstra<Point>()
        return codes
            .map { code ->

                val movesList = getMoves(digiboard, 'A', code, dijkstra)
                val movesList2 = getMoves(arrowboard, 'A', movesList, dijkstra)
                val movesList3 = getMoves(arrowboard, 'A', movesList2, dijkstra)
                val codeInt = code.removeLast().joinToString("").toInt()
                movesList3.size * codeInt
            }
            .sumOf { it }
    }

    private fun getMoves(
        board: List<List<Char>>,
        startingPoint: Char,
        code: List<Char>,
        dijkstra: Dijkstra<Point, Int>
    ): List<Char> {
        val moves = lists(board, startingPoint, code, dijkstra)
        val movesList = moves.flatMap { it }
        return movesList
    }

    private fun lists(
        board: List<List<Char>>,
        startingPoint: Char,
        code: List<Char>,
        dijkstra: Dijkstra<Point, Int>
    ): List<List<Char>> {
        var start = board.findPoint(startingPoint)
        val moves = code.map { key ->
            val end = board.findPoint(key)
            val path = dijkstra.computePath(start, end) { p, m ->
                p.neighbours()
                    .filter { n -> board.containsPoint(n) && board.getPoint(n) != '#' }
                    .map { n -> Pair(n, 1) }
            }
            val pointsList = path.first
            val directions = pointsList.removeFirst().mapIndexed { i, p ->
                val vector = p - pointsList[i]!!
                when (vector) {
                    Point(0, -1) -> '^'
                    Point(0, 1) -> 'v'
                    Point(-1, 0) -> '<'
                    Point(1, 0) -> '>'
                    else -> throw IllegalArgumentException("Invalid direction")
                }
            }
            start = end
            directions + listOf('A')
        }
        return moves
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
