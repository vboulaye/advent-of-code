package aoc2024.day19

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): Game {
        var patterns = listOf("")
        var designs = listOf<String>()

        input
            .filter { line -> line.isNotEmpty() }
            .map { line ->
                when {
                    line.contains(", ") -> patterns = line.split(", ")
                    else -> designs += line
                }
            }
        return Game(patterns, designs)
    }

    data class Game(val patterns: List<String>, val designs: List<String>)

    val part1ExpectedResult = 6
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val r = Regex("^(" + input.patterns.joinToString("|") + ")+$")
        return input.designs.count { design ->
            r.matches(design)
        }
    }

    val part2ExpectedResult = 16
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val r = Regex("^(" + input.patterns.joinToString("|") + ")+$")
        val r2 = Regex("(" + input.patterns.joinToString("|") + ")")
        return input.designs
            .filter { design -> r.matches(design) }
            .sumOf { design ->
                print("\n\n\ncount($design) = \n")
                val d = decompose(design, input.patterns)
//                val count = count(r, design, r2, input.patterns,0)
                d.size
            }
    }

    val cache = mutableMapOf<String, List<List<String>>>()

    private fun decompose(design: String, patterns: List<String>): List<List<String>> {
        if (design.isEmpty()) return listOf(listOf())
        cache.get(design)?.let {
//            println("cache hit")
            return it }
        val flatMap = patterns
            .filter { design.startsWith(it) }
            .flatMap { firstPattern ->
                decompose(design.replaceFirst(firstPattern, ""), patterns)
                    .map { listOf(firstPattern) + it }
            }
        cache.put(design, flatMap)
        return flatMap
//            .map { firstPattern => decompose( design.replaceFirst(firstPattern, ""), patterns)
//                    .flatMap { l => listOf(it ) + l } }
    }

    val m = mutableMapOf<String, Regex>()
    private fun count(r: Regex, design: String, r2: Regex, patterns: List<String>, level: Int): Int {
        if (design.isEmpty()) {
            //println(" ".repeat(level) + "count($design) = 1")
            return 1
        }
        val sumOf = patterns
            .filter { design.startsWith(it) }
            .sumOf { pattern ->
                val count = count(r, design.replaceFirst(pattern, "").removePrefix(pattern), r2, patterns, level + 1)
                println(" ".repeat(level) + "count($design) = $count")
                if (count == 0) 0 else count
            }
        return sumOf
//        if (design.length == 1) return 1
//        val r3 = Regex("(" + patterns.joinToString("|") + ")")
//        if (!r3.matches(design)) return 0
//        return 1 + r3.findAll(design)
//            .sumOf { matchResult ->
//                val count = count(r, matchResult!!.value, r2, patterns.filter { it != matchResult!!.value })
//                if (count == 0) return 0
//                count
//            }

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
