package aoc2023.day01

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int

class Puzzle {
    fun clean(input: List<String>): List<String> {
        return input
            .filter { line -> true }
            .map { line -> line }
    }

    val CONV= mapOf(
            "0" to 0,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9,
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,


    )


    val part1ExpectedResult = 142
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
     return   input.map { line ->
            val onlyDigits = line.replace(Regex("[^0-9]"),"")
            val number = onlyDigits[0] +""+ onlyDigits[onlyDigits.length-1]
            number.toInt()
        }.sum()
    }

    val part2ExpectedResult = 281
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
       return  input.map {line ->
//           (0..line.length-1)
//                   .filter { index -> CONV.any {line.substring(index).startsWith(it.key) }
//
//               line.forEachIndexed { index, c -> CONV.any {line.substring(index).startsWith(it.key) } }
//           }
//           val firstC = line.forEachIndexed { index, c -> CONV.any {line.substring(index).startsWith(it.key) } }
//           val lastC = .forEachIndexed { index, c -> CONV.any {line.substring(index).startsWith(it.key) } }

            var test = line
            val list = MutableList<Int>(0, { 0 })

            while (test.length>0) {
                when {
                    Regex("^[0-9].*").matches(test) -> {
                        list.add((""+test[0]).toInt())
                        test=test.substring(1)
                    }
                    test.startsWith("one") ->  {list.add(1); test=test.substring(1);}
                    test.startsWith("two") ->  {list.add(2); test=test.substring(1);}
                    test.startsWith("three") ->  {list.add(3); test=test.substring(1);}
                    test.startsWith("four") ->  {list.add(4); test=test.substring(1);}
                    test.startsWith("five") ->  {list.add(5); test=test.substring(1);}
                    test.startsWith("six") ->  {list.add(6); test=test.substring(1);}
                    test.startsWith("seven") ->  {list.add(7); test=test.substring(1);}
                    test.startsWith("eight") ->  {list.add(8); test=test.substring(1);}
                    test.startsWith("nine") ->  {list.add(9); test=test.substring(1);}


                        else -> test=test.substring(1)
                }

            }

            val first=(""+list[0])
            val last=(""+list[list.size-1])
           val toInt = ("" + first[0] + last[last.length - 1]).toInt()
           toInt
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

//    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
