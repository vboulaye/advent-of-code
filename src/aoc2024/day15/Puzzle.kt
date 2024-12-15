package aoc2024.day15

import utils.*
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
        var robot = map.findPoint('@')
        path.forEach { direction ->
            println(direction)
            robot = doMove(direction, robot to '@', map, false)
        }
        return map.browsePoints()
            .filter { it.second == 'O' }
            .sumOf { it.first.x + it.first.y * 100 }
    }

    private fun doMove(
        direction: Char,
        robot: Pair<Point, Char>,
        map: List<MutableList<Char>>,
        trialMode:Boolean
    ): Point {
        val newPoint = robot.first.move(direction)
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
                    if (!trialMode) {
                        doMove(direction, newPoint to value, map, trialMode)
                    }
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
                        if (!trialMode) {
                            doMove(direction, other to ']', map, trialMode)
                            doMove(direction, newPoint to value, map, trialMode)
                        }
                        newPoint
                    }
                } else {
                    val newPoint2 = tryMove(direction, newPoint to value, map)
                    val other = newPoint + Point(1, 0)
                    val newPoint3 = tryMove(direction, other to ']', map)
                    if (newPoint2 == newPoint || newPoint3 == other) {
                        robot.first
                    } else {
                        if (!trialMode) {
                            doMove(direction, newPoint to value, map,trialMode)
                            doMove(direction, other to ']', map,trialMode)
                        }
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
                        if (!trialMode) {
                            doMove(direction, other to '[', map,trialMode)
                            doMove(direction, newPoint to value, map,trialMode)
                        }
                        newPoint
                    }
                } else {
                    val newPoint2 = tryMove(direction, newPoint to value, map)
                    val other = newPoint + Point(-1, 0)
                    val newPoint3 = tryMove(direction, other to '[', map)
                    if (newPoint2 == newPoint || newPoint3 == other) {
                        robot.first
                    } else {
                        if (!trialMode) {
                            doMove(direction, newPoint to value, map,trialMode)
                            doMove(direction, other to '[', map,trialMode)
                        }
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
        if (!trialMode) {
            if (newPosition == robot.first) {
                println("hit wall")
            } else {
                println("moving " + robot.second)
                map.setPoint(newPoint, robot.second)
                map.setPoint(robot.first, '.')
            }
        }

        return newPosition
    }

    private fun tryMove(
        direction: Char,
        robot: Pair<Point, Char>,
        map: List<MutableList<Char>>
    ): Point {
        return doMove(direction, robot, map, true)
    }

    private fun tryMove2(
        direction: Char,
        robot: Pair<Point, Char>,
        map: List<MutableList<Char>>
    ): Point {
        val newPoint = robot.first.move(direction)
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
        //    map.forEach { line -> println(line.joinToString("")) }
        var robot = map.findPoint('@')
        path.forEach { direction ->
            println(direction)
            robot = doMove(direction, robot to '@', map,false)
            //      map.forEach { line -> println(line.joinToString("")) }
        }

        //   map.forEach { line -> println(line.joinToString("")) }

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
