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

    val part1ExpectedResult = 126384
    fun part1(rawInput: List<String>): Result {
        val codes = clean(rawInput)


        val dijkstra = initDijkstra<Point>()
        return codes
            .map { code ->

                var movesList: List<List<Char>> = getMoves(digiboard, 'A', code, dijkstra)

                (1..2).forEach { it ->
                    //  println("\n"+it.toString() + " = "+"\n"+movesList.joinToString { it.joinToString("") +"\n"}+"\n"+"\n"+"\n")
                    movesList = movesList.flatMap { move ->
                        getMoves(arrowboard, 'A', move, dijkstra)
                    }
                }

                val minBy = movesList.minBy { it.size }
                val codeInt = code.removeLast().joinToString("").toInt()
                minBy.size * codeInt
            }
            .sumOf { it }
    }

    private fun getMoves(
        board: List<List<Char>>,
        startingPoint: Char,
        code: List<Char>,
        dijkstra: Dijkstra<Point, Int>
    ): List<List<Char>> {
        val moves = codeMoveList(board, startingPoint, code, dijkstra)
        return explodesAlternatives(moves)
    }

    private fun explodesAlternatives(moves: List<List<List<Char>>>): List<List<Char>> {
        val currentAlternatives: List<List<Char>> = moves.get(0)
        if (moves.size == 1) {
            return currentAlternatives
        }
        val exploded: List<List<Char>> = explodesAlternatives(moves.removeFirst())
        val result: MutableList<List<Char>> = mutableListOf<List<Char>>()
        for (explod in exploded) {
            for (currentAlternative in currentAlternatives) {
                result.add(currentAlternative + explod)
            }
        }
        val distinct = result.distinct()
        val minSize = distinct.minOf { it.size }
        return distinct.filter { it.size == minSize }
    }

    private fun codeMoveList(
        board: List<List<Char>>,
        startingPoint: Char,
        code: List<Char>,
        dijkstra: Dijkstra<Point, Int>
    ): List<List<List<Char>>> {
        var start = board.findPoint(startingPoint)
        val moves = code.map { key ->
            val end = board.findPoint(key)
            val paths = dijkstra.computePaths(start, end) { p, m ->
                p.neighbours()
                    .filter { n -> board.containsPoint(n) && board.getPoint(n) != '#' }
                    .map { n -> Pair(n, 1) }
            }
            val pointsLists = paths.first
            val directionsList = pointsLists.map { pointsList ->
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
                directions + listOf('A')
            }

            start = end
            directionsList
        }
        return moves
    }

    val part2ExpectedResult = 0
    fun part2(rawInput: List<String>): Result {
        val codes = clean(rawInput)


        val dijkstra = initDijkstra<Point>()
        return codes
            .map { code ->

                var movesList: List<List<Char>> = getMoves(digiboard, 'A', code, dijkstra)

                (1..25).forEach { it ->
                    println("\n" + it.toString() + " = " + "\n moves " + movesList.size.toString() + " length " + movesList[0].size.toString() + "\n")
                    movesList = movesList.flatMap { move ->
                        getMoves(arrowboard, 'A', move, dijkstra)
                    }
                    val distinct = movesList.distinct()
                    val minSize = distinct.minOf { it.size }
                    movesList= distinct.filter { it.size == minSize }
                }

                val minBy = movesList.minBy { it.size }
                val codeInt = code.removeLast().joinToString("").toInt()
                minBy.size * codeInt
            }
            .sumOf { it }
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
