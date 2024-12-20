package aoc2024.day20

import utils.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): List<MutableList<Char>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.toMutableList() }
    }

    val part1ExpectedResult = 0
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val start = input.findPoint('S')
        val end = input.findPoint('E')
        val walls = input.browsePoints()
            .filter { (p, v) ->
                isWall(p, input, v)
            }
        val disktraCompute = disktraCompute(start) { p ->
            p.neighbours()
                .filter { input.getPoint(it) != '#' }
                .map { it to 1 }
        }
        val maxTime= disktraCompute.get(end)!!
        val dijkstra = initDijkstra<Point>()
        val stepToDistance = (0 until maxTime)
            .map { step ->
                val computePath = dijkstra.computePath(start, end) { p, comingFrom ->
                    val l = dijkstra.generatePath(p, comingFrom).size
                    if (l == step+2 && input.getPoint(p) == '#' ) return@computePath emptyList()
                    p.neighbours()
                        .filter { input.containsPoint(it) && (input.getPoint(it) != '#' || (step == l ))  }
                        .map { it to 1 }
                }
                step to (maxTime-computePath.second)
            }

//        val flatMap = walls.flatMap { wall ->
//            val wallstoDrop = wall.first.neighbours()
//                .filter { n -> isWall(n, input, input.getPoint(n)) }
//                .map { wall.first to it }
//                .map { (a,b)->
//                    if(a<b) a to b else b to a
//                }
//                .distinct()
//
//
//            wallstoDrop.map { (a, b) ->
//                input.setPoint(a, '.')
//                input.setPoint(b, '.')
//
//                val disktraCompute = disktraCompute(start) { p ->
//                    p.neighbours()
//                        .filter { input.getPoint(it) != '#' }
//                        .map { it to 1 }
//                }
//
//                input.setPoint(a, '#')
//                input.setPoint(b, '#')
//
//                  (a to b) to (maxTime-disktraCompute.get(end)!!)
//            }
//        }


        val reversed = stepToDistance.sortedBy { it.second }.reversed()
        val reversedx = stepToDistance.groupingBy { it.second }
        return 0

    }

    private fun isWall(
        p: Point,
        input: List<MutableList<Char>>,
        v: Char
    ) = (p.x != 0 && p.y != 0
            && p.x != input[0].size - 1
            && p.y != input.size - 1
            && v == '#')

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


