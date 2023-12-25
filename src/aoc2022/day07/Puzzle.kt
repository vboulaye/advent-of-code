package aoc2022.day07

import utils.readInput
import java.util.HashMap
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


data class File(val parent: Directory, val name: String, val size: Int) {
    init {
        parent.files[name] = this
    }


}

data class Directory(val parent: Directory?, val name: String) {
    val files = HashMap<String, File>()
    val dirs = HashMap<String, Directory>()

    init {
        if (parent != null) {
            parent.dirs[name] = this
        }
    }

    var totalSize: Int = -1

    fun computeSize(): Int {
        if (totalSize < 0) {
            totalSize = files.values.sumOf { it.size } + dirs.values.sumOf { it.computeSize() }
        }
        return totalSize
    }

    fun asList(): List<Directory> {
        return listOf(this) + dirs.values.flatMap { it.asList() }
    }
}


class Puzzle {
    fun clean(input: List<String>): List<String> {
        return input
            .filter { line -> true }
            .map { line -> line }
    }

    val part1ExpectedResult = 95437

    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val root = parse(input)

        return root.asList()
            .map {it.computeSize()}
            .filter { it<=100000 }
            .sum()
    }

    private fun parse(input: List<String>): Directory {
        val root = Directory(null, "root")
        var current = root

        input.forEach {
            println(it)
            val parts = it.split(" ")
            when {
                parts[0] == "$" && parts[1] == "cd" -> {
                    if (parts[2] == "/") {
                        current = root
                    } else if (parts[2] == "..") {
                        current = current.parent!!
                    } else {
                        current = current.dirs[parts[2]]!!
                    }
                    println("cd to  " + current.name)
                }

                parts[0] == "$" && parts[1] == "ls" -> {
                    println("LS " + current.name)
                }

                parts[0] == "dir" -> {
                    Directory(current, parts[1])
                    println("create dir " + parts[1])
                }

                else -> {
                    File(current, parts[1], parts[0].toInt())
                    println("create file " + parts[1])
                }

            }
        }
        return root
    }

    val part2ExpectedResult = 24933642
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val root = parse(input)
        val remaining = 70000000 - root.computeSize()

        val needed = 30000000 - remaining
        return root.asList()
            .map {it.computeSize()}
            .filter { it>=needed }
            .min()
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
