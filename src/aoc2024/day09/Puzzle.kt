package aoc2024.day09

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): List<Int> {
        return this.toList().map { it.code - '0'.code }
    }

    fun clean(input: List<String>): List<List<Int>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }

    }

    val part1ExpectedResult = 1928L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val line = input[0]
        val decompressed = line
            .flatMapIndexed { index, value ->
                if (index % 2 == 0) {
                    (0..value - 1).map { index / 2 }
                } else {
                    (0..value - 1).map { null }
                }
            }
            .toMutableList()

        var endCounter = decompressed.size - 1
        decompressed.forEachIndexed { index, value ->
            if (index >= endCounter) return@forEachIndexed
            if (value == null) {
                while (decompressed[endCounter] == null) {
                    endCounter--
                }
                val i = decompressed[endCounter]
                decompressed[index] = i
                decompressed[endCounter] = null
                endCounter--
            }
        }
//        println(decompressed.joinToString(""))
        return decompressed.mapIndexed { index, i ->
            when (i) {
                null -> 0L
                else -> (index * i).toLong()
            }
        }.sum(); ///decompressed.count ()
    }

    val part2ExpectedResult = 2858L
    fun part2(rawInput: List<String>): Result {
//        val input = clean(rawInput)
//        val line = input[0]
//        val joinToString = line
//            .flatMapIndexed { index, value ->
//                if (index % 2 == 0) {
//                    (0..value - 1).map { index / 2 }
//                } else {
//                    (0..value - 1).map { null }
//                }
//            }
//            .joinToString("") { if (it == null) "." else it.toString() }
//        var decompressed = StringBuffer(joinToString)
//
//        var endCounter = decompressed.length - 1
//
//        while (endCounter > 0) {
//            var index = endCounter
//            while (decompressed[index] == decompressed[endCounter]&& index>0) {
//                index--
//            }
//            if (decompressed[endCounter] != '.') {
//                val length = endCounter - index
//                // build a string repeating '.' chars 'length' times
//                val emptyBloc = (0..length - 1).map { '.' }.joinToString("")
//
//                val indexOf = decompressed.indexOf(emptyBloc)
//                if (indexOf >= 0 && indexOf < index) {
//                    decompressed = decompressed.replace(
//                        indexOf,
//                        indexOf + length,
//                        decompressed.substring(index + 1, endCounter + 1)
//                    )
//                    decompressed = decompressed.replace(index + 1, index + 1 + length, emptyBloc)
//                }
//            }
//            endCounter = index
//        }
//
//
//
//        return decompressed.mapIndexed { index, i ->
//            when (i) {
//                '.' -> 0L
//                else -> (index * (i.code - '0'.code)).toLong()
//            }
//        }.sum(); ///decompressed.count ()

        val input = clean(rawInput)
        val line = input[0]
        val decompressed = line
            .mapIndexed() { index, value ->
                if (index % 2 == 0) {
                    (0..value - 1).map { index / 2 }
                } else {
                    (0..value - 1).map { null }
                }
            }
            .filter { !it.isEmpty() }
            .toMutableList()

        var endCounter = decompressed.size - 1

        while (endCounter > 0) {
            var index = endCounter
            val bloc = decompressed[endCounter]
            if (bloc[0] != null) {
                val length = endCounter - index
                // build a string repeating '.' chars 'length' times

                var indexOf = 0
                while (indexOf < endCounter
                    &&(decompressed[indexOf][0] != null
                            || decompressed[indexOf].size < bloc.size)
                ) {
                    indexOf++
                }
                if (indexOf < endCounter
                    && decompressed[indexOf][0] == null
                    && decompressed[indexOf].size >= bloc.size
                ) {
                    if (decompressed[indexOf].size == bloc.size) {
                        decompressed[endCounter] = decompressed[indexOf]
                        decompressed[indexOf] = bloc
                    } else {
                        decompressed[endCounter] = decompressed[indexOf].subList(0, bloc.size)
                        val remaining = decompressed[indexOf].subList(bloc.size, decompressed[indexOf].size)
                        decompressed.set(indexOf, bloc)
                        decompressed.add(
                            indexOf + 1,
                            remaining
                        )
                        endCounter++
                    }
                }
            }


            endCounter--
        }
        println(decompressed.flatMap { it }.joinToString(""))
        return decompressed
            .flatMap { it }
            .mapIndexed { index, i ->
                when (i) {
                    null -> 0L
                    else -> (index * i).toLong()
                }
            }.sum(); ///decompressed.count ()
//        decompressed.indices.reversed()
//            .forEach { endCounter->
////            if (index>=endCounter) return@forEach
//            val value=decompressed[endCounter]
//            if (value.any{it!= null} ) {
//                var index=0
//                val i=decompressed[endCounter]
//                val s=i.size
//                while(decompressed[index][0]!=null
//                    && decompressed[index].size>=s) {
//                    index++
//                }
//                decompressed[index] = i
//                decompressed[endCounter]= i.map { null }
//            }
//        }
//        println(decompressed.joinToString (""  ))
//        return decompressed
//            .flatMap { it }
//            .mapIndexed { index, i -> when(i){
//                null -> 0L
//                else ->  (index*i).toLong()
//            } }.sum(); ///decompressed.count ()
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
