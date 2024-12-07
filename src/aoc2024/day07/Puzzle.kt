package aoc2024.day07

import utils.readInput
import utils.removeFirst
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): Pair<Long, List<Long>> {
        val split: List<String> = this.split(": ")
        return split[0].toLong() to split[1].split(' ').map { it.toLong() }
    }

    fun clean(input: List<String>): List<Pair<Long, List<Long>>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 3749L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.filter { equation ->
            val (result, terms) = equation

            hasMatch(terms to mutableListOf<Char>(), result, terms)

        }.sumOf {
            it.first
        }
    }

    private fun hasMatch(terms: Pair<List<Long>, List<Char>>, result: Long, terms1: List<Long>): Boolean {
        if (testWith('*', terms, result, terms1)) return true
        if (testWith('+', terms, result, terms1)) return true
        if (part == 2) {
            if (testWith('|', terms, result, terms1)) return true

//            if (result.toString().startsWith(terms.first[0].toString())) {
//                val newTerms = terms.first.removeFirst()
//                val toMutableList = terms.second.toMutableList()
//                toMutableList.add('|')
//                val newr = result.toString().removePrefix(terms.first[0].toString()).toLong()
//                if (newTerms.size >= 2 && hasMatch(newTerms to toMutableList, newr, terms1)) {
//                    return true
//                } else {
//                    if (newTerms[0] == newr) return true
//                }
//            }

//            val p2 = terms.first.indices.any {
//
//                val r = terms.first.subList(0, it).joinToString("") + terms.first.subList(it, terms.first.size)
//                    .joinToString("")
//                r.toLong() == result
//            }
//            if (p2) return true
//            val newTerms = terms.first.removeFirst()
//            val toMutableList = terms.second.toMutableList()
//            toMutableList.add('|')
//            if (newTerms.size>=2 && hasMatch(newTerms to listOf(), result, terms1)) {
//                return true
//            } else {
//                if(newTerms[0]==result) return true
//            }
//            val newTerms2 = terms1.subList(0, terms.second.size+1)
//            if (newTerms2.size>=2 && hasMatch(newTerms2 to listOf(), result, terms1)) {
//                return true
//            } else {
//                if(newTerms2[0]==result) return true
//            }
        }
        return false

    }

    var part = 1
    private fun testWith(c: Char, terms: Pair<List<Long>, List<Char>>, result: Long, terms1: List<Long>): Boolean {
        val head = terms.first[0]
        val head2 = terms.first[1]
        val atEnd = terms.first.size == 2


        val withAdd = when (c) {
            '+' -> head + head2
            '*' -> head * head2
            '|' -> (head.toString() + head2.toString()).toLong()
            else -> error("unknown operator $c")
        }
        if (atEnd) return withAdd == result
        // if (withAdd >= result) return false
        val newOperators = terms.second.toMutableList()
        newOperators.add(c)
        val newTerms = terms.first
            .subList(2, terms.first.size)
            .toMutableList()
        newTerms.add(0, withAdd)
        if (hasMatch(newTerms to newOperators, result, terms1)) return true

        return false
    }

    val part2ExpectedResult = 11387L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        part = 2
        return input.filter { equation ->
            val (result, terms) = equation

            hasMatch(terms to mutableListOf<Char>(), result, terms)

        }.sumOf { it.first }
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
