package aoc2024.day12

import utils.Point
import utils.browsePoints
import utils.getPoint
import utils.readInput
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Char> {
        return this.toList()
    }

    fun clean(input: List<String>): List<List<Char>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 1930
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val seen = mutableSetOf<Point>()
        val areaPerimeter = mutableMapOf<Point, Pair<Int, Int>>()
        val aretoToPerimeter = input.browsePoints()
            .filter { (p, v) -> !(p in seen) }
            .mapNotNull { (p, v) ->
                if (!(p in seen)) {
                    println(p)
                    val seen2 = mutableSetOf<Point>()
                    val areaPoints = computeAp(p, v, input, seen)
                    val points = areaPoints
                    println(v + "=" + points)
                    points
                } else {
                    null
                }
            }.sumOf { areaPoints ->
                val perimeter = areaPoints.sumOf { p ->
                    p.neighbours()
                        .count { it !in areaPoints }
                }
                println(areaPoints.size.toString() + '*' + perimeter)
                areaPoints.size * perimeter
            }

        return aretoToPerimeter
    }

    private fun computeAp(
        p: Point,
        v: Char,
        input: List<List<Char>>,
        seen2: MutableSet<Point>,
    ): List<Point> {
        seen2.add(p)
        return listOf(p) + p.neighbours()
            .filter { input.getPoint(it, '~') == v }
            .flatMap {
                if (!(it in seen2)) {

                    val computeAp = computeAp(it, v, input, seen2)
                    computeAp
                } else {
                    emptyList()
                }

            }
    }

    val part2ExpectedResult = 1206
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val seen = mutableSetOf<Point>()
        val areaPerimeter = mutableMapOf<Point, Pair<Int, Int>>()
        val areas = input.browsePoints()
            .filter { (p, v) -> !(p in seen) }
            .mapNotNull { (p, v) ->
                if (!(p in seen)) {
                    println(p)
                    val seen2 = mutableSetOf<Point>()
                    val areaPoints = computeAp(p, v, input, seen)
                    val points = areaPoints
                    println(v + "=" + points)
                    points
                } else {
                    null
                }
            }
        val aretoToPerimeter = areas.sumOf { areaPoints ->
//                val distinct = areaPoints
//                    .flatMap { p ->
//                        p.neighbours()
//                            .filter { it !in areaPoints }
//                            .map {
//                                val direction = it - p
//                                val serializable = when {
//                                    direction == Point(1, 0) -> p.x to p.x + 1 // '>'
//                                    direction == Point(-1, 0) -> p.x - 1 to p.x//'<'
//                                    direction == Point(0, 1) -> 1000000 * (p.y) to 1000000 * (p.y+ 1) //'v'
//                                    direction == Point(0, -1) -> 1000000 * (p.y - 1) to 1000000 * (p.y) // '^'
//                                    else -> Error("unexpected direction $direction")
//                                }
//                                serializable
//                            }
//                    }
//                    .distinct()
                val distinct = areaPoints
                    .flatMap { p ->
                        p.neighbours()
                            .filter { it !in areaPoints }
                            .map {
//                                val direction = it - p
//                                val serializable = when {
//                                    direction == Point(1, 0) -> p.x to p.x + 1 // '>'
//                                    direction == Point(-1, 0) -> p.x - 1 to p.x//'<'
//                                    direction == Point(0, 1) -> 1000000 * (p.y) to 1000000 * (p.y+ 1) //'v'
//                                    direction == Point(0, -1) -> 1000000 * (p.y - 1) to 1000000 * (p.y) // '^'
//                                    else -> Error("unexpected direction $direction")
//                                }
                                p to it
                            }
                    }
                    .distinct()
                    .toMutableList()
                var i = 0
                while (i < distinct.size) {
                    val (from, to) = distinct[i]
                    val seen1 = mutableSetOf<Pair<Point, Point>>()
                    val other = getStraightLine(distinct[i], distinct, seen1, areas)
                        .toMutableList()
//                    other.remove(distinct[i])
//                    distinct.removeAll(other)
                    seen1.remove(distinct[i])
                    distinct.removeAll(seen1)
                    i++
                }
                println(areaPoints + "=>" + distinct)
                val perimeter = distinct
                    .count()

                println( input.getPoint( areaPoints[0]).toString() +" =>" + areaPoints.size.toString() + '*' + perimeter)
                areaPoints.size * perimeter
            }
        return aretoToPerimeter
    }

    private fun getStraightLine(
        pair: Pair<Point, Point>,
        distinct: MutableList<Pair<Point, Point>>,
        seen: MutableSet<Pair<Point, Point>>,
        areas: List<List<Point>>
    ): List<Pair<Point, Point>> {
        val direction = pair.second - pair.first
        val firstArea = areas.find { it.contains(pair.first) }
        val secondArea = areas.find { it.contains(pair.second) }

        val serializable = when {
            direction.y == 0 -> distinct
                .filter {
                    val direction2 = it.second - it.first
                    val b = !(it in seen) && direction2.y == 0
                            && (abs(it.second.y - pair.second.y)==1 )
                            && direction==direction2
                           // && (isInSimilarAreas(it, firstArea, secondArea, areas))
                            && (
                            (it.second.x == pair.second.x && it.first.x == pair.first.x)
                                    || (it.second.x == pair.first.x && it.first.x == pair.second.x)
                            )
                    b
                }
                .flatMap {
                    seen.add(it)
                    listOf(it) + getStraightLine(it, distinct, seen, areas)
                }

            direction.x == 0 -> distinct
                .filter {
                    val direction2 = it.second - it.first
                    !(it in seen) && direction2.x == 0
                            && direction==direction2
                            //&& (isInSimilarAreas(it, firstArea, secondArea, areas))
                            && (abs(it.second.x - pair.second.x)==1 )
                            && (
                            (it.second.y == pair.second.y && it.first.y == pair.first.y)
                                    || (it.second.y == pair.first.y && it.first.y == pair.second.y)
                            )
                }
                .flatMap {
                    seen.add(it)
                    listOf(it) + getStraightLine(it, distinct, seen, areas)
                }

            else -> throw Error("unexpected direction $direction")
        }
        return serializable
    }

    private fun isInSimilarAreas(
        p: Pair<Point, Point>,
        firstArea: List<Point>?,
        secondArea: List<Point>?,
        areas: List<List<Point>>
    ): Boolean {
        val firstArea2 = areas.find { it.contains(p.first) }
        val secondArea2 = areas.find { it.contains(p.second) }

      return   (firstArea==firstArea2 && secondArea==secondArea2)
                || (firstArea==secondArea2 && secondArea==firstArea2)

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
