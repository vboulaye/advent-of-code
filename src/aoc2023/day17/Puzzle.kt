package aoc2023.day17

import utils.Point
import utils.Vector
import utils.readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput() = this.map { ("" + it).toInt() }


    data class Grid(val map: List<List<Int>>) {
        val width = map[0].size
        val height = map.size


        //val array: List<List<Int>>
        inner class PointFinder(
            val mode: Boolean,
            val minIdx: Int ,
            val maxIdx: Int ,
        ) : FindRelated<Vector> {

            override fun findRelated(p: WorkPathElement<Vector>?): List<WorkPathElement<Vector>> {
                if (p == null) {
                    return emptyList()
                }

                if (p.element.to == p.element.from) {
                    return buildList {
                        (minIdx..maxIdx).forEach {
                            appendMove(p, it, 0)
                            appendMove(p, 0, it)
                        }
                    }
                }
                if (p.element.from.x - p.element.to.x == 0) {
                    return buildList {
                        (minIdx..maxIdx).forEach {
                            appendMove(p, it, 0)
                            appendMove(p, -it, 0)
                        }
                    }
                } else {
                    return buildList {
                        (minIdx..maxIdx).forEach {
                            appendMove(p, 0, it)
                            appendMove(p, 0, -it)
                        }
                    }
                }

            }

            private fun MutableList<WorkPathElement<Vector>>.appendMove(
                p: WorkPathElement<Vector>,
                x1: Int,
                y1: Int
            ) {
                val move = Point(p.element.to.x + x1, p.element.to.y + y1)
                if (move.x in 0 until width && move.y >= 0 && move.y < height) {
                    add(buildWorkPathElement(p, p.element.to, move))
                }
            }

            private fun buildWorkPathElement(
                before: WorkPathElement<Vector>,
                from: Point,
                to: Point
            ): WorkPathElement<Vector> {
                val workPathElement = WorkPathElement(Vector(from, to))
                if (from.x != to.x && from.y != to.y) {
                    throw Exception("not a move")
                }

//                workPathElement.distance= map[to.y][to.x]
                workPathElement.distance = (min(from.x, to.x)..max(from.x, to.x))
                    .sumOf { x ->
                        (min(from.y, to.y)..max(from.y, to.y)).sumOf { y ->
                            map[y][x]
                        }
                    }
                workPathElement.distance -= map[from.y][from.x]
//                workPathElement.distance +=  before.distance
                return workPathElement
            }
        }

        fun get(x: Int, y: Int): Int {
            return map[y][x]
        }


        fun print() {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    print(get(x, y))
                }
                println()
            }
        }

        fun findDistance(from: Point, to: Point, minIdx:Int, maxIdx:Int): Int {
            val pathFinder = PathFinder(PointFinder(false, minIdx, maxIdx ))
            return pathFinder.findDistance(
                Vector(from, from),
                { vector -> vector.to == to }
            )
        }


    }

    fun clean(input: List<String>): Grid {
        return Grid(input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() })
    }

    val part1ExpectedResult = 102
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.findDistance(
            Point(0, 0),
            Point(input.width - 1, input.height - 1),
            1,3
        )
    }

    val part2ExpectedResult = 94//too high 1354 1347
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.findDistance(
            Point(0, 0),
            Point(input.width - 1, input.height - 1),
            4,10
        )
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

//    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}

