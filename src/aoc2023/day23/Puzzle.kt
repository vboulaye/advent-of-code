package aoc2023.day23

import utils.Direction
import utils.Point
import utils.Vector
import utils.readInput
import java.util.PriorityQueue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Char> {
        return this.toList()
    }

    fun clean(input: List<String>): Maze {
        val lists = input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
        return Maze(lists)
    }


    data class Maze(val grid: List<List<Char>>) {
        val width = grid[0].lastIndex
        val height = grid.lastIndex
        var part1: Boolean = true
        fun get(p: Point): Char? {
            try {
                return grid[p.y][p.x] ?: null
            } catch (e: Exception) {
                return null
            }
        }

        val topLeft = Point(1, 0)
        val bottomRight = Point(width - 1, height)
        val start = Move(topLeft)
        val end = bottomRight


        val cacheNeighbours = mutableMapOf<Point, List<Point>>()

        fun findDistance(): Result {
            var toVisit = PriorityQueue<Move>(compareBy { -it.distance })
            toVisit.add(start)
            var maxDist = 0
            while (toVisit.isNotEmpty()) {

                val currentPoint = toVisit.poll()
                // println("${currentPoint.distance}")
//                currentPoint.visited = true
//                toVisit.remove(currentPoint)
                if (currentPoint.to != end) {
//                    toVisit.removeIf {
//                        it == currentPoint
//                                || (it.to == currentPoint.to && it.distance<=currentPoint.distance)
//                    }
                    val neighbours = currentPoint.findNeighbours()
                    // println("" + currentPoint + " => " + neighbours.map { it.to })
                    toVisit.addAll(neighbours)
                } else {
//                    val disatnceMap = currentPoint.getPath().map { it.to to it.distance }.toMap()
//                    val before  = toVisit.size
//                    toVisit.removeIf {
//                        val pathDistance = disatnceMap[it.to]
//                        if (pathDistance == null) {
//                            false
//                        } else {
//                            it.distance < pathDistance
//                        }
//                    }
//                    if (before>toVisit.size) {
//                        println("$before => ${toVisit.size}")
//                    }
                    val distance = currentPoint.distance
                    if (maxDist < distance) {
                        println("found $distance ${toVisit.size}")
                        maxDist = distance
                    }

                }
            }
            return maxDist
        }

        inner class Move(val to: Point, val previous: Move? = null) {
            var  path: Set<Point>? = null
            val distance = if (previous == null) 0 else getDistance(start)

            override fun toString(): String {
                val list = calculatePath()
                return list.reversed().map {
                    it.toString()
                }.joinToString(" -> ")
            }

            fun calculatePath(): Set<Point> {
                if (path == null) {
                    val list = mutableListOf<Move>()
                    var p: Move? = this
                    while (p != null) {
                        list.add(p)
                        p = p.previous
                    }
                    path = list.map { it.to }.toSet()
                }
                return path!!
            }

            fun findNeighbours(): List<Move> {
                val currentPoint = to
                return getNeighbourPoints(currentPoint)
                    .filter {
                            !calculatePath().contains(it)
//                        var p = previous
//                        while (p != null) {
//                            if (p.to == it) {
//                                return@filter false
//                            }
//                            p = p.previous
//                        }
//                        true
                    }
                    .map { Move(it, this) }

            }


              fun getNeighbourPoints(currentPoint: Point): List<Point> {
                return cacheNeighbours.computeIfAbsent(currentPoint) {
                    val nextPoints = calNextPoints(currentPoint)
                    nextPoints
                }
            }

            private fun calNextPoints(currentPoint: Point, start: Set<Point> = setOf(currentPoint)): List<Point> {
                val nextPoints = getClosestNextPoints(currentPoint)

                if (nextPoints.size == 1) {
                    run {
                        nextPoints[0] = getFurtherPoint(currentPoint, nextPoints[0])
                    }
                } else if (nextPoints.size == 2) {
                    run {
                        nextPoints[0] = getFurtherPoint(currentPoint, nextPoints[0])
                    }
                    run {
                        nextPoints[1] = getFurtherPoint(currentPoint, nextPoints[1])
                    }
                }

                return nextPoints
            }

            private fun getFurtherPoint(previousPoint: Point, nextPoint: Point): Point {
                var possibleNextPoint = nextPoint
                var previous = previousPoint
                val direction = nextPoint - previousPoint
                while (true) {
                    if (getClosestNextPoints(possibleNextPoint).size !in (1..2)) {
                        break;
                    }
                    val nextPoint = possibleNextPoint + direction
                    if (get(nextPoint) == '.') {
                        possibleNextPoint = nextPoint
                    } else {
                        break
                    }
//                    val calNextPoints = getClosestNextPoints(possibleNextPoint)
//                    if (calNextPoints.size == 1) {
//                        if (calNextPoints[0] == previous) {
//                            break
//                        }
//                        previous = possibleNextPoint
//                        possibleNextPoint = calNextPoints[0]
//                    } else if (calNextPoints.size == 2) {
//                        val newNextPoint = if (calNextPoints[0] == previous) {
//                            calNextPoints[1]
//                        } else {
//                            calNextPoints[0]
//                        }
//                        previous = possibleNextPoint
//                        possibleNextPoint = newNextPoint
//                    } else {
//                        break
//                    }
                }
                return possibleNextPoint
            }

            private fun getClosestNextPoints(currentPoint: Point) = getNextDirection(currentPoint)
                .map {
                    var point = currentPoint + it.coord()
                    //                            while (true) {
                    //                                if(!isPossible(point+it.coord())) {
                    //                                    break
                    //                                }
                    //                                point = point + it.coord()
                    //                            }
                    point
                }
                .filter {
                    isPossible(it)
                }
                .toMutableList()

            private fun isPossible(it: Point): Boolean {
                val get = get(it)
                return get != '#' && get != null
            }

            private fun getNextDirection(currentPoint: Point): List<Direction> {
                if (part1) {
                    return when (get(currentPoint)) {
                        '>' -> listOf((Direction.R))
                        '<' -> listOf((Direction.L))
                        'v' -> listOf((Direction.D))
                        '^' -> listOf((Direction.U))
                        else -> Direction.values().toList()
                    }
                } else {
                    return Direction.values().toList()
                }
            }

            fun getDistance(start: Move): Int {
                val vector = Vector(this.to, this.previous!!.to)
                val manhattanLength = vector.manhattanLength()
                return manhattanLength + previous.distance
//                var p: Move? = this
//                var counter = 0
//                while (p != null) {
//                    if (p == start) {
//                        return counter
//                    }
//                    val previous1 = p.previous
//                    if (previous1 != null) {
//                        val vector = Vector(p.to, previous1.to)
//                        counter += vector.manhattanLength()
//                    }
//                    p = p.previous
//                }
//                throw IllegalArgumentException()
            }
        }


    }


    val part1ExpectedResult = 94
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.findDistance()
    }

    val part2ExpectedResult = 154
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val noslopes = input.grid.map { row ->
            row.map {
                when (it) {
                    '>' -> '.'
                    '<' -> '.'
                    'v' -> '.'
                    '^' -> '.'
                    else -> it
                }
            }
        }
        //5050 too low 5934 5942 6162 6226 6414

        // 6590
        val maze = Maze(noslopes)
        maze.part1 = false
        return maze.findDistance()
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
