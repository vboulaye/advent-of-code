package aoc2024.day03

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<MatchResult.Destructured> {
        val matchResult = Regex("mul\\((\\d+),(\\d+)\\)").findAll(this).toList()
        return matchResult.map { it.destructured }
    }

    fun clean(input: List<String>): List<List<MatchResult.Destructured>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 161
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.flatMap { it.map { (a, b) -> a.toInt() * b.toInt() } }.sum()
    }

    val part2ExpectedResult = 48
    fun part2(rawInput: List<String>): Result {
//        val input = clean(rawInput)
        val a = if (rawInput[0]!!.length < 100) {
            listOf("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")
        } else {
            rawInput.joinToString("")
        }
        //566731110
        //94455185

        val map = Regex("do\\(\\)(.*?)don't\\(\\)").findAll("do()"+a+"don't()").toList()
            .map { it.groupValues[1] }
            map.forEach { println(it) }
        val blocs = map
     return   blocs.sumOf { part1(listOf(it)) }
//        val regex = Regex("(do|don't)\\(\\)")
//        val matches = regex.findAll(a).toList()
//        var inDo = true
//        var total = 0
//        //too low 24842536 24842536
//        for (i in 0 until matches.size) {
//            if (i==0) {
//                val start = 0
//                val end = matches[i].range.first
//                val res = part1(listOf(a.substring(start, end)))
//                println(a.substring(start, end))
//                println(res)
//                total += res
//            }
//            if (matches[i].value == "do()") {
//                inDo = true
//            } else if (matches[i].value == "don't()") {
//                inDo = false
//            }
//            if (inDo) {
//                val start = matches[i].range.last + 1
//                val end = if (i == matches.size - 1) {
//                    a.length
//                } else {
//                    matches[i + 1].range.first
//                }
//                val res = part1(listOf(a.substring(start, end)))
//                println(a.substring(start, end))
//                println(res)
//                total += res
//            }
//        }
//        return total
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
