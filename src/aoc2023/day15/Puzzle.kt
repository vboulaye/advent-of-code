package aoc2023.day15

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput() = this

    fun clean(input: List<String>): List<String> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }[0].split(",")
    }

    val part1ExpectedResult = 1320
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.fold(0) { acc, s ->
            val hash1 = s.fold(0) { hash, c ->
                (hash + c.code) * 17 % 256
            }
            acc + hash1
        }
    }

    data class Lens(val label: String, val action: String, val focal: Int) {
    }

    data class Step(val label: String, val action: String, val hash: Int, val focal: Int) {
    }

    val part2ExpectedResult = 145
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val steps = input.map { s ->
            if (s.endsWith("-")) {
                val label = s.substring(0, s.length - 1)
                val action = s.substring(s.length - 1)
                val hash = label.fold(0) { hash, c ->
                    (hash + c.code) * 17 % 256
                }
                Step(label, action, hash, 0)
            } else {
                val label = s.substringBefore("=")
                val action = "="
                val focal = s.substringAfter("=").toInt()

                val hash = label.fold(0) { hash, c ->
                    (hash + c.code) * 17 % 256
                }
                Step(label, action, hash, focal)
            }
        }
        // init boxes as a mutable list with 256 empty mutablelists
        val boxes = List(256) { mutableListOf<Lens>() }
        steps.forEach { step ->
            val boxContents = boxes[step.hash]
            if (step.action == "=") {
                val indexOfFirst = boxContents.indexOfFirst { it.label == step.label }
                if (indexOfFirst<0) {
                    boxContents.add(Lens(step.label, step.action, step.focal))
                } else {
                    boxContents[indexOfFirst]=(Lens(step.label, step.action, step.focal))
                }
            } else {
                boxContents.removeIf { it.label == step.label }
            }
        }
        return boxes.mapIndexed { boxIndex, steps: MutableList<Lens> ->
            steps.mapIndexed { index, step -> (boxIndex + 1) * (index + 1) * step.focal }.sum()
        }.sum()
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
