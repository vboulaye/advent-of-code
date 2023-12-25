package aoc2023.day03

import utils.readInput
import kotlin.streams.toList
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {
    fun clean(input: List<String>): List<List<Int>> {
        return input
            .filter { line -> true }
            .map { line -> line.chars().toList() }
    }

    val part1ExpectedResult = 4361
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val result = buildList {
            (0 until input.size).forEach { y ->
                (0 until input[y].size).forEach { x ->
                    val currentChar = input[y][x].toChar()
                    if (isDigit(currentChar) && !isPartOfNumber(input, y, x)) {
                        var hasSymbolAround = false
                        val digitList = buildList<String> {
                            run digitListBuilder@{
                                (x until input[y].size).forEach { x2 ->
                                    val currentChar2 = input[y][x2].toChar()

                                    if (isDigit(currentChar2)) {
                                        this.add(currentChar2.toString())
                                    } else {
                                        return@digitListBuilder
                                    }
                                    if (!hasSymbolAround && hasSymbolAround(input, y, x2)) {
                                        hasSymbolAround = true
                                    }
                                }
                            }
                        }
                        if (hasSymbolAround) {
                            add(digitList.joinToString("").toInt())
                        }
                    }
                }
            }
        }
        // to ohigh 523641
//        519444
        return result.sum()
    }

    private fun hasSymbolAround(input: List<List<Int>>, y: Int, x2: Int): Boolean {
        (y - 1..y + 1)
            .filter { y2 -> y2 >= 0 && y2 < input.size }
            .forEach { y2 ->
                (x2 - 1..x2 + 1)
                    .filter { x3 -> x3 >= 0 && x3 < input[y2].size }
                    .forEach { x3 ->
                        val currentChar3 = input[y2][x3].toChar()
                        if (!(isDigit(currentChar3) || currentChar3 == '.')) {
                            return true
                        }
                    }
            }
        return false;
    }

    private fun isPartOfNumber(
        input: List<List<Int>>,
        y: Int,
        x: Int
    ): Boolean {
        if (x == 0) return false;
        val prev = input[y][x - 1].toChar()
        return isDigit(prev)
    }

    private fun isDigit(currentChar2: Char) = currentChar2.isDigit()
            //&& currentChar2 != '.'

    val part2ExpectedResult = 467835
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val result = buildList<Int> {
            (0 until input.size).forEach { y ->
                (0 until input[y].size).forEach { x ->
                    val currentChar = input[y][x].toChar()
                    if (currentChar == '*') {
                        lookForNumber(input, y, x)?.let { this.add(it) }
                    }
                }
            }
        }
        return result.sum()
    }

    private fun lookForNumber(input: List<List<Int>>, y: Int, x: Int): Int? {
        val startPos = buildSet<Pair<Int, Int>> {
            (y - 1..y + 1)
                .filter { y2 -> y2 >= 0 && y2 < input.size }
                .forEach { y2 ->
                    (x - 1..x + 1)
                        .filter { x2 -> x2 >= 0 && x2 < input[y2].size }
                        .forEach { x2 ->
                            val currentChar = input[y2][x2].toChar()
                            if (isDigit(currentChar)) {
                                run to@{
                                    (x2 downTo 0).forEach { x3 ->
                                        val currentChar2 = input[y2][x3].toChar()
                                        if (!isDigit(currentChar2)) {
                                            val start = Pair(y2, x3 + 1)
                                            this.add(start)
                                            return@to
                                        }
                                        if (x3 == 0) {
                                            val start = Pair(y2, x3)
                                            this.add(start)
                                            return@to
                                        }
                                    }
                                }
                            }
                        }
                }
        }
        if (startPos.size != 2) {
            return null
        }
        startPos.map { (y, x) ->
            val number = buildList<String> {
                run lll@{
                    (x until input[y].size)
                        .forEach { x3 ->
                            val currentChar2 = input[y][x3].toChar()
                            if (isDigit(currentChar2)) {
                                add(currentChar2.toString())
                            } else {
                                return@lll
                            }
                        }
                }
            }
            number.joinToString("").toInt()
        }.let {
            return it[0] * it[1]
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
