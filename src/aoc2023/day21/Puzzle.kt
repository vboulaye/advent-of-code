package aoc2023.day21

import utils.Point
import utils.readInput
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


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

    data class SubGrid(
        val x: Int,
        val y: Int,
        val grid: List<Point>,
    )

    val cache = mutableMapOf<List<Point>, List<SubGrid>>()
    private fun nextSteps(plotsList: List<SubGrid>, input: List<List<Char>>): List<SubGrid> {

        val newSubGridList = mutableListOf<SubGrid>()

        plotsList.forEach { plots: SubGrid ->
            if (cache[plots.grid] != null) {
                val elements = cache[plots.grid]!!.map {
                    plots.copy(
                        x = plots.x + it.x,
                        y = plots.y + it.y,
                        grid = it.grid
                    )
                }
                newSubGridList.addAll(elements)
                return@forEach
            }
            val newSubPlotList = mutableListOf<SubGrid>()
            val newPlots = mutableListOf<Point>()
            for (plot in plots.grid) {
                (-1..1).forEach { dx ->
                    (-1..1).filter { dy ->
                        abs(dx) + abs(dy) == 1
                    }.forEach { dy ->
                        val newPlot = Point(plot.x + dx, plot.y + dy)
                        if (!(newPlot.y in input.indices)) {
                            val newY = if (newPlot.y < 0) input.lastIndex else 0
                            val newPlots = mutableListOf<Point>(newPlot.copy(y = newY))
                            val dy = if (newPlot.y < 0) -1 else +1
                            val newSubGrid = SubGrid(0, dy, newPlots)
                            if (!newSubPlotList.contains(newSubGrid)) {
                                newSubPlotList.add(newSubGrid)
                            }
                        } else if (!(newPlot.x in input[0].indices)
                        ) {
                            val newX = if (newPlot.x < 0) input[0].lastIndex else 0
                            val newPlots = mutableListOf<Point>(newPlot.copy(x = newX))
                            val dx = if (newPlot.x < 0) -1 else +1
                            val newSubGrid = SubGrid(dx, 0, newPlots)
                            if (!newSubPlotList.contains(newSubGrid)) {
                                newSubPlotList.add(newSubGrid)

                            }
                        } else
                            if (newPlot !in newPlots
                                && abs(dx) + abs(dy) == 1
                                && input[newPlot.y][newPlot.x] != '#'
                            ) {
                                newPlots.add(newPlot)
                            }
                    }
                }
            }
            newSubPlotList.add(SubGrid(0, 0, newPlots))
            cache[plots.grid] = newSubPlotList.toList()

            val elements = cache[plots.grid]!!.map {
                plots.copy(
                    x = plots.x + it.x,
                    y = plots.y + it.y,
                    grid = it.grid
                )
            }
            newSubGridList.addAll(elements)
        }
        val merged = newSubGridList.map { Point(it.x, it.y) to it.grid }
            .groupBy { it.first }
            .map { it.key to it.value.map { it: Pair<Point, List<Point>> -> it.second }.flatten() }
            .map { SubGrid(it.first.x, it.first.y, it.second.distinct()) }
        return merged

//        return newSubGridList
    }


    val part1ExpectedResult = 42L//16
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        var startPlots = listOf<Point>(findStart(input))
        val element = SubGrid(0, 0, startPlots)
        val startGrids = listOf(element)
        var plots = startGrids
        var step = 0
        while (step < 26501365) {
            step++
            plots = nextSteps(plots, input)
            println("" + step + ":" + plots.sumOf { it.grid.size.toLong() } + "-" + cache.size)
        }
        return plots.sumOf { it.grid.size.toLong() }
    }

    val part2ExpectedResult = 0L
    private fun findStart(input: List<List<Char>>): Point {
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == 'S') {
                    return Point(x, y)
                }
            }
        }
        throw Exception("No start found")
    }

    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val mappings: Map<Point, List<Point>> = buildMap {
            for (y in input.indices) {
                for (x in input[y].indices) {
                    val targets = (-1..1)
                        .flatMap { dx ->
                            (-1..1).filter { dy ->
                                abs(dx) + abs(dy) == 1
                            }.map { dy -> Point(x + dx, y + dy) }
                                .filter { newPlot -> newPlot.y !in input.indices || newPlot.x !in input[0].indices || (input[newPlot.y][newPlot.x] != '#') }
                        }
                    put(Point(x, y), targets)
                }
            }
        }

        val startPlots = listOf<Point>(findStart(input))
        var step = 0
        var plots = startPlots
        while (step < 26501365) {
            step++
            plots = plots.flatMap { point ->
                val translatedX = if (point.x >= 0) point.x % input[0].size else point.x % input[0].size + input[0].size
                val translatedY = if (point.y >= 0) point.y % input[0].size else point.y % input.size + input.size
                //println("" + point + " -> " + Point(translatedX, translatedY))
                mappings[Point(translatedX, translatedY)]!!
                    .map { mappedPoint ->
                        mappedPoint.copy(
                            point.x + (if (point.x > 0) mappedPoint.x - translatedX else mappedPoint.x - translatedX + input[0].size),
                            point.y + (if (point.y > 0) mappedPoint.y - translatedX else mappedPoint.y - translatedX + input.size),
                        )
                    }
            }.distinct()
            println("" + step + ":" + plots.size.toLong())
//        } + "-" + cache.size)
        }
        return plots.size.toLong()
    }
}


@OptIn(ExperimentalTime::class)
fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)

    fun runPart(part: String, expectedTestResult: Result, partEvaluator: (List<String>) -> Result) {
//        val testDuration =  measureTime {
//            println("test ${part}: $testResult == ${expectedTestResult}")
//            check(testResult == expectedTestResult) { "$testResult != ${expectedTestResult}" }
//        }
        val fullDuration = measureTime {
            val fullResult = partEvaluator(input)
            println("${part}: $fullResult")
        }
//        println("${part}: test took ${testDuration.inWholeMilliseconds}ms, full took ${fullDuration.inWholeMilliseconds}ms")
    }

    //runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
