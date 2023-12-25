package aoc2023.day06

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {

    data class Race(val time: Long, val distance: Long) {
        fun computeDistance(accel: Long) = accel * (time - accel)
        fun isWinner(accel: Long) = computeDistance(accel) > distance
        fun Long.concat(l: Long) = (this.toString() + l.toString()).toLong()

        fun concat(race: Race) = Race(
            this.time.concat(race.time),
            this.distance.concat(race.distance)
        )
    }

    fun clean(input: List<String>): List<Race> {
        val times = input[0].parse()
        val destinations = input[1].parse()
        return times.zip(destinations) { time, distance -> Race(time, distance) }
    }

    fun String.parse() = this.substringAfter(":")
        .trim()
        .split(Regex(" +"))
        .map { it.trim().toLong() }

    val part1ExpectedResult = 288L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.map { race ->
            (1..race.time)
                .count { accel -> race.isWinner(accel) }.toLong()
        }.reduce { acc, i -> acc * i }
    }


    val part2ExpectedResult = 71503L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val bigRace = input.reduce { acc, race -> acc.concat(race) }

        val minAccel = (1 until bigRace.time)
            .first { accel -> bigRace.isWinner(accel) }
        val maxAccel = (bigRace.time downTo 1)
            .first { accel -> bigRace.isWinner(accel) }

        return maxAccel - minAccel + 1

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
