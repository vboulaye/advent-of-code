package aoc2024.day13

import aoc2021.day13.X
import utils.Point
import utils.readInput
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {

        return this
    }


    data class Game(val a: Point, val b: Point, val prize: Point)

    fun clean(input: List<String>): List<Game> {

        var a: Point? = null;
        var b: Point? = null;
        var prize: Point? = null;
        return input
            .filter { line -> line.acceptInput() }
            .mapNotNull { line ->
                if (line.length == 0) {
                    Game(a!!, b!!, prize!!)
                } else {
                    when {
                        line.startsWith("Button A") -> {
                            Regex("Button A: X\\+(\\d+), Y\\+(\\d+)").find(line)!!
                                .destructured.let { (x, y) -> a = Point(x.toInt(), y.toInt()) }
                        }

                        line.startsWith("Button B") -> {
                            Regex("Button B: X\\+(\\d+), Y\\+(\\d+)").find(line)!!
                                .destructured.let { (x, y) -> b = Point(x.toInt(), y.toInt()) }
                        }

                        line.startsWith("Prize") -> {
                            Regex("Prize: X=(\\d+), Y=(\\d+)").find(line)!!
                                .destructured.let { (x, y) -> prize = Point(x.toInt(), y.toInt()) }
                        }
                    }
                    null
                }
            }
    }

    val part1ExpectedResult = 480L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.sumOf { game ->
//            r.X= nA*A.x+nB*b.x
//            r.y=nAa*A.y+nB*b.y
            //           ( r.X -nB*b.x)/A.x = nA
//            (r.y*A.x -r.X*A.y) /(b.y*A.x-*b.x*A.y )=  +nB
            //nA=(r.X - b.x*   (r.y*A.x -r.X*A.y) /(b.y*A.x-*b.x*A.y ) )/A.x
            var dB =
                (game.prize.y.toDouble() * game.a.x.toDouble() - game.prize.x.toDouble() * game.a.y.toDouble()) / (game.b.y.toDouble() * game.a.x.toDouble() - game.b.x.toDouble() * game.a.y.toDouble())
            //val nA = (game.prize.x - nB * game.b.x) / game.a.x
            var dA = (game.prize.x.toDouble() - dB * game.b.x.toDouble()) / game.a.x.toDouble()

            val nA = dA.roundToInt()
            val nB = dB.roundToInt()

            if (matches(nA, game, nB)) {
                (nA * 3 + nB).toLong()
            } else 0L
        }
    }

    private fun matches(nA: Int, game: Game, nB: Int) =
        ((nA * game.a.x + nB * game.b.x) == game.prize.x
                && (nA * game.a.y + nB * game.b.y) == game.prize.y)

    val part2ExpectedResult = 459236326669L

    data class Point2(val x: Long, val y: Long)
    data class Game2(val a: Point, val b: Point, val prize: Point2)

    fun part2(rawInput: List<String>): Long {
        val input = clean(rawInput)

        return input
            .map { Game2(it.a, it.b, Point2(it.prize.x + 10000000000000, it.prize.y + 10000000000000)) }
            .sumOf { game ->
//            r.X= nA*A.x+nB*b.x
//            r.y=nAa*A.y+nB*b.y
                //           ( r.X -nB*b.x)/A.x = nA
//            (r.y*A.x -r.X*A.y) /(b.y*A.x-*b.x*A.y )=  +nB
                //nA=(r.X - b.x*   (r.y*A.x -r.X*A.y) /(b.y*A.x-*b.x*A.y ) )/A.x
                var dB =
                    (game.prize.y.toDouble() * game.a.x.toDouble() - game.prize.x.toDouble() * game.a.y.toDouble()) / (game.b.y.toDouble() * game.a.x.toDouble() - game.b.x.toDouble() * game.a.y.toDouble())
                //val nA = (game.prize.x - nB * game.b.x) / game.a.x
                var dA = (game.prize.x.toDouble() - dB * game.b.x.toDouble()) / game.a.x.toDouble()

                val nA = dA.roundToLong()
                val nB = dB.roundToLong()

                if (matches2(nA, game, nB)) {
                    (nA * 3 + nB)
                } else 0
            }

    }

    private fun matches2(nA: Long, game: Game2, nB: Long) =
        ((nA * game.a.x + nB * game.b.x) == game.prize.x
                && (nA * game.a.y + nB * game.b.y) == game.prize.y)
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
