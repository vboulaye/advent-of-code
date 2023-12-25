package aoc2023.day16

import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput() = this

    enum class Mirror {
        EW,
        NS,
        SWNE,
        NWSE,
        TRANSPARENT

    }

    fun clean(input: List<String>): List<List<Mirror>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line ->
                line.parseInput().map {
                    when (it) {
                        '.' -> Mirror.TRANSPARENT
                        '-' -> Mirror.EW
                        '|' -> Mirror.NS
                        '/' -> Mirror.SWNE
                        '\\' -> Mirror.NWSE
                        else -> throw Exception("Unknown input $it")
                    }
                }
            }
    }

    val part1ExpectedResult = 46

    enum class Direction(val dx: Int, val dy: Int) {
        NORTH(0, -1),
        EAST(1, 0),
        WEST(-1, 0),
        SOUTH(0, 1);

        fun nextDirection(mirror: Mirror): List<Direction> = when (mirror) {
            Mirror.EW -> when (this) {
                NORTH -> listOf(EAST, WEST)
                EAST -> listOf(this)
                WEST -> listOf(this)
                SOUTH -> listOf(EAST, WEST)
            }

            Mirror.NS -> when (this) {
                NORTH -> listOf(this)
                EAST -> listOf(NORTH, SOUTH)
                WEST -> listOf(NORTH, SOUTH)
                SOUTH -> listOf(this)
            }

            Mirror.SWNE -> when (this) {
                NORTH -> listOf(EAST)
                EAST -> listOf(NORTH)
                WEST -> listOf(SOUTH)
                SOUTH -> listOf(WEST)
            }

            Mirror.NWSE -> when (this) {
                NORTH -> listOf(WEST)
                EAST -> listOf(SOUTH)
                WEST -> listOf(NORTH)
                SOUTH -> listOf(EAST)
            }

            Mirror.TRANSPARENT -> listOf(this)
        }
    }

    data class Ray(val direction: Direction, val point: Point) {

    }

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val first = Ray(Direction.EAST, Point(0, 0))
        return computeEnergized(first, input)
    }

    private fun computeEnergized(
        first: Ray,
        input: List<List<Mirror>>
    ): Int {
        val toProcess = mutableListOf(first)
        val energized = mutableSetOf(first)
        while (toProcess.isNotEmpty()) {
            val current = toProcess.removeLast()

            current.direction.nextDirection(input[current.point.y][current.point.x])
                .forEach {
                    val nextPoint = Point(current.point.x + it.dx, current.point.y + it.dy)
                    if (input[0].indices.contains(nextPoint.x) && input.indices.contains(nextPoint.y)) {
                        val nextRay = Ray(it, nextPoint)
                        if (energized.add(nextRay)) {
                            toProcess.add(nextRay)
                        }
                    }
                }
        }
        return energized.map { it.point }.distinct().size
    }

    val part2ExpectedResult = 51
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val possibleStarts = buildList<Ray> {
            (0 until input.size).forEach { y ->
                add(Ray(Direction.EAST, Point(0, y)))
                add(Ray(Direction.WEST, Point(input[0].size - 1, y)))
            }
            (0 until input[0].size).forEach { x ->
                add(Ray(Direction.SOUTH, Point(x, 0)))
                add(Ray(Direction.NORTH, Point(x, input.size - 1)))
            }
        }

        return possibleStarts.map {
            val computeEnergized = computeEnergized(it, input)
            println("$it: $computeEnergized")
            computeEnergized
        }.maxOrNull() ?: 0
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
