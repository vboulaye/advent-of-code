package aoc2023.day18

import utils.Point
import utils.readInput
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    enum class Direction {
        U,
        D,
        L,
        R;

        fun coord(): Point {
            return when (this) {
                U -> Point(0, -1)
                D -> Point(0, 1)
                L -> Point(-1, 0)
                R -> Point(1, 0)
            }
        }

        fun opposite(): Direction {
            return when (this) {
                U -> D
                D -> U
                L -> R
                R -> L
            }
        }

    }

    data class Step(val direction: Direction, val distance: Int, val color: String)

    fun String.parseInput(): Step {
        val s = this.split(" ")
        return Step(Direction.valueOf(s[0]), s[1].toInt(), s[2])
    }

    fun clean(input: List<String>): List<Step> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 62L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return calculateContents(input)

//        var a = 0
//        var total = 0
//        val set = buildSet<Point> {
//            sortedBy.windowed(2).forEachIndexed { idx, steps ->
//                if (steps[0].y == steps[1].y ) {
//                    (steps[0].x..steps[1].x).forEach {
//                        add(Point(it, steps[0].y))
//                    }
//                }
//            }
//
////            (0..sortedBy.size - 2).forEach { idx ->
////                if (sortedBy[idx].y == sortedBy[idx + 1].y) {
////                    (sortedBy[idx].x..sortedBy[idx + 1].x).forEach {
////                        add(Point(it, sortedBy[idx].y))
////                    }
////                }
////            }
//        }
//
//        return set.size
//        (0..sortedBy.size - 2).forEach { idx ->
//            val area = if (sortedBy[idx].y == sortedBy[idx + 1].y) {
//                val interval = sortedBy[idx + 1].x - sortedBy[idx].x
//                val realinterval = if (interval > 1) {
//                    interval + 1
//                } else {
//                    if (idx + 2 < sortedBy.size && sortedBy[idx + 2].y == sortedBy[idx + 1].y) {
//                        interval
//                    } else {
//                        interval + 1
//                    }
//                }
//
//                a += realinterval
//                realinterval
//
//            } else {
//                println(sortedBy[idx].y.toString() + " y :" + a)
//                a = 0
//                0
//            }
//            total += area
//        }
//        return total
//
//        return sortedBy.windowed(2).sumOf { it ->
//            val area = if (it[0].y == it[1].y) {
//                val interval = it[1].x - it[0].x
//                val realinterval = if (interval > 1) {
//                    interval + 1
//                } else {
//                    interval
//                }
//                a+=realinterval
//                realinterval
//
//            } else {
//                println( it[0].toString()+ " y :"+ a)
//                a=0
//                0
//            }
//            area
//        }

//        return a
        // 74697 too high
        // 73111 too high
        //48795 but why ?
    }

    private fun calculateContents(input: List<Step>): Result {
        val edges2 = buildList<Point> {
            var cur = Point(0, 0)
            add(cur)
            input.forEach { step ->
                //                (0 until step.distance).forEach { _ ->
                cur += step.direction.coord() * step.distance
                if (!contains(cur))
                    add(cur)
                //                }
            }
            // addAll(input.map { it.direction.coord() }.scan(Point(0, 0)) { a, b -> a + b })
        }

        println("edges2.size" + edges2.size)

//        val edges = buildList<Point> {
//            var cur = Point(0, 0)
//            add(cur)
//            input.forEach { step ->
//                (0 until step.distance).forEach { _ ->
//                    cur = cur + step.direction.coord()
//                    if (!contains(cur))
//                        add(cur)
//
//                }
//            }
//            // addAll(input.map { it.direction.coord() }.scan(Point(0, 0)) { a, b -> a + b })
//        }
//        println("edges.size"+edges.size)


//        val sortedBy = edges.sortedBy { it.x }.sortedBy { it.y }
//        println(sortedBy)
//        val perimeter = sortedBy.size
        val perimeter = edges2.windowed(2)
            .fold(0) { acc, points ->
                acc + abs(points[0].x - points[1].x) + abs(points[0].y - points[1].y)
            } +abs(edges2[edges2.size - 1].x - edges2[0].x) + abs(edges2[edges2.size - 1].y - edges2[0].y)
        println(perimeter)
        //.forEach { println(it)}
        val sumOf = edges2.windowed(2).sumOf { (it[0].x * it[1].y - it[1].x * it[0].y).toLong() }
        var area=0L
        edges2.forEachIndexed() { idx, point ->
            if(idx>0) {
                edges2[idx - 1].let { prev ->
                    area += prev.x.toLong() * point.y.toLong() - point.x.toLong() * prev.y.toLong()
                }
            }
        }
        //16399845976 too low
        val result: Long = ((abs(area)
                + (edges2[edges2.size - 1].x * edges2[0].y - edges2[0].x * edges2[edges2.size - 1].y).toLong()
                + perimeter
                ) / 2) + 1
        return result
    }

    val part2ExpectedResult = 952408144115L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
            .map { step ->
                val colorString = step.color.removePrefix("(#").removeSuffix(")")
                val distance = colorString.substring(0, 5).toInt(16)
                val direciton = when (colorString[5]) {
                    '2' -> Direction.L
                    '0' -> Direction.R
                    '3' -> Direction.U
                    '1' -> Direction.D
                    else -> throw IllegalArgumentException("Unknown direction ${colorString[5]}")
                }
                Step(direciton, distance, "")
            }
        return calculateContents(input)
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
