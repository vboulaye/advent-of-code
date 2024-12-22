package aoc2024.day22

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): List<Int> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.toInt() }
    }

    val part1ExpectedResult = 37990510L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.sumOf { seq ->
            (1..2000).fold(seq.toLong()) { acc, _ -> next(acc) }
        }
    }

    val part2ExpectedResult = 23L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val seqToCombis: List<Pair<Int, Map<List<Int>, Int>>> = input.map { seq ->
            val bananas = bananas(seq)
            val changes = bananas(seq).zipWithNext { a, b -> b - a }
            val combis = changes.zipWithNext4 { it }
            val bestCombis = combis
                .mapIndexedNotNull { index, pair ->
                    val bananaIndex = index + 4
                    if (bananaIndex < bananas.size) {
                        bananas[bananaIndex] to pair
                    } else null
                }
            val combinationsToValue = bestCombis.groupBy({ it.second }, {it.first}).mapValues { it.value[0] }
            seq to combinationsToValue
        }

        val combinationToValuesList = seqToCombis.map {
            it.second.entries
        }
        return combinationToValuesList
            .flatten()
            .groupBy({ it.key }, { it.value })
            .map { (k, v) -> v.sum() }
            .max().toLong()

    }

    inline fun <T, R> Iterable<T>.zipWithNext4(transform: (a: List<T>) -> R): List<R> {
        val iterator = iterator()
        if (!iterator.hasNext()) return emptyList()
        if (!iterator.hasNext()) return emptyList()
        if (!iterator.hasNext()) return emptyList()
        var current1 = iterator.next()
        var current2 = iterator.next()
        var current3 = iterator.next()

        val result = mutableListOf<R>()
        while (iterator.hasNext()) {
            val next = iterator.next()
            result.add(transform(listOf(current1, current2, current3, next)))
            current1 = current2
            current2 = current3
            current3 = next
        }
        return result
    }

    private fun bananas(seq: Int): MutableList<Int> {
        var s = seq.toLong()
        val l = mutableListOf(getLstChar(s))
        (1..2000).map { i ->
            s = next(s)
            //println(i.toString().toString() + " -> " + s)
            l.add(getLstChar(s))
        }
        return l
    }

    private fun getLstChar(s: Long): Int {
        val toString = s.toString()
        val c = toString[toString.length - 1]
        return c.code - '0'.code
    }

    private fun next(seq: Long): Long {
        val s = seq.toLong()
        val s1 = mixAndPrune(s * 64L, s)
        val s2 = mixAndPrune(s1 / 32, s1)
        val s3 = mixAndPrune(s2 * 2048, s2)
        return s3
    }


    private fun mixAndPrune(value: Long, secret: Long): Long {
        val mix = value xor secret
        return mix.mod(16777216L)
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
