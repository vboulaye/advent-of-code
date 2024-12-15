package aoc2024.day15

import utils.*
import java.nio.file.Files.move
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }


    fun clean(input: List<String>): Pair<List<MutableList<Char>>, List<Char>> {
        val map = getMap(input)

        val path = input
            .filter { line ->
                !line.contains("#") && !line.isEmpty()
            }
            .flatMap { line -> line.toList().toMutableList() }

        return map to path
    }

    private fun getMap(input: List<String>): List<MutableList<Char>> {
        return input
            .filter { line ->
                line.contains("#")
            }
            .map { line -> line.toMutableList() }
    }

    val part1ExpectedResult = 10092
    fun part1(rawInput: List<String>): Result {
        val (map, path) = clean(rawInput)
        var robot = findPoint(map, '@')
        path.forEach { direction ->
            println(direction)
            robot = doMove(direction, robot to '@', map)
            // map.forEach { line -> println(line.joinToString("")) }
        }
        return map.browsePoints()
            .filter { it.second == 'O' }
            .sumOf { it.first.x + it.first.y * 100 }
    }

    private fun doMove(
        direction: Char,
        robot: Pair<Point, Char>,
        map: List<MutableList<Char>>
    ): Point {
        val newPoint = move(direction, robot.first)
        val value = map.getPoint(newPoint)
        val newPosition = when (value) {
            '#' -> {
                robot.first
            }

            'O' -> {
                val newPoint2 = tryMove(direction, newPoint to value, map)
//                val newPoint2 = doMove(direction, newPoint to value, map)
                if (newPoint2 == newPoint) {
                    robot.first
                } else {
                    doMove(direction, newPoint to value, map)
                    newPoint
                }
            }

            '[' -> {
                if (direction == '>') {
                    val other = newPoint + Point(1, 0)
                    val newPoint3 = tryMove(direction, other to ']', map)
                    if (newPoint3 == other) {
                        robot.first
                    } else {
                        doMove(direction, other to ']', map)
                        doMove(direction, newPoint to value, map)
                        newPoint
                    }
                } else {
                    val newPoint2 = tryMove(direction, newPoint to value, map)
                    val other = newPoint + Point(1, 0)
                    val newPoint3 = tryMove(direction, other to ']', map)
                    if (newPoint2 == newPoint || newPoint3 == other) {
                        robot.first
                    } else {
                        doMove(direction, newPoint to value, map)
                        doMove(direction, other to ']', map)
                        newPoint
                    }
                }
            }

            ']' -> {
                if (direction == '<') {
                    val other = newPoint + Point(-1, 0)
                    val newPoint3 = tryMove(direction, other to '[', map)
                    if (newPoint3 == other) {
                        robot.first
                    } else {
                        doMove(direction, other to '[', map)
                        doMove(direction, newPoint to value, map)
                        newPoint
                    }
                } else {
                    val newPoint2 = tryMove(direction, newPoint to value, map)
                    val other = newPoint + Point(-1, 0)
                    val newPoint3 = tryMove(direction, other to '[', map)
                    if (newPoint2 == newPoint || newPoint3 == other) {
                        robot.first
                    } else {
                        doMove(direction, newPoint to value, map)
                        doMove(direction, other to '[', map)
                        newPoint
                    }
                }
            }

            '.' -> {
                newPoint
            }

            else -> {
                throw IllegalArgumentException("Invalid value")
            }
        }
        if (newPosition == robot.first) {
            println("hit wall")
        } else {
            println("moving " + robot.second)
            map.setPoint(newPoint, robot.second)
            map.setPoint(robot.first, '.')
        }
        return newPosition
    }

    private fun tryMove(
        direction: Char,
        robot: Pair<Point, Char>,
        map: List<MutableList<Char>>
    ): Point {
        val newPoint = move(direction, robot.first)
        val value = map.getPoint(newPoint)
        val newPosition = when (value) {
            '#' -> {
                robot.first
            }

            'O' -> {
                val newPoint2 = tryMove(direction, newPoint to value, map)
                if (newPoint2 == newPoint) {
                    robot.first
                } else {
                    newPoint
                }
            }

            '[' -> {
                if (direction == '>') {
                    val other = newPoint + Point(1, 0)
                    val newPoint3 = tryMove(direction, other to ']', map)
                    if (newPoint3 == other) {
                        robot.first
                    } else {
                        newPoint
                    }
                } else {
                    val newPoint2 = tryMove(direction, newPoint to value, map)
                    val other = newPoint + Point(1, 0)
                    val newPoint3 = tryMove(direction, other to ']', map)
                    if (newPoint2 == newPoint || newPoint3 == other) {
                        robot.first
                    } else {
                        newPoint
                    }
                }
            }

            ']' -> {
                if (direction == '<') {
                    val other = newPoint + Point(-1, 0)
                    val newPoint3 = tryMove(direction, other to '[', map)
                    if (newPoint3 == other) {
                        robot.first
                    } else {
                        newPoint
                    }
                } else {
                    val newPoint2 = tryMove(direction, newPoint to value, map)
                    val other = newPoint + Point(-1, 0)
                    val newPoint3 = tryMove(direction, other to '[', map)
                    if (newPoint2 == newPoint || newPoint3 == other) {
                        robot.first
                    } else {
                        newPoint
                    }
                }
            }

            '.' -> {
                newPoint
            }

            else -> {
                throw IllegalArgumentException("Invalid value" + value)
            }
        }

        return newPosition
    }

    fun <E> List<MutableList<E>>.setPoint(it: Point, value: E): Unit {
        if (!this.containsPoint(it)) {
            throw IllegalStateException("point $it is out of bounds")
        }
        this[it.y][it.x] = value
    }

    private fun move(direction: Char, robot: Point) = when (direction) {
        '^' -> robot + Point(0, -1)
        'v' -> robot + Point(0, 1)
        '<' -> robot + Point(-1, 0)
        '>' -> robot + Point(1, 0)
        else -> throw IllegalArgumentException("Invalid direction")
    }

    fun findPoint(map: List<MutableList<Char>>, c: Char): Point {
        map.indices.forEach { y ->
            map[y].indices.forEach { x ->
                if (map[y][x] == c) {
                    return Point(x, y)
                }
            }
        }
        throw IllegalArgumentException("Point not found")
    }

    val part2ExpectedResult = 9021
    fun part2(rawInput: List<String>): Result {
        var (map, path) = clean(rawInput)
        map = map
            .map { line ->
                line.flatMap {
                    when (it) {
                        '@' -> listOf('@', '.')
                        '#' -> listOf(it, it)
                        '.' -> listOf(it, it)
                        'O' -> listOf('[', ']')
                        else -> throw IllegalArgumentException("Invalid value")
                    }
                }.toMutableList()
            }
        map.forEach { line -> println(line.joinToString("")) }
        var robot = findPoint(map, '@')
        path.forEach { direction ->
            println(direction)
            robot = doMove(direction, robot to '@', map)
            map.forEach { line -> println(line.joinToString("")) }
        }

        map.forEach { line -> println(line.joinToString("")) }

        return map.browsePoints()
            .filter { it.second == '[' }
            .sumOf { it.first.x + it.first.y * 100 }
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

    //runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
