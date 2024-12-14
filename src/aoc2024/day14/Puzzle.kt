package aoc2024.day14

import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): Pair<Point, Point> {
        //p=9,5 v=-3,-3
        return Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)").find(this)!!.destructured
            .let { (x, y, X, Y) -> Point(x.toInt(), y.toInt()) to Point(X.toInt(), Y.toInt()) }
    }

    fun clean(input: List<String>): List<Pair<Point, Point>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 12L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val dims = when {
            input.size < 20 -> Point(11, 7)
            else -> Point(101, 103)
        }
        var pos = input
//        var i = 0
//        while (i < 100) {
//            i++
//            pos=pos.map { (p, v) -> (p+v).modulo(dims) to v }
//        }
        pos = pos.map { (p, v) ->
            val point = (p) + v * 100
            val modulo = point.modulo(dims)
            modulo to v
        }
        val middle = Point(dims.x / 2, dims.y / 2)
        (0 until dims.y).forEach { y ->
            (0 until dims.x).forEach { x ->
                val p = Point(x, y)
                val count = pos.filter { it.first == p }.count()
                print(if (count == 0) "." else count)
            }
            println()
        }
        val mapNotNull = pos.mapNotNull { (p, v) ->
            when {
                p.x < middle.x && p.y < middle.y -> "NW" to p
                p.x > middle.x && p.y < middle.y -> "NE" to p
                p.x > middle.x && p.y > middle.y -> "SE" to p
                p.x < middle.x && p.y > middle.y -> "SW" to p
                else -> null
            }
        }.groupBy { it.first }
        val quadrant = mapNotNull

            .values
            .map { it.size }
        return quadrant.fold(1L, { acc, countOfRobots -> acc * countOfRobots.toLong() })
    }


    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val dims = when {
            input.size < 20 -> Point(11, 7)
            else -> Point(101, 103)
        }
        var pos = input
        var i = 0
        while (i < 10000) {
            i++

            pos = pos.map { (p, v) -> (p + v).modulo(dims) to v }

            val grid: List<String> = (0 until dims.y).map { y ->
                (0 until dims.x).map { x ->
                    val p = Point(x, y)
                    val count = pos.filter { it.first == p }.count()
                    if (count == 0) "." else "#"
                }.joinToString("")
            }
            val hasline = grid.any { line ->

                line.contains("##########")
            }
            if (hasline) {

                println()
                println()
                println(i)
                println()
                grid.forEach { println(it) }

            }
        }
//        pos=pos.map { (p, v) -> val point = (p) + v * 100
//            val modulo = point.modulo(dims)
//            modulo to v }
//        val middle = Point(dims.x/2, dims.y/2)


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
//        val testDuration = measureTime {
//            val testResult = partEvaluator(testInput)
//            println("test ${part}: $testResult == ${expectedTestResult}")
//            check(testResult == expectedTestResult) { "$testResult != ${expectedTestResult}" }
//        }
        val fullDuration = measureTime {
            val fullResult = partEvaluator(input)
            println("${part}: $fullResult")
        }
//        println("${part}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
    }

//    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
