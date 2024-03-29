package aoc2021.day18

import utils.checkEquals
import utils.readInput
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long



data class SnailFishNumber(var line: String, val start: Int) {
    var end: Int = 0
    var comma: Int = 0
    var parentSnail: SnailFishNumber? = null
    var leftSnail: SnailFishNumber? = null
    var leftValue: Int = -1
    var rightSnail: SnailFishNumber? = null
    var rightValue: Int = -1
    fun left() = line.substring(start + 1, comma)
    fun right() = line.substring(comma + 1, end)

    override fun toString(): String {
        return "[${leftSnail ?: leftValue},${rightSnail ?: rightValue}]"
    }
}


class Puzzle {
    fun clean(input: List<String>): List<SnailFishNumber> {

        return input.filter { true }.map { line ->
            val stack = Stack<SnailFishNumber>()
            val reverseStack = Stack<SnailFishNumber>()

            line.forEachIndexed { index, c ->

                if (c == '[') {
                    stack.push(SnailFishNumber(line = line, start = index))
                }
                if (c == ',') {
                    val fishNumber = stack.peek()
                    fishNumber.comma = index
                    if (reverseStack.isEmpty()) {
                        fishNumber.leftValue = fishNumber.left().toInt()
                    } else {
                        val childFishNumber = reverseStack.pop()
                        fishNumber.leftSnail = childFishNumber
                        childFishNumber.parentSnail = fishNumber
                    }
                }
                if (c == ']') {
                    val fishNumber = stack.pop()
                    fishNumber.end = index
                    if (reverseStack.isEmpty()) {
                        fishNumber.rightValue = fishNumber.right().toInt()
                    } else {
                        val snailFishNumber = reverseStack.pop()
                        fishNumber.rightSnail = snailFishNumber
                        snailFishNumber.parentSnail = fishNumber
                    }
                    reverseStack.push(fishNumber)
                    //     println(fishNumber)
                }
            }

            val snailFishNumber = reverseStack.pop()
            snailFishNumber

        }
    }

