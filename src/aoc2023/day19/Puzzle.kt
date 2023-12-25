package aoc2023.day19

import utils.readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    data class Part(val map: Map<String, Result>) {
        fun sum(): Result {
            return map.values.sum()
        }

    }

    data class Rule(val check: (x: Part) -> Boolean, val result: String, val key: String, val interval: IntRange) {


    }

    data class Game(val rulesMap: Map<String, List<Rule>>, val parts: List<Part>) {
        fun accept(): Result {
            return parts.sumOf { part ->
                var cur = rulesMap.get("in")!!
                while (true) {
                    val result = cur.first { it.check(part) }
                    if (result.result == "A") {
                        return@sumOf part.sum()
                    }
                    if (result.result == "R") {
                        return@sumOf 0
                    }
                    cur = rulesMap.get(result.result)!!

                }
                throw IllegalStateException()
            }

        }


        data class Solution(val map: MutableMap<String, IntRange>) {
            constructor() : this(
                mutableMapOf(
                    "x" to (1..4000),
                    "m" to (1..4000),
                    "a" to (1..4000),
                    "s" to (1..4000),
                )
            )

            fun copy(): Solution {
                return Solution(map.toMutableMap())
            }
        }

        fun process(key: String, sol: List<Solution>, solutions: MutableList<Solution>) {
            println(key + ": " + sol)

            if (key == "A") {
                solutions.addAll(sol)
                return
            }
            if (key == "R") {
                return
            }
            val rules = rulesMap[key]!!
            var remainingSolutions = sol
            rules.forEach { rule ->
                if (rule.key.isNotEmpty()) {
                    val matching = remainingSolutions.map {
                        it.copy().apply {
                            val before = this.map[rule.key]!!
                            val condition = rule.interval
                            this.map[rule.key] =
                                (max(before.start, condition.start)..min(before.endInclusive, condition.endInclusive))
                        }
                    }.filter { it.map.values.all { it.start <= it.endInclusive } }

                    val toMutableList = remainingSolutions.flatMap {
                        val left = it.copy().apply {
                            val before = this.map[rule.key]!!
                            val condition = rule.interval
                            this.map[rule.key] =
                                (min(before.start, condition.start) until max(before.start, condition.start))
                            if (before.intersect(this.map[rule.key]!!).isEmpty()) {
                                this.map[rule.key] = (1..0)
                            }
                        }
                        val right = it.copy().apply {
                            val before = this.map[rule.key]!!
                            val condition = rule.interval
                            this.map[rule.key] =
                                (min(before.endInclusive, condition.endInclusive) + 1..max(
                                    before.endInclusive,
                                    condition.endInclusive
                                ))
                            if (before.intersect(this.map[rule.key]!!).isEmpty()) {
                                this.map[rule.key] = (1..0)
                            }
                        }
                        listOf(left, right)

                    }.filter { it.map.values.all { it.start <= it.endInclusive } }
                        .toMutableList()
                    remainingSolutions = toMutableList

                    if (matching.isNotEmpty()) {
                        process(rule.result, matching, solutions)
                    }
                } else {
                    process(rule.result, remainingSolutions, solutions)
                    remainingSolutions = mutableListOf()
                }
            }

        }

        fun findSolutions(): Result {
            val solutions = mutableListOf<Solution>()
            process("in", mutableListOf(Solution()), solutions)
            return solutions.sumOf { solu ->
                val fold: Long =
                    solu.map.values.fold(1L) { acc: Long, it: IntRange ->
                        val l: Long = it.endInclusive.toLong() - it.start.toLong() + 1L
                        l * acc
                    }
                fold
            }
        }

        fun findSolutions2(): Result {

            val soluces: List<Solution> = findSolutions("A", listOf(Solution()))

            val duplicates: List<Solution> = buildList {
                (0 until soluces.size).forEach { idx ->
                    val left = soluces[idx]
                    (idx + 1 until soluces.size).forEach { otherIdx ->
                        val right = soluces[otherIdx]

                        val intersectMAp = left.map.map { (key, value) ->
                            val otherValue = right.map[key]!!
                            key to (max(value.start, otherValue.start)..min(
                                value.endInclusive,
                                otherValue.endInclusive
                            ))
                        }
                        if (intersectMAp.all { it.second.start <= it.second.endInclusive }) {
                            add(Solution(intersectMAp.toMap().toMutableMap()))
                        }
                    }
                }
            }

            return soluces.sumOf { solu ->
                val fold =
                    solu.map.values.fold(1L) { acc, it -> (it.endInclusive.toLong() - it.start.toLong() + 1L) * acc }
                fold
            } - duplicates.sumOf { solu ->
                val fold =
                    solu.map.values.fold(1L) { acc, it -> (it.endInclusive.toLong() - it.start.toLong() + 1L) * acc }
                fold
            }
        }

        private fun mergeIntRange(a: IntRange, b: IntRange): IntRange {
            val minInterval = max(a.start, b.start)
            val maxInterval = min(a.endInclusive, b.endInclusive)
            return (minInterval..maxInterval)

        }

        private fun findSolutions(result: String, sols: List<Solution>): List<Solution> {
            if (result == "in") {
                return sols
            }
            val buildList = buildList {
                rulesMap.forEach { (key, rules) ->
                    rules
                        .filter { it.result == result }
                        .forEach { rule ->
                            val possibleSols = sols.map { sol ->
                                val copy = Solution(sol.map.toMutableMap())
                                if (rule.key.isNotEmpty()) {
                                    copy.map[rule.key] = mergeIntRange(sol.map[rule.key]!!, rule.interval)
                                }
                                copy
                            }.filter {
                                it.map.values
                                    .all {
                                        it.start <= it.endInclusive
                                    }
                            }
                            if (possibleSols.isNotEmpty()) {
                                val findSolutions = findSolutions(key, possibleSols)
                                addAll(findSolutions)
                            }
                        }
                }
            }
            return buildList
        }
    }


    fun clean(input: List<String>): Game {
        val rulesMap = buildMap<String, List<Rule>> {
            input.forEach { line ->
                if (line.isEmpty()) {
                    return@forEach
                }
                val (ruleId, rule) = line.split("{")
                val rules = rule.substringBefore("}").split(",").map { instr ->
                    val instrParts = instr.split(":")
                    val result = instrParts[instrParts.size - 1]
                    if (instrParts.size > 1) {
                        val lessThen = instrParts[0].split('<')
                        if (lessThen.size > 1) {
                            val (attr, value) = lessThen
                            Rule({ x -> x.map[attr]!! < value.toInt() }, result, attr, (1 until value.toInt()));
                        } else {
                            val (attr, value) = instrParts[0].split('>')
                            Rule({ x -> x.map[attr]!! > value.toInt() }, result, attr, (value.toInt() + 1..4000));
                        }
                    } else {
                        Rule({ x -> true }, result, "", (1..4000));
                    }
                }
                put(ruleId, rules)
            }


        }
        var ready = false
        val parts = input.filter { line ->
            if (line.isEmpty()) {
                ready = true
                false
            } else {
                ready
            }
        }.map { line ->
            // {x=787,m=2655,a=1222,s=2876}
            val map = line.removePrefix("{").removeSuffix("}").split(",").map { attr ->
                val (key, value) = attr.split("=")
                key to value.toLong()
            }.toMap()
            Part(map)
        }
        return Game(rulesMap, parts)
    }

    val part1ExpectedResult = 19114L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.accept()
    }

    val part2ExpectedResult = 167409079868000L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.findSolutions()
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
