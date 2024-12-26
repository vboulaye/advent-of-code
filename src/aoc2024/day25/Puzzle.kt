package aoc2024.day25

import utils.readInput
import java.util.random.RandomGeneratorFactory.all
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result1 = Int
typealias Result2 = Int


class Puzzle {


    fun parseInput(rawInput: List<String>): Pair<MutableList<List<Int>>, MutableList<List<Int>>> {
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()


        val grid = mutableListOf<List<Char>>()

        rawInput
            .filter { line -> true }
            .forEach { line ->
                if (line.trim().isEmpty()) {
                    processGrid(grid, locks, keys)
                    grid.clear()
                } else {
                    grid.add(line.toList())
                }
            }

        if (!grid.isEmpty())
        processGrid(grid, locks, keys)

        return locks to keys
    }

    private fun processGrid(
        grid: MutableList<List<Char>>,
        locks: MutableList<List<Int>>,
        keys: MutableList<List<Int>>
    ): Boolean {
        val firstLine = grid[0]
        val width = firstLine.size
        return if (firstLine.all { it == '#' }) {
            locks.add(
                grid.fold(MutableList(width) { -1 })
                { acc, row ->
                    row.forEachIndexed { index, v -> acc[index] += if (v == '#') 1 else 0 }
                    acc
                }
            )
        } else {
            keys.add(
                grid.reversed().fold(MutableList(width) { -1 })
                { acc, row ->
                    row.forEachIndexed { index, v -> acc[index] += if (v == '#') 1 else 0 }
                    acc
                }
            )
        }
    }

    val part1ExpectedResult: Result1 = 3
    fun part1(rawInput: List<String>): Result1 {
        val input = parseInput(rawInput)
        val filter = input.first.flatMap { lock ->
            input.second.map { key ->
                lock to key
            }
        }
            .filter { (lock, key) ->
                lock.indices.all { lock[it] + key[it] <= 5 }
            }
        return filter.size
    }

    val part2ExpectedResult: Result2 = 0
    fun part2(rawInput: List<String>): Result2 {
        val input = parseInput(rawInput)

        return 0
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
