package aoc2023.day02

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


data class Game(val id: Int, val cubes: List<Map<String, Int>>) {

}

class Puzzle {
    fun clean(input: List<String>): List<Game> {
        return input
                .filter { line -> true }
                .map { line ->
                    val match = Regex("Game (\\d+): (.*)").matchEntire(line)!!
                    val id = match.groupValues[1].toInt()
                    val cubesList = match.groupValues[2].split("; ")
                            .map { cubes ->
                                val map = cubes.split(", ")
                                        .map { cube ->
                                            val colorToCount = Regex("(\\d+) (.*)").matchEntire(cube)?.let { cubeMatch ->
                                                val count = cubeMatch.groupValues[1].toInt()
                                                val color = cubeMatch.groupValues[2]
                                                color to count
                                            }
                                            colorToCount
                                        }.filterNotNull().toMap()
                                map
                            }
                    Game(id, cubesList)
                }
    }

    val part1ExpectedResult = 8
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val wishedGame = mapOf(
                "red" to 12,
                "green" to 13,
                "blue" to 14,
        )
        return input.filter { game ->
            game.cubes.all { cube ->
                cube.all { (color, count) ->
                    count <= (wishedGame[color] ?: 0)
                }
            }
        }.map { it.id }
                .sum()

    }

    val part2ExpectedResult = 2286
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.map { game ->
            val minMap = mutableMapOf<String, Int>()
            game.cubes.map { cube ->
                cube.forEach { (color, count) ->
                    if (minMap[color] == null || minMap[color]!! < count) {
                        minMap[color] = count
                    }
                }
            }
            (minMap["red"] ?: 0) * (minMap["green"] ?: 0) * (minMap["blue"] ?: 0)
        }.sum()
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
