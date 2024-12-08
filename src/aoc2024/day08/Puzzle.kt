package aoc2024.day08

import utils.Point
import utils.browsePoints
import utils.containsPoint
import utils.readInput
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

    val part1ExpectedResult = 14
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val antennaPositions = getAntennaPositions(input)

        return antennaPositions
            .flatMap { (antenna, positions) ->
                positions.flatMap { sourceAntenna ->
                    positions
                        .filter { it != sourceAntenna }
                        .flatMap {
                            val vector = it - sourceAntenna
                            listOf(sourceAntenna - vector, it + vector)
                                .filter { input.containsPoint(it) }
                        }

                }
            }
            .distinct()
            .count()
    }

    private fun getAntennaPositions(input: List<List<Char>>) = input.browsePoints()
        .filter { it.second != '.' }
        .groupBy({ it.second }, { it.first })

    val part2ExpectedResult = 34
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val antennaPositions = getAntennaPositions(input)

        val antinodes = antennaPositions
            .flatMap { (antenna, positions) ->
                positions.flatMap { sourceAntenna ->
                    positions
                        .filter { it != sourceAntenna }
                        .flatMap { targetAntenna ->
                            val vector = targetAntenna - sourceAntenna
//                            val newAntinodes = mutableListOf<Point>()
//                            var node = sourceAntenna
//                            while (node.y in input.indices && node.x in input[0].indices) {
//                                newAntinodes.add(node)
//                                node -= vector
//                            }
//                            node = targetAntenna
//                            while (node.y in input.indices && node.x in input[0].indices) {
//                                newAntinodes.add(node)
//                                node += vector
//                            }
//                            newAntinodes

                            generateSequence(sourceAntenna) { it - vector }
                                .takeWhile { input.containsPoint(it) } +
                                    generateSequence(targetAntenna + vector) { it + vector }
                                        .takeWhile { input.containsPoint(it) }
                        }
                        .distinct()

                }
                    .distinct()
            }
            .distinct()
        return antinodes
            .count()
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
