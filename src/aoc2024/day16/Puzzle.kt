package aoc2024.day16

import aoc2021.day15.*
import utils.*
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
        val pathFinder = PathFinder(Finder(input))
        val remainingNodes = pathFinder.computePath(start, end)
        val workPathElement = remainingNodes.getWorkPathElement(end)
        //too high 225808
        //         257919
//        val findPath = pathFinder.findPath(start, end).reversed()
//        findPath.zipWithNext { a, b ->
//            println("${a.element} -> ${b.element} ${b.distance}")
//            if(b.element==a.predecessor) throw Exception()
//        }
//        val sum = findPath.zipWithNext { a, b ->
//            if (b.distance - a.distance > 1) {
//                println("${a.element} -> ${b.element} ${b.distance}")
//                1
//            } else {
//                0
//            }
//        }.sum()

//        val map = findPath.map { it.element }
//        val x=map.count()
//        val xzx=map.distinct().count()


        return workPathElement.distance
    }

    class Finder(val grid: List<List<Char>>) : FindRelated<Point, Pair<Point, Point>> {

        override fun findRelated(p: WorkPathElement<Point>?): List<WorkPathElement<Point>> {
            if (p == null) {
                return emptyList()
            }

            val neighbours: List<WorkPathElement<Point>> =
                p.element.neighbours()
                    .filter { point ->
                        this.grid.containsPoint(point)
                                && this.grid.getPoint(point) != '#'
                                && p.predecessor != point
                    }
                    .mapNotNull { point ->
                        val workPathElement = WorkPathElement(point)
                        workPathElement.distance = 1 + 1000 * rotation(p, point)
                        if (workPathElement.distance > 2000) {
                            null
                        } else {
                            workPathElement
                        }
                    }


            return neighbours
        }

        private fun rotation(workPathElement: WorkPathElement<Point>, point: Point): Int {

            val vector = point - workPathElement.element
            val previousVector: Point
            val predecessor = workPathElement.predecessor
            if (predecessor == null) {
                previousVector = Point(1, 0)
            } else {
                previousVector = workPathElement.element - predecessor
            }

            return when {
                vector == previousVector -> 0
                vector.y == 0 && previousVector.x == 0 -> 1
                vector.x == 0 && previousVector.y == 0 -> 1
                else -> 2
            }
        }
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
