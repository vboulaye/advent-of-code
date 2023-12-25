package aoc2023.day22

import utils.*
import kotlin.collections.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): Vector3 {
        val split = this.split("~")
        return Vector3(Point3.parse(split[0]), Point3.parse(split[1]))
    }

    fun clean(input: List<String>): List<Vector3> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    private fun settle(input: List<Vector3>): List<Vector3> {
        val downList = mutableListOf<Vector3>()
        val sortedBy = input.sortedBy { it.minZ() }
        sortedBy
            .forEach { brick ->

                val directions = brick.pointsStraight()
                    .map { point ->
                        val floorZ: Int = downList
                            .filter { point.x in it.intervalX() && point.y in it.intervalY() }
                            .flatMap { it.pointsStraight() }
                            .filter { it.x == point.x && it.y == point.y }
                            .maxOfOrNull { it.z } ?: 0
                        point.z - floorZ - 1
                    }
                val minDirection = directions.min()
                val settledBrick = brick.translate(Point3(0, 0, -minDirection))
                downList.add(settledBrick)
            }

        return downList.toList()
    }

    private fun buildSupportsMap(settled: List<Vector3>): Map<Vector3, List<Vector3>> {
        val supportsMap = settled.mapIndexed { brickIndex, brick ->
            val supports = (0 until brickIndex) // only check the bricks "below" the current one
                .map { settled[it] }
                .filter { otherBrick -> otherBrick.intervalZ(1).intersect(brick.intervalZ()).isNotEmpty() } // they touch each other on at least one point
                .filter { otherBrick ->
                    otherBrick.pointsStraight()
                        .any { point ->
                            brick.pointsStraight()
                                .any { brickPoint -> point.z + 1 == brickPoint.z && point.x == brickPoint.x && point.y == brickPoint.y }
                        }
                }
            brick to supports
        }.toMap()
        return supportsMap
    }

    val part1ExpectedResult = 5

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val settled = settle(input)

        val brickSupports = buildSupportsMap(settled)

        val required = brickSupports.values.filter { it.size == 1 }.map { it[0] }.toSet()

        return settled.count { !required.contains(it) }
    }

    val part2ExpectedResult = 7
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val settled = settle(input)

        val brickSupports = buildSupportsMap(settled)

        val required = brickSupports.entries.filter { it.value.size == 1 }.map { it.value[0] to it.key }.toMap()

        return settled
            .filter { required.containsKey(it) }
            .sumOf { disintegratedBrick ->
                val droppedBricks = mutableSetOf(disintegratedBrick, required[disintegratedBrick])
                settled.forEach { brick ->
                    if (!droppedBricks.contains(brick)
                        && brick.minZ() > disintegratedBrick.minZ() //not the ones below the current one
                        && brickSupports[brick]!!.all { droppedBricks.contains(it) }
                    ) {
                        droppedBricks.add(brick)
                    }
                }
                droppedBricks.size - 1
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

    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
