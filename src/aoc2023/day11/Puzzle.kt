package aoc2023.day11

import aoc2021.day15.PathFinder
import aoc2021.day15.PointFinder
import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {

    data class Universe(val map: List<List<Char>>) {

        val expandedRow = buildExpandedRow()
        val expandedCol = buildExpandedCol()


        val galaxies = buildList<Point> {
            map.forEachIndexed { y, row ->
                row.forEachIndexed { x, col ->
                    if (col == '#') {
                        add(Point(x, y))
                    }
                }
            }
        }


        override fun toString(): String {
            return map.map { row -> row.joinToString("") }.joinToString("\n")
        }

        fun expand(): Universe {
            val flatMapIndexed = mutableListOf<List<Char>>()
            val expandedRow = buildExpandedRow()
            val expandedCol = buildExpandedCol()

            map.forEachIndexed { y, row ->
                if (expandedRow.contains(y)) {
                    flatMapIndexed.add(row)
                }
                flatMapIndexed.add(row)
            }
            val expRow = flatMapIndexed
                .map { row ->
                    val foldRight = expandedCol.foldRight(row) { x, acc ->
                        acc.subList(0, x) + listOf('.') + acc.subList(x, acc.size)
                    }
                    foldRight
                }

            return Universe(expRow)
        }

        private fun buildExpandedCol(): MutableList<Int> {
            val expandedCol = mutableListOf<Int>()
            map.forEachIndexed { x, row ->
                var found = false
                for (y in 0 until row.size) {
                    if (map[y][x] != '.') {
                        found = true
                        break
                    }
                }
                if (!found) {
                    expandedCol.add(x)
                }

            }
            return expandedCol
        }

        private fun buildExpandedRow(): MutableList<Int> {
            val expandedRow = mutableListOf<Int>()
            map.forEachIndexed { y, row ->
                if (row.all { it == '.' }) {
                    expandedRow.add(y)
                }
            }
            return expandedRow
        }

        fun findDistance(from: Point, to: Point): Int {
            val pathFinder = PathFinder(PointFinder())
            return pathFinder.findDistance(from, to)
        }

        fun computeGalaxiesDistances(exp: Int): Long {
            return galaxies.flatMapIndexed { index, galaxy ->
                (index + 1 until galaxies.size).map { otherIndex ->
                    val otherGalaxy = galaxies[otherIndex]

                    val distance = findDistancePart2(galaxy, otherGalaxy, exp)
                    println("$index -> $otherIndex = $distance")
                    distance
                }
            }.sum()
        }

        private fun findDistance2(galaxy: Point, otherGalaxy: Point): Int {
            return Math.abs(otherGalaxy.x - galaxy.x) + Math.abs(otherGalaxy.y - galaxy.y)
        }

        private fun findDistancePart2(galaxy: Point, otherGalaxy: Point, exp: Int): Long {
            val baseDistance = Math.abs(otherGalaxy.x - galaxy.x) + Math.abs(otherGalaxy.y - galaxy.y)
            return baseDistance.toLong() + expandedCol.count {
                Math.min(galaxy.x, otherGalaxy.x) < it
                        && it < Math.max(galaxy.x, otherGalaxy.x)
            } * (exp) + expandedRow.count {
                Math.min(galaxy.y, otherGalaxy.y) < it
                        && it < Math.max(galaxy.y, otherGalaxy.y)
            } * (exp)

        }

    }


    fun String.acceptInput() = true

    fun String.parseInput() = this

    fun clean(input: List<String>): Universe {
        val map = input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput().map { it } }
        return Universe(map)
    }

    val part1ExpectedResult = 374L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        println("input: $input")
//        val expand = input.expand()
//        println("expand: $expand")
//        val computeGalaxiesDistances = input.computeGalaxiesDistances(0)
//        println("kkkkkkkkkkkkkkkk"+computeGalaxiesDistances)
        return input.computeGalaxiesDistances(1)
        // 9742154
    }

    val part2ExpectedResult = 82000210L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        println("input: $input")
//        val expand = input.expand()
//        println("expand: $expand")
        return input.computeGalaxiesDistances(1000000 - 1)
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
