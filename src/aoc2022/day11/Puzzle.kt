package aoc2022.day11

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long

data class Monkey(
    val monkeyNum: Int,
    val startingItems: List<Long>,
    val operation: Operation,
    val testDivisibleBy: Int,
    val testTrueTargetMonkey: Int,
    val testFalseTargetMonkey: Int
) {

    var items: MutableList<Long> = startingItems.toMutableList()
    var inspections: Long = 0L
}


data class Operation(val left: String, val op: String, val right: String) {

    fun computeWorryLevel(old: Long): Long {
        val l = if (left == "old") old else (left.toLong())
        val r = if (right == "old") old else (right.toLong())
        return when (op) {
            "*" -> l * r
            "+" -> l + r
            else -> TODO("hop")
        }
    }
}

class Puzzle {
    fun clean(input: List<String>): List<Monkey> {
        val fullText = input
            .filter { line -> true }
            .map { line -> line }
            .joinToString("\n")
        val monkeysText = fullText.split(Regex("\n?Monkey "))
        return monkeysText
            .filter { !it.isEmpty() }
            .map { monkeyText ->
                // println(monkeyText)
                val split = monkeyText.split("\n")
                val monkeyNum = split[0][0].digitToInt()
                val startingItems = split[1].substring("  Starting items: ".length).split(", ").map { it.toLong() }
                val opAsList = split[2].split("= ")[1].split(" ")
                val operation = Operation(opAsList[0], opAsList[1], opAsList[2])
                val testDivisibleBy = split[3].substring("  Test: divisible by ".length).toInt()
                val testTrueTargetMonkey = split[4].substring("    If true: throw to monkey ".length).toInt()
                val testFalseTargetMonkey = split[5].substring("    If false: throw to monkey ".length).toInt()
//            "  Starting items: 79, 98\n" +
//                    "  Operation: new = old * 19\n" +
//                    "  Test: divisible by 23\n" +
//                    "    If true: throw to monkey 2\n" +
//                    "    If false: throw to monkey 3\n")
                Monkey(
                    monkeyNum,
                    startingItems,
                    operation,
                    testDivisibleBy,
                    testTrueTargetMonkey,
                    testFalseTargetMonkey
                )
            }
    }

    val part1ExpectedResult = 10605L
    fun part1(rawInput: List<String>): Result {
        val monkeys = clean(rawInput)

        (1..20).forEach {
            println("round " + it)

            monkeys.forEach { monkey ->
                monkey.items.forEach {
                    monkey.inspections++
                    val worryLevel = monkey.operation.computeWorryLevel(it)
                    val waitAbit = worryLevel / 3
                    val test = waitAbit % monkey.testDivisibleBy == 0L
                    if (test) {
                        monkeys[monkey.testTrueTargetMonkey].items.add(waitAbit)
                    } else {
                        monkeys[monkey.testFalseTargetMonkey].items.add(waitAbit)
                    }
                }
                monkey.items.clear()
            }
            monkeys.forEach {
                println("monkey " + it.monkeyNum + " =  " + it.items)
            }
        }
        val product = monkeys.map { it.inspections }.sorted().reversed().subList(0, 2).reduce { a, b -> a * b }
        return product
    }

    val part2ExpectedResult = 2713310158L
    fun part2(rawInput: List<String>): Result {
        val monkeys = clean(rawInput)

        (1..10000).forEach {
            println("round " + it)

            val maxDivider = monkeys.map { it.testDivisibleBy }.reduce { a, b -> a * b }

            monkeys.forEach { monkey ->
                monkey.items.forEach {
                    monkey.inspections++
                    val worryLevel = monkey.operation.computeWorryLevel(it) % maxDivider
                    val test = worryLevel % monkey.testDivisibleBy == 0L
                    if (test) {
                        monkeys[monkey.testTrueTargetMonkey].items.add(worryLevel)
                    } else {
                        monkeys[monkey.testFalseTargetMonkey].items.add(worryLevel)
                    }
                }
                monkey.items.clear()
            }
            monkeys.forEach {
                println("monkey " + it.monkeyNum + "(" + it.inspections + ")" + " =  " + it.items)
            }

            println()
        }
        val product = monkeys.map { it.inspections }.sorted().reversed().subList(0, 2).reduce { a, b -> a * b }
        return product
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
