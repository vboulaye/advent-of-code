package aoc2022.day23

import utils.Point
import utils.readInput
import java.util.*
import java.util.function.Function
import java.util.function.Predicate
import kotlin.collections.HashSet
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


private const val ELF = '#'

private const val EMPTY = '.'

data class Grid(val elves: Set<Point>, val algoPos: Int) {

    val algos = listOf(
        Algo(this::isNorthFree) { elf: Point -> Point(elf.x, elf.y - 1) },
        Algo(this::isSouthFree) { elf: Point -> Point(elf.x, elf.y + 1) },
        Algo(this::isWestFree) { elf: Point -> Point(elf.x - 1, elf.y) },
        Algo(this::isEastFree) { elf: Point -> Point(elf.x + 1, elf.y) },
    ).toMutableList()

    init {
        Collections.rotate(algos, -algoPos)
    }

    fun get(p: Point): Char {
        return if (elves.contains(p)) ELF else EMPTY
    }

    fun isNorthFree(elf: Point): Boolean {
        return (-1..1).none { idx -> get(Point(elf.x + idx, elf.y - 1)) == ELF }
    }

    fun isSouthFree(elf: Point): Boolean {
        return (-1..1).none { idx -> get(Point(elf.x + idx, elf.y + 1)) == ELF }
    }

    fun isWestFree(elf: Point): Boolean {
        return (-1..1).none { idx -> get(Point(elf.x - 1, elf.y + idx)) == ELF }
    }

    fun isEastFree(elf: Point): Boolean {
        return (-1..1).none { idx -> get(Point(elf.x + 1, elf.y + idx)) == ELF }
    }

    data class Algo(val tester: Predicate<Point>, val nextSupplier: Function<Point, Point>) {

    }


    fun nextPos(elf: Point): Pair<Point, Point> {
        if (algos.all { it.tester.test(elf) }) {
            return Pair(elf, elf)
        }
        algos.forEach { algo ->

            if (algo.tester.test(elf)) {
                return Pair(elf, algo.nextSupplier.apply(elf))
            }
        }
        return Pair(elf, elf)
    }

    fun nextElvesPositions(): List<Point> {
        val moves = elves.map { nextPos(it) }
        val grousp = moves.groupBy { it.second }
        return moves
            .map { move -> if (grousp[move.second]!!.size > 1) move.first else move.second }

    }

    fun print() {

        val xMin = this.elves.map { it.x }.min()
        val xMax = this.elves.map { it.x }.max()
        val yMin = this.elves.map { it.y }.min()
        val yMax = this.elves.map { it.y }.max()
        (yMin..yMax).forEach { y ->

            print("" + y.toString().padEnd(3) + ": ")
            (xMin..xMax).forEach { x ->
                print(get(Point(x, y)))
            }
            println()
        }
    }
}

class Puzzle {

    fun clean(input: List<String>): Grid {
        val elfes = HashSet<Point>()
        input
            .filter { line -> true }
            .forEachIndexed { y, line ->
                line
                    .forEachIndexed() { x, ch -> if (ch == ELF) elfes.add(Point(x, y)) }
            }

        return Grid(elfes, 0)
    }

    val part1ExpectedResult = 110
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
//        input.print()
        val finalGrid = (1..10)
            .fold(input) { grid, round ->
                println("ROUND " + round)
                val nextElvesPositions = grid.nextElvesPositions()
                val grid1 = Grid(nextElvesPositions.toSet(), (grid.algoPos + 1) % 4)
//                grid1.print()
//                println()
//                println()
//                println()
                grid1
            }

//            .forEach { round ->
//            val moves = nextElvesPositions
//        }
        val xMin = finalGrid.elves.map { it.x }.min()
        val xMax = finalGrid.elves.map { it.x }.max()
        val yMin = finalGrid.elves.map { it.y }.min()
        val yMax = finalGrid.elves.map { it.y }.max()
        return (yMax - yMin + 1) * (xMax - xMin + 1) - finalGrid.elves.size
    }


    val part2ExpectedResult = 20
    fun part2(rawInput: List<String>): Result {
        var grid = clean(rawInput)
        grid.print()
        var round = 0
        while (round < 99999) {
            round++
            println("ROUND " + round)
            val nextElvesPositions = grid.nextElvesPositions()
            val diffElves = nextElvesPositions.toMutableSet()
            val diffs = diffElves.removeAll(grid.elves)
            println(diffElves.size)
            if (diffElves.isEmpty()) {
                return round
            }
            grid = Grid(nextElvesPositions.toSet(), (grid.algoPos + 1) % 4)
//            grid.print()
//            println()
//            println()
//            println()
        }

        return -1
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
