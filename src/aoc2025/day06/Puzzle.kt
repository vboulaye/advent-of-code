package aoc2025.day06

import utils.readInput
import kotlin.collections.get
import kotlin.div
import kotlin.text.get
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.times

typealias Result1 = Long
typealias Result2 = Long


class Puzzle {

    data class Game(val numbers: List<List<Long>>, val operations: List<String>)

    fun parseInput(rawInput: List<String>): Game {
        val numbers = rawInput
            .filter { line -> true }
            .take(rawInput.size - 1)
            .map { line -> //println(line);
                line.trim().split(Regex(" +")).map { //println("==="+it);
                    it.toLong()
                }
            }
        val operations: List<String> = rawInput.last().split(Regex(" +"))
        return Game(numbers, operations)
    }

    val part1ExpectedResult: Result1 = 4277556L
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)
        val transposedNumbers = input.numbers[0].indices.map { col ->
            input.numbers.map { row -> row[col] }
        }
        val result = input.operations.foldIndexed(0L) { idx, acc, op ->
            val foldIndexed = transposedNumbers[idx].drop(1)
                .fold(transposedNumbers[idx][0]) { acc, n ->
                    when (op) {
                        "+" -> acc + n
                        "*" -> acc * n
                        "-" -> acc - n
                        "/" -> acc / n
                        else -> error("Unknown operation")
                    }
                }
//            println(op + " = " + foldIndexed)
            foldIndexed + acc
        }

        return result
    }


    fun parseInput2(rawInput: List<String>): Game {
        var numbers = mutableListOf<List<Long>>()
        var numbersBloc = mutableListOf<Long>()
        (rawInput.map { it.length - 1 }.max() downTo 0)

            .forEach { colIndex ->
                println(colIndex)
                val col = rawInput
                    .take(rawInput.size - 1)
                    .map { line -> if(colIndex<line.length)  line[colIndex] else  ' ' }
                if (col.all { it == ' ' }) {
                    numbers.add(numbersBloc)
                    numbersBloc = mutableListOf<Long>()
                } else {
                    val vertNNumber = col
                        .filter { it != ' ' }
                        .joinToString("")
                        .toLong()
                    numbersBloc.add(vertNNumber)
                }
            }

        numbers.add(numbersBloc)
        println(numbers)
//        val numbers = rawInput
//            .take(rawInput.size - 1)
//            .map { line -> //println(line);
//                line.trim().split(Regex(" +")).map { //println("==="+it);
//                    it.toLong()
//                }
//            }
        val operations: List<String> = rawInput.last().split(Regex(" +")).reversed()
        return Game(numbers, operations)
    }

    val part2ExpectedResult: Result2 = 3263827L
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput2(rawInput)
        var transposedNumbers = input.numbers
//        var transposedNumbers = input.numbers[0].indices.map { col ->
//            input.numbers.map { row -> row[col] }
//        }
//
//        transposedNumbers = transposedNumbers.map { row ->
//            var updateableList = row.toList()
//            val workList = row.toMutableList()
//            var idx = 0
//            for (idx in 0..3) {
//
//                println("input " + updateableList + "  " + idx)
//                val newNumber = updateableList
//                    .map { number -> val padStart = number.toString().padStart(4, '0')
//                       // println("..." +number+" ->"+ padStart + " " + idx +" /"+    padStart[idx]+"/")
//                        padStart[idx]
//                    }
//                    .filter { it != ' ' }
//                    .joinToString("").toLong()
//                workList.add(newNumber)
//                println("outpu " + newNumber + "  " + idx)
//            }
//            workList
//        }
        val result = input.operations.foldIndexed(0L) { idx, acc, op ->
            val foldIndexed = transposedNumbers[idx].drop(1)
                .fold(transposedNumbers[idx][0]) { acc, n ->
                    when (op) {
                        "+" -> acc + n
                        "*" -> acc * n
                        "-" -> acc - n
                        "/" -> acc / n
                        else -> error("Unknown operation")
                    }
                }
//            println(op + " = " + foldIndexed)
            foldIndexed + acc
        }
        return result
    }

}


@OptIn(ExperimentalTime::class)
fun main() {
    val puzzle = Puzzle()
    println(Puzzle::class.qualifiedName)

    val testInput = readInput("00test", Puzzle::class)
    val input = readInput("zzdata", Puzzle::class)

    fun <R> runPart(part: String, expectedTestResult: R, partEvaluator: (List<String>) -> R) {
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
