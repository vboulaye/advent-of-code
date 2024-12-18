package aoc2024.day18

import utils.*
import java.util.*
import kotlin.collections.removeFirst
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = String


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): List<Point> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line ->
                val coords = line.split(",").map { it.toInt() }
                Point(coords[0], coords[1])
            }
    }

    val part1ExpectedResult = "22"
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val w = input.maxOf { it.x }
        val h = input.maxOf { it.y }

        val grid = (0..h).map { y ->
            (0..w).map { x ->
                '.'
            }.toMutableList()
        }

        val iteraitons = if (input.size < 100) 12 else 1024
        (0 until iteraitons).forEach { i ->
            input[i].let { p ->
                grid[p.y][p.x] = '#'
            }
        }
        val start = Point(0, 0)
        val end = Point(w, h)
        val dijkstra = dijkstra(grid, start)

        return dijkstra.get(end)!!.toString()
    }

    //140
    fun getNeighbours(node: Point, grid: List<MutableList<Char>>): List<Pair<Point, Int>> {
        return node.neighbours().filter { grid.containsPoint(it) && grid.getPoint(it) != '#' }
            .map { it to 1 }
    }

    fun dijkstra(grid: List<MutableList<Char>>, start: Point): Map<Point, Int> {
        val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
        val visited = mutableSetOf<Pair<Point, Int>>()

        priorityQueue.add(start to 0)
        distances[start] = 0
//        graph: Map<Point, List<Pair<Point, Int>>>,
        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (visited.add(node to currentDist)) {
//                graph[node]?
                getNeighbours(node, grid).forEach { (adjacent, weight) ->
                    val totalDist = currentDist + weight
                    if (totalDist < distances.getValue(adjacent)) {
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)
                    }
                }
            }
        }
        return distances
    }

    val part2ExpectedResult = "6,1"
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput).toMutableList()
        val w = input.maxOf { it.x }
        val h = input.maxOf { it.y }

        val grid = (0..h).map { y ->
            (0..w).map { x ->
                '.'
            }.toMutableList()
        }

        var p = Point(0, 0)
        var iteraion = 0
        while (true) {
            p = input.removeFirst()
            grid[p.y][p.x] = '#'
//            val iteraitons = if(input.size<100) 12 else 1024
//            (0 until iteraitons).forEach { i ->
//                input[i].let { p ->
//                    grid[p.y][p.x] = '#'
//                }
//            }
            val start = Point(0, 0)
            val end = Point(w, h)
            val dijkstra = dijkstra(grid, start)
            val l = dijkstra.get(end)
            if(l==null )       {
                return p.x.toString() + "," + p.y
            }
        }


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
