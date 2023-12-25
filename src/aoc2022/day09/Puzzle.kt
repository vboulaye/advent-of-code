package aoc2022.day09

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


data class Pos(val x: Int, val y: Int) {
    fun moveDir(dir: String): Pos {
        val newHead = when {
            dir == "U" -> Pos(this.x, this.y + 1)
            dir == "D" -> Pos(this.x, this.y - 1)
            dir == "L" -> Pos(this.x - 1, this.y)
            dir == "R" -> Pos(this.x + 1, this.y)
            else -> {
                throw NotImplementedError()
            }
        }
        return newHead
    }

    fun moveCloser(newHead: Pos): Pos {
        val xDiff = newHead.x - this.x
        val yDiff = newHead.y - this.y
        var xd = 0
        var yd = 0
        if (xDiff * xDiff > 1 || yDiff * yDiff > 1) {
            xd = when {
                xDiff >= 1 -> 1
                xDiff <= -1 -> -1
                else -> 0
            }
            yd = when {
                yDiff >= 1 -> 1
                yDiff <= -1 -> -1
                else -> 0
            }
        }
        val newTail = Pos(this.x + xd, this.y + yd)
        return newTail
    }

}
fun printSnake(pos:Collection<Pos>) {
    val minX = minOf(0,pos.minOf { it.x })
    val minY = minOf(0,pos.minOf { it.y })
    val maxX = pos.maxOf { it.x }
    val maxY = pos.maxOf { it.y }
    (maxY downTo minY).forEach { y ->
        (minX..maxX).forEach { x ->
            if (x == 0 && y == 0) print('s')
            else
                print(if (pos.contains(Pos(x, y))) '#' else '.')
//                print(if (pos.contains(Pos(x, y))) pos.indexOf(Pos(x, y)) else '.')
        }
        println()

    }
}

val tailPos = HashSet<Pos>()

data class Snake(val nodes: List<Pos>) {

    constructor(size: Int) : this((1..10).map { Pos(0, 0) })


    fun move(dir: String): Snake {
        val newHead = nodes[0].moveDir(dir)
        val list = MutableList<Pos>(1) { newHead };

        nodes.subList(1, nodes.size)
            .forEach { node ->
                val previousNode = list.last()
                val newTail = node.moveCloser(previousNode)
                list.add(newTail)
            }

        tailPos.add(list.last())

        val snake = Snake(list)
//        println(snake)
//        printSnake(list)

        return snake
    }


    fun move(dir: String, count: Int): Snake {
        return (1..count).fold(this) { acc, i -> acc.move(dir) }
    }

}

class Puzzle {
    fun clean(input: List<String>): List<String> {
        return input
            .filter { line -> true }
            .map { line -> line }
    }

    val part1ExpectedResult = 88
    fun part1(rawInput: List<String>): Result {
        tailPos.clear()
        val input = clean(rawInput)
        val start = Pos(0, 0)
        tailPos.add(start)

        val snake = Snake(listOf(start, start))
        val finalSnake = input.fold(snake) { acc, s ->
            val p = s.split(" ")
            acc.move(p[0], p[1].toInt())
        }
        printSnake(tailPos)

        return tailPos.size
    }



    val part2ExpectedResult = 36
    fun part2(rawInput: List<String>): Result {
        tailPos.clear()
        val input = clean(rawInput)
        val start = Pos(0, 0)
        tailPos.add(start)
        val snake = Snake((1..10).map { start })
        val finalSnake = input.fold(snake) { acc, s ->
            val p = s.split(" ")
            acc.move(p[0], p[1].toInt())
        }
//        2608 too high
        println("::::::::::::::::::::::::::::")
        printSnake(tailPos)
        return tailPos.size
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