    val part1ExpectedResult = 4140L
    fun part1(rawInput: List<String>): Result {


        val input = clean(rawInput)
        val explodes = clean(readInput("explode", Puzzle::class))

        explode(explodes[0], 0)
        checkEquals(explodes[0].toString(), "[[[[0,9],2],3],4]")
        explode(explodes[1], 0)
        checkEquals(explodes[1].toString(), "[7,[6,[5,[7,0]]]]")
        explode(explodes[2], 0)
        checkEquals(explodes[2].toString(), "[[6,[5,[7,0]]],3]")
        explode(explodes[3], 0)
        checkEquals(explodes[3].toString(), "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
        explode(explodes[4], 0)
        checkEquals(explodes[4].toString(), "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")

        hcek("000", "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
        hcek("00", "[[[[1,1],[2,2]],[3,3]],[4,4]]")
        hcek("01", "[[[[3,0],[5,3]],[4,4]],[5,5]]")
        hcek("02", "[[[[5,0],[7,4]],[5,5]],[6,6]]")
        hcek("another", "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
        val reduceSnail = reduceSnail(input)
        return magnitude(reduceSnail)
    }

    private fun hcek(file: String, result: String) {
        val reduced = reduceSnail(clean(readInput(file, Puzzle::class))).toString()
        checkEquals(reduced.toString(), result)
    }

    fun magnitude(reduceSnail: SnailFishNumber): Result {
        val l: Long = if (reduceSnail.leftValue > -1) {
            reduceSnail.leftValue.toLong()
        } else {
            magnitude(reduceSnail.leftSnail!!)
        }
        val r = if (reduceSnail.rightValue > -1) {
            reduceSnail.rightValue.toLong()
        } else {
            magnitude(reduceSnail.rightSnail!!)
        }
        return 3 * l + 2 * r
    }

    private fun reduceSnail(input: List<SnailFishNumber>): SnailFishNumber {
        val fishNumber = input.reduce { acc, snailFishNumber ->
            val result = SnailFishNumber(acc.line, 0)
            result.leftSnail = acc
            acc.parentSnail = result
            result.rightSnail = snailFishNumber
            snailFishNumber.parentSnail = result
            doExplode(result)
        }
        return fishNumber
    }

    private fun doExplode(result: SnailFishNumber): SnailFishNumber {
        while (explode(result, 0) || split(result)) {
        }
        return result
    }

    fun getTop(source: SnailFishNumber): SnailFishNumber? {
        var parent = source
        while (parent.parentSnail != null) {
            parent = parent.parentSnail!!
        }
        return parent
    }

    fun getAllValues(source: SnailFishNumber): List<Pair<SnailFishNumber, Int>> {
        val result = mutableListOf<Pair<SnailFishNumber, Int>>()
        if (source.leftValue > -1) {
            result.add(Pair(source, source.leftValue))
        } else {
            result.addAll(getAllValues(source.leftSnail!!))
        }
        if (source.rightValue > -1) {
            result.add(Pair(source, source.rightValue))
        } else {
            result.addAll(getAllValues(source.rightSnail!!))
        }
        return result
    }

    private fun explode(current: SnailFishNumber, depth: Int): Boolean {
        if (depth == 4 && (current.leftSnail != null || current.rightSnail != null)) {
            throw java.lang.IllegalStateException()
        }
        if (depth == 4 && current.leftSnail == null && current.rightSnail == null) {
//            println("explode")
            val allValues = getAllValues(getTop(current)!!)
            val indexOfFirst = allValues.indexOfFirst { it.first === current }
            if (indexOfFirst > 0) {
                val exploding = allValues[indexOfFirst - 1].first
                if (exploding.rightValue != -1) {
                    exploding.rightValue += current.leftValue
                } else {
                    exploding.leftValue += current.leftValue
                }
            }
            if (indexOfFirst + 2 < allValues.size) {
                val exploding = allValues[indexOfFirst + 2].first
                if (exploding.leftValue != -1) {
                    exploding.leftValue += current.rightValue
                } else {
                    exploding.rightValue += current.rightValue
                }
            }
            val parentSnail = current.parentSnail!!
            if (parentSnail.leftSnail == current) {
                parentSnail.leftSnail = null
                parentSnail.leftValue = 0
            } else if (parentSnail.rightSnail == current) {
                parentSnail.rightSnail = null
                parentSnail.rightValue = 0
            } else {
                throw IllegalStateException("Could not find parent snail")
            }
            return true
        }
        if (current.leftSnail != null && explode(current.leftSnail!!, depth + 1)) {
            return true
        }
        if (current.rightSnail != null && explode(current.rightSnail!!, depth + 1)) {
            return true
        }
        return false
    }

    private fun split(result: SnailFishNumber): Boolean {
        if (result.leftValue >= 10) {
//            println("split left")
            val newSnail = splitValueIntoSnailFishNumber(result.leftValue)
            result.leftSnail = newSnail
            newSnail.parentSnail = result
            result.leftValue = -1
            return true
        }
        if (result.leftSnail != null && split(result.leftSnail!!)) {
            return true
        }
        if (result.rightValue >= 10) {
//            println("split right")
            val newSnail = splitValueIntoSnailFishNumber(result.rightValue)
            result.rightSnail = newSnail
            newSnail.parentSnail = result
            result.rightValue = -1
            return true
        }

        if (result.rightSnail != null && split(result.rightSnail!!)) {
            return true
        }
        return false
    }

    private fun splitValueIntoSnailFishNumber(
        valueToSplit: Int
    ): SnailFishNumber {
        val newSnail = SnailFishNumber("", 0)
        newSnail.leftValue = valueToSplit / 2
        newSnail.rightValue = valueToSplit - newSnail.leftValue
        return newSnail
    }

    val part2ExpectedResult = 3993L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var maxMagnittude = 0L
        input.forEachIndexed { index, _ ->
            input.forEachIndexed { index1, _ ->
                if (index != index1) {
                    val input = clean(rawInput)
                    val line = input[index]
                    val otherline = input[index1]
                    val reduceSnail = reduceSnail(listOf(line, otherline))
                    val magnitude = magnitude(reduceSnail)
                    maxMagnittude = kotlin.math.max(maxMagnittude, magnitude)
                }
            }
        }
        return maxMagnittude
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
