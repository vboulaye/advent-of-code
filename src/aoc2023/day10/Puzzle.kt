package aoc2023.day10

import utils.readInput
import java.lang.IllegalStateException
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true


    enum class Direction {
        N, E, S, W
    }

    fun String.parseInput() = this.map {
        when (it) {
            '.' -> ""
            'S' -> "S"
            '|' -> "NS"
            '-' -> "EW"
            'L' -> "NE"
            'J' -> "NW"
            '7' -> "SW"
            'F' -> "SE"
            else -> throw Exception("Invalid input " + it)
        }
    }

    data class Point(val x: Int, val y: Int, val direction: String? = null) {
        fun move(direction: String) = when (direction) {
            "N" -> Point(x, y - 1, "N")
            "E" -> Point(x + 1, y, "E")
            "S" -> Point(x, y + 1, "S")
            "W" -> Point(x - 1, y, "W")

            else -> throw Exception("Invalid input " + direction)
        }

        override fun equals(other: Any?): Boolean {
            if (other is Point == false) {
                return false
            }
            return x == other.x && y == other.y
        }
    }

    data class Grid(val grid: List<List<String?>>) {
        fun get(point: Point): String {
            try {
                return grid.getOrNull(point.y)?.getOrNull(point.x) ?: ""
            } catch (e: Exception) {
                println("point: $point")
                return ""
            }
        }

        fun getStart(): Point {
            grid.forEachIndexed { y, line ->
                line.forEachIndexed { x, s -> if (s == "S") return Point(x, y) }
            }
            throw IllegalStateException("No start found")
        }

        data class Track(val point: Point, val count: Int)

        fun getEnd(point: Point, count: Int, tested: MutableSet<Point>): Int? {
            val toTest = mutableListOf(Track(point, count))
            while (!toTest.isEmpty()) {
                val lastTRack = toTest.removeLast()
                val lastPoint = lastTRack.point
//                println("track $lastTRack")

                val pipe = get(lastPoint)
                if (pipe == "") {
                    continue;
                }
                if (pipe == "S") {
//                    println("found " + lastPoint)
                    return lastTRack.count;
                }
                val previous = lastPoint.direction!!// if (track.size > 1) track[track.size - 2] else null
                val nextDirection = getNext(previous, pipe)
                if (nextDirection != null) {
                    val nextPoint = lastPoint.move(nextDirection)
                    if (tested.contains(nextPoint)) {
                        continue
                    }
//                if ( get(nextPoint)!="S" && track.contains(nextPoint)) {
//                    throw IllegalStateException("Loop detected")
//                }
                    toTest.add(Track(nextPoint, lastTRack.count + 1))
//                    val end = getEnd(nextPoint, count+1,tested)
//                    if (end != null) {
//                        return end
//                    }
                    continue
                }
            }

            return null
        }

        fun opposite(direction: String) = when (direction) {
            "N" -> "S"
            "E" -> "W"
            "S" -> "N"
            "W" -> "E"
            else -> throw Exception("Invalid input " + direction)
        }

        private fun getNext(previous: String, pipe: String): String? {
            val opposite = opposite(previous)
            if (pipe.contains(opposite)) {
                return pipe.replace(opposite ?: "", "")
            }
            return null;
        }

        fun getTrack(startAndDir: Point, mutableSetOf: MutableSet<Point>): MutableSet<Point> {
            var current: Point? = null
            while (current != startAndDir) {
                if (current == null) current = startAndDir
                val pipe = get(current)
                if (pipe == "S") {
                    return mutableSetOf;
                }
                val previous = current.direction!!// if (track.size > 1) track[track.size - 2] else null
                val nextDirection = getNext(previous, pipe)!!
                val nextPoint = current.move(nextDirection)
                mutableSetOf.add(nextPoint)
                current = nextPoint
            }
            return mutableSetOf
        }

        fun isInside(mutableSetOf: MutableSet<Point>, point: Point): Boolean {
            if(mutableSetOf.contains(point.copy(direction = "N"))
                    ||mutableSetOf.contains(point.copy(direction = "S"))
                    ||mutableSetOf.contains(point.copy(direction = "E"))
                    ||mutableSetOf.contains(point.copy(direction = "W"))) return false
            val t = (0 until point.x).count { x ->
                val pathPoint = mutableSetOf.find { it.x == x && it.y == point.y }
                if (pathPoint == null) {
                    return@count false
                }
                val pathType = get(pathPoint)
                pathType.contains("N")||pathType.contains("S")
            }
            val ty = (0 until point.y).count { y ->
                val pathPoint = mutableSetOf.find { it.x == point.x && it.y == y }
                if (pathPoint == null) {
                    return@count false
                }
                val pathType = get(pathPoint)
                pathType.contains("E")||pathType.contains("W")
            }
            return t % 2 == 1 && ty % 2 == 1
        }

    }

    fun clean(input: List<String>): Grid {
        val map = input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
        return Grid(map)
    }

    val part1ExpectedResult = 70
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val start = input.getStart()
        "NSWE".forEach { firstDirection ->
            println("start " + firstDirection)
            val first = start.move("" + firstDirection)
            if (first != null) {
                val track = mutableListOf(start)
                track.add(first)
                val size = input.getEnd(first, 1, mutableSetOf(first))
                if (size != null) {
                    return size / 2
                }
            }
        }

        return 0
    }

    val part2ExpectedResult = 8

    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val start = input.getStart()
        val dir = getDirection(start, input)
        val startAndDir = start.move("" + dir)
        val mutableSetOf = mutableSetOf(startAndDir)
        input.getTrack(startAndDir, mutableSetOf)
        var count = 0
        input.grid.forEachIndexed { y, line ->
            line.forEachIndexed { x, s ->
                val point = Point(x, y)
                if (input.isInside(mutableSetOf, point)) {
                    println(point)
                    count++
                }
            }
        }
        // 1721 too high
        // 397
        return count
    }

    private fun getDirection(start: Point, input: Grid): Char {
        "NSWE".forEach { firstDirection ->
            println("start " + firstDirection)
            val first = start.move("" + firstDirection)
            if (first != null) {
                val track = mutableListOf(start)
                track.add(first)
                val size = input.getEnd(first, 1, mutableSetOf(first))
                if (size != null) {
                    return firstDirection
                }
            }
        }
        return 'x'
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
