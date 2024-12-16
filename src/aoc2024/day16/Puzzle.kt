package aoc2024.day16

import aoc2021.day15.*
import utils.*
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): List<List<Char>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.toList() }
    }

    val part1ExpectedResult = 11048
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val start = input.findPoint('S')
        val end = input.findPoint('E')

        val map: Map<Pair<Point, Point>, Int> = dijkstra(input, start to Point(1, 0))

        return map.get(end to Point(0, -1))!!
    }

    fun dijkstra(
        grid: List<List<Char>>,
        start: Pair<Point, Point>
    ): Map<Pair<Point, Point>, Int> {
        val distances = mutableMapOf<Pair<Point, Point>, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue =
            PriorityQueue<Pair<Pair<Point, Point>, Int>>(compareBy { it.second }).apply { add(start to 0) }

        distances[start] = 0

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()

            getNeighbours(node, grid).forEach { (adjacent, weight) ->
                val totalDist = currentDist + weight
                if (totalDist < distances.getValue(adjacent)) {
                    distances[adjacent] = totalDist
                    priorityQueue.add(adjacent to totalDist)
                }
            }
        }
        return distances
    }

    private fun getNeighbours(node: Pair<Point, Point>, grid: List<List<Char>>): List<Pair<Pair<Point, Point>, Int>> {
        val sourcePoint = node.first
        val sourceDirection = node.second
        return sourcePoint.neighbours()
            .filter { grid.containsPoint(it) && grid.getPoint(it) != '#' }
            .mapNotNull { point ->
                val newDirection = point - sourcePoint
                if (newDirection == sourceDirection) {
                    point to newDirection to 1
                } else if (newDirection == sourceDirection * -1) {
                    null
                } else {
                    point to newDirection to 1001
                }
            }

    }


    val part2ExpectedResult = 64
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val start = input.findPoint('S')
        val end = input.findPoint('E')

        val map: Map<Pair<Point, Point>, Int> = dijkstra(input, start to Point(1, 0))

        return map.get(end to Point(0, -1))!!

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
            //check(testResult == expectedTestResult) { "$testResult != ${expectedTestResult}" }
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
