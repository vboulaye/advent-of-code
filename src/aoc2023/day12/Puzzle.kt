package aoc2023.day12

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    data class Records(val records: List<Record>) {
        fun countSolutions(): Result {
            return records.map { val countSolutions = it.countSolutions()
                println("$it -> $countSolutions")
                countSolutions
            }.sum()
        }

        fun expand():Records {
            return Records(records.map { record ->
                Record(buildList {
                    for (i in 1..5) {
                        addAll(record.damagedGroups)
                    }
                } , record.springs.repeat(5))
            })
        }

    }

    data class Record(val damagedGroups: List<Int>, val springs: String) {
        fun countSolutions(): Int {
            val solutions = buildList<String> {
                buildSolutions(springs, this)
            }
           return  solutions.map { springs ->
                val counts = mutableListOf<Int>()
                var count = 0
                for (spring in springs) {
                    if (spring == '#') {
                        count++
                    } else {
                        if(count>0) {
                            counts.add(count)
                        }
                        count = 0
                    }
                }
               if(count>0) {
                   counts.add(count)
               }
               counts
            }.count { it .equals (damagedGroups) }
        }

        private fun buildSolutions(springs: String, strings: MutableList<String>) {
            val indexOf = springs.indexOf('?')
            if (indexOf >= 0) {
                buildSolutions(springs.substring(0, indexOf) + "." + springs.substring(indexOf + 1), strings)
                buildSolutions(springs.substring(0, indexOf) + "#" + springs.substring(indexOf + 1), strings)
            } else {
                strings.add(springs)
            }
        }

    }

    fun String.parseInput(): Record {
        val split = this.split(" ")
        val damagedGroups = split[1].split(",").map { it.toInt() }
        val springs = split[0]
        return Record(damagedGroups, springs)
    }

    fun clean(input: List<String>): Records {
        return Records(input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() })
    }

    val part1ExpectedResult = 21
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.countSolutions()
    }

    val part2ExpectedResult = 525152
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
            .expand()

        return input.countSolutions()
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
