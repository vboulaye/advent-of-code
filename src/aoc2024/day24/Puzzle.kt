package aoc2024.day24

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    data class Gate(val source: Pair<String, String>, val operator: String, val target: String)
    data class Game(
        val data: MutableMap<String, Int>,
        val inputs: MutableMap<Pair<String, String>, List<Gate>>,
        val outputs: MutableMap<String, Gate>

    ) {

        val zKeys = outputs.keys.filter { it.startsWith("z") }.sorted()

        fun missValues(): Boolean {
            return zKeys.all { data[it] != null }
        }

        fun getZ(): Long {
            val zValue = zKeys.map { data[it] }.reversed()
            return zValue.fold(0L) { acc, i -> acc * 2L + i!! }
        }
    }

    fun clean(input: List<String>): Game {

        val data: MutableMap<String, Int> = mutableMapOf<String, Int>();
        val inputs: MutableMap<Pair<String, String>, List<Gate>> = mutableMapOf<Pair<String, String>, List<Gate>>();
        val outputs: MutableMap<String, Gate> = mutableMapOf<String, Gate>();
        input
            .filter { line -> line.acceptInput() }
            .forEach { line ->
                when {
                    line.contains(":") -> {
                        val split = line.split(": ")
                        data.put(split[0], split[1].toInt())
                    }

                    line.contains("->") -> {
                        val split = line.split(" -> ")
                        val (inputs2, operator) = split[0].split(" ").let { source ->
                            Pair(source[0], source[2]) to source[1]
                        }
                        val target = split[1]
                        val gate = Gate(inputs2, operator, target)
                        inputs.get(inputs2)?.let { gates ->
                            inputs.put(inputs2, gates + listOf(gate))
                        } ?: inputs.put(inputs2, listOf(gate))
//                        if (inputs.put(inputs2, gate)!=null) throw Exception("duplicate input")
                        if (outputs.put(target, gate) != null) throw Exception("duplicate input")
                    }

                    else -> {

                    }
                }
            }
        return Game(data, inputs, outputs)
    }

    val part1ExpectedResult = 2024L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        while (!input.missValues()) {
            input.outputs.values
                .forEach { gate ->
                    val s1 = input.data[gate.source.first]
                    val s2 = input.data[gate.source.second]
                    if (s1 == null || s2 == null) {
                        return@forEach
                    }
                    val r = when (gate.operator) {
                        "AND" -> s1 and s2
                        "OR" -> s1 or s2
                        "XOR" -> s1 xor s2
                        else -> throw Exception("unknown operator")
                    }
                    input.data[gate.target] = r

                }
        }
        return input.getZ()
    }

    val part2ExpectedResult = 0L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return 0L
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
