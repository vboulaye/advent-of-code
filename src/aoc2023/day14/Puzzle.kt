package aoc2023.day14

import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int

val cache = mutableMapOf<Pair<Point, Puzzle.Grid>, Puzzle.Grid>()


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput() = this

    data class Grid(val map: List<String>) {
        override fun toString(): String {
            return this.map.map { it + "\n" }.joinToString("")
        }


        fun move(direction: Point): Grid {
            cache.get(Pair(direction, this))?.let {
                // println("hit cache")
                return it
            }
            val grid = map.toMutableList()

            val verticalRange = if (direction.y > 0) (0 until map.size).reversed() else 0 until map.size
            verticalRange.forEach { y ->
                val horizontalRange = if (direction.x > 0) (0 until map[y].length).reversed() else 0 until map[y].length
                horizontalRange.forEach { x ->
                    val char = grid[y][x]
                    if (char == 'O') {
                        var targetPoint: Point = Point(x, y)
                        while (true) {
                            val targetPoint2 = Point(targetPoint.x + direction.x, targetPoint.y + direction.y)
                            if (targetPoint2.x < 0 || targetPoint2.x >= map[y].length) {
                                break;
                            }
                            if (targetPoint2.y < 0 || targetPoint2.y >= map.size) {
                                break;
                            }
                            val targetChar = grid[targetPoint2.y][targetPoint2.x]
                            if (targetChar == '.') {
                                targetPoint = targetPoint2
                            } else {
                                break;
                            }
                        }
                        if (targetPoint != Point(x, y)) {
                            grid[targetPoint.y] = grid[targetPoint.y].toCharArray()
                                .also { it[targetPoint.x] = 'O' }.joinToString("")
                            grid[y] = grid[y].toCharArray()
                                .also { it[x] = '.' }.joinToString("")
                        }


                    }
                }
            }
            val newGrid = Grid(grid)
            cache.put(Pair(direction, this), newGrid)
            // println(direction.toString() + " => "+newGrid)
            return newGrid
        }

        fun countTop(): Result {
            val counts = mutableListOf<Int>()
            (0 until map[0].length).forEach { counts.add(0) }
            (0 until map.size).forEach { y ->
                (0 until map[y].length).forEach { x ->
                    val char = map[y][x]
                    if (char == 'O') {
                        counts[x] = counts[x] + (map.size - y)
                    }
                }
            }
            return counts.sum()
        }
    }


    fun clean(input: List<String>): Grid {
        val map = input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
        return Grid(map)
    }

    val part1ExpectedResult = 136
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.move(Point(0, -1)).countTop()
    }

    val part2ExpectedResult = 64
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val passedValues = mutableListOf<Grid>()
        val values = mutableMapOf<Grid, Int>()

        var inpuit2 = input
        var start = 0
        var length = 0
        run loop@{(0 until 1000000000).forEach {
            if (it % 1000000 == 0)
                println(it)

            inpuit2 = inpuit2
                .move(Point(0, -1))
                .move(Point(-1, 0))
                .move(Point(0, 1))
                .move(Point(1, 0))

            println("" + it + ":" + inpuit2.hashCode()+"values"+values.size)
            if (start == 0) {
                values.get(inpuit2)?.let { foundId ->
                    start = foundId
                    length = it - foundId
                }
            }
            if (start == 0) {
                passedValues.add(inpuit2)
                values.put(inpuit2, it)
            } else {
                return@loop;
//                val index = (it - start) % length
//                println("index: " + index)
//                val detuctedGrid =  passedValues[start + index]
//                if (detuctedGrid!= inpuit2) {
//                    println("not equal")
//                }
            }

        }
        }
        val index = (1000000000-1 - start) % length
        println("index: " + index)
        val detuctedGrid =  passedValues[start + index]
        return detuctedGrid.countTop()
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
