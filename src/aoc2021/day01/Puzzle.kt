package aoc2021.day01

import utils.readInput

data class Ctx(
    val last: Int = Integer.MAX_VALUE,
    val counter: Int = 0
) {
    fun next(newVal: Int): aoc2021.day01.Ctx = when {
        newVal > last -> aoc2021.day01.Ctx(newVal, counter + 1)
        else -> aoc2021.day01.Ctx(newVal, counter)
    }
}

class Puzzle {

    fun part1(input: List<String>): Int {
        return aoc2021.day01.part1Work(aoc2021.day01.part1Clean(input))
    }


    fun part2(input: List<String>): Int {
        return aoc2021.day01.part2Work(aoc2021.day01.part1Clean(input))
    }

}
fun part1Clean(input: List<String>): List<Int> {
    return input.map(String::toInt)
}

fun part1Work(input: List<Int>): Int {
    return input
        .fold(aoc2021.day01.Ctx()) { a, v -> a.next(v) }
        .counter
}

fun part2Work(input: List<Int>): Int {
    return input
        .foldIndexed(aoc2021.day01.Ctx()) { i, a, v ->
            when {
                i < 2 -> a
                else -> a.next(v + input[i - 1] + input[i - 2])
            }

        }
        .counter
}

fun main() {
    val puzzle = aoc2021.day01.Puzzle()
    println(aoc2021.day01.Puzzle::class.qualifiedName)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("test", aoc2021.day01.Puzzle::class)
    check(puzzle.part1(testInput) == 7)
    check(puzzle.part2(testInput) == 5)

    val input = readInput("data", aoc2021.day01.Ctx::class)
    println(puzzle.part1(input))
    println(puzzle.part2(input))
}
