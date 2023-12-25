package aoc2023.day25

import aoc2023.day17.FindRelated
import aoc2023.day17.PathFinder
import aoc2023.day17.WorkPathElement
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Pair<String, String>> {
        val split = this.split(": ")
        val from = split[1].split(" ").map { split[0] to it.trim() }
        return from
    }

    data class Components(val connections: List<Pair<String, String>>) {
        val map = connections
            .flatMap { listOf(it.first to it.second, it.second to it.first) }
            .groupBy { it.first }
            .mapValues { it.value.map { it.second } }
        fun getGroups(): List<List<String>> {
            val groups = mutableListOf<List<String>>()
            val seen = mutableSetOf<String>()
            for (key in map.keys) {
                if (seen.contains(key)) continue
                val group = mutableListOf<String>()
                val toVisit = mutableListOf<String>()
                toVisit.add(key)
                while (toVisit.isNotEmpty()) {
                    val current = toVisit.removeAt(0)
                    if (seen.contains(current)) continue
                    seen.add(current)
                    group.add(current)
                    toVisit.addAll(map[current]!!)
                }
                groups.add(group)
            }
            return (groups).toList()
        }
    }

    fun clean(input: List<String>): List<Pair<String, String>> {
        return input
            .filter { line -> line.acceptInput() }
            .flatMap { line -> line.parseInput() }

    }

    val part1ExpectedResult = 54
    fun part1(rawInput: List<String>): Result {
        val input =clean(rawInput)

        val components = Components(input)
        val kaeys = input
        (0 until kaeys.size).forEach { i ->
            (i + 1 until kaeys.size).forEach { j ->
                    val a = kaeys[i]

                    val b = kaeys[j]

//                        PathFinder<String>{ (w:WorkPathElement<String>)->components.map[w.el]!!.map { WorkPathElement(it) }}


            }
        }

        val keys = input
        (0 until keys.size).forEach { i ->
            (i + 1 until keys.size).forEach { j ->
                (j + 1 until keys.size).forEach { k ->
                    val a = keys[i]
                    val b = keys[j]
                    val c = keys[k]
                    val workMap = input.filter { !(it == a || it == b || it == c) }
                    val groups = Components(workMap).getGroups()
                    if (groups.size == 2) {
                        return groups[0].size * groups[1].size
                    }
                }
            }
        }
        return 0
    }

    val part2ExpectedResult = 0
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return 0
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
