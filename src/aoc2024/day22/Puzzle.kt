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

    val part1ExpectedResult = 37327623L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        return input.sumOf { seq ->
            var s = seq.toLong()
            (1..2000).forEach { i ->
                s = next(s)
                //println(i.toString().toString() + " -> " + s)
            }
            s
        }
    }

    val part2ExpectedResult = 23L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val seqToCombis = input.map { seq ->
            val bananas = bananas(seq)
            val changes = bananas(seq).zipWithNext { a, b -> b - a }
            val combis = changes.zipWithNext4 { a -> a }
            val bestCombis = combis
                .mapIndexed { index, pair ->

                    val bananaIndex = index + 4
                    if (bananaIndex >= bananas.size) {
                        Int.MIN_VALUE to pair
                    } else {
                        bananas[bananaIndex] to pair
                    }
                }
            val map = mutableMapOf<List<Int>, Int>()
            bestCombis.forEach { scorToCombi ->
                map.computeIfAbsent(scorToCombi.second) { scorToCombi.first }
            }
            seq to map
        }

        val m2 = mutableMapOf<List<Int>, List<Int>>()
        seqToCombis.forEach { (seq, bestCombisMap) ->
            bestCombisMap.entries.forEach{ (k,v)->
                val orDefault = m2.getOrDefault(k, mutableListOf()).toMutableList()
                orDefault.add(v)
                m2.put(k,orDefault)
            }
        }


        val first = m2.entries.map { (k, v) -> v.sum() }.max()
        return  first!!.toLong()
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

//    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
