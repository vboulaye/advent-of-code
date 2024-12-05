package aoc2024.day05

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): Pair<List<List<Int>>, List<List<Int>>> {
        val pageOrders = input
            .filter { line -> line.contains("|") }
            .map { line -> line.split("|").map { it.toInt() } }

        val pageUpdates = input
            .filter { line -> line.contains(",") }
            .map { line -> line.split(",").map { it.toInt() } }


        return pageOrders to pageUpdates
    }

    val part1ExpectedResult = 143
    fun part1(rawInput: List<String>): Result {

        val input = clean(rawInput)

        val index = input.first.groupBy({ it[0] }, { it[1] })
//        val index2 = index.map { (k, v) -> k to v.min()!! }.toMap()
        val okUpdates = input.second.filter { pageUpdates ->
            okUpdate(pageUpdates, index)
        }
        return okUpdates.sumOf { it[it.size / 2] }
    }

    private fun okUpdate(
        pageUpdates: List<Int>,
        index: Map<Int, List<Int>>
    ) = (0..pageUpdates.size - 2).all { i ->
        val testVal = pageUpdates[i]
        index[testVal] != null && pageUpdates.subList(i + 1, pageUpdates.size)
            .all { index[testVal]!!.contains(it) }
    }

    val part2ExpectedResult = 123
    fun part2(rawInput: List<String>): Result {

        val input = clean(rawInput)

        val index = input.first.groupBy({ it[0] }, { it[1] })
//        val index2 = index.map { (k, v) -> k to v.min()!! }.toMap()

//        val kUpdates = input.second.filter { pageUpdates ->
//            !okUpdate(pageUpdates, index)
//        }

        val okUpdates = input.second
            .filter { pageUpdates ->
                !okUpdate(pageUpdates, index)
            }.map { pageUpdates ->
                val pageUpdates2 = pageUpdates.toMutableList()
                    .sortedWith(Comparator { o1, o2 ->
                        if(index[o1]!=null &&   index[o1]!!.contains(o2) ) {
                            -1
                        } else {
                            1
                        }
                    })
//                (0..pageUpdates2.size - 2).filter { i ->
//                    val testVal = pageUpdates2[i]
//                    if (index[testVal] == null || !index[testVal]!!.contains(pageUpdates2[i + 1])) {
//
//                        val followers = pageUpdates2.subList(i + 1, pageUpdates.size)
//                        val possibles = followers
//                            .filter {
//                                val toMutableList = followers.toMutableList()
//                                toMutableList.remove(it)
//                                toMutableList.add(testVal)
//                                toMutableList.all { mod ->
//                                    index[it] != null
//                                            && index[it]!!.contains(mod)
//                                }
//                            }
//                             val indexOfFirst=followers.indexOf(possibles[0])
////                        if (indexOfFirst != 0) {
//                        pageUpdates2[i] = pageUpdates2[i + 1 + indexOfFirst]
//                        pageUpdates2[i + 1 + indexOfFirst] = testVal
////                        }
//
//                    }
////                if(!(index[testVal] != null && pageUpdates2.subList(i + 1, pageUpdates.size)
////                    .all { index[testVal]!!.contains(it) })) {
////                    pageUpdates2[i] = pageUpdates2[i+1]
////                    pageUpdates2[i+1]=testVal
////                }
//                    true
//                }
//                6518 high

                (0..pageUpdates2.size - 2).all { i ->
                    val testVal = pageUpdates2[i]
                    index[testVal] != null && pageUpdates2.subList(i + 1, pageUpdates2.size)
                        .all { index[testVal]!!.contains(it) }
                }

                pageUpdates2
            }
        return okUpdates.sumOf { it[it.size / 2] }
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
