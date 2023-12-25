package aoc2023.day05

import utils.min
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


data class Garden(val seeds: List<Long>, val maps: MutableList<Map>)
data class Map(val name: String, val translations: List<Translation>)
data class Translation(val destination: Long, val source: Long, val range: Long)

class Puzzle {
    fun clean(input: List<String>): Garden {
        val seeds = input[0].substringAfter(":").split(Regex(" +"))
            .filter { !it.trim().isEmpty() }
            .map { it.trim().toLong() }
        var title = ""
        val maps = mutableListOf<Map>()
        val translations = mutableListOf<Translation>()
        input
            .filterIndexed { idx, line -> idx >= 2 }
            .forEach { line ->
                if (line.endsWith(" map:")) {
                    title = line.substringBefore(" map:")
                } else if (line.trim() == "") {
                    maps.add(Map(title, translations.toList()))
                    translations.clear()
                    title = ""
                } else {
                    val (destination, source, range) = line.split(Regex(" +"))
                    translations.add(Translation(destination.toLong(), source.toLong(), range.toLong()))
                }
            }

        maps.add(Map(title, translations.toList()))

        return Garden(seeds, maps)
    }

    val part1ExpectedResult = 35L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        return input.maps.fold(input.seeds) { acc, map ->
            println(">>>>>map: " + map.name)
            println("acc2: " + acc)

            val processed = mutableListOf<Boolean>()
            acc.forEach { processed.add(false) }
            map.translations.fold(acc) { acc2, translation ->
                println("acc2: " + acc2)
                println("translation: " + translation)
                val (destination, source, range) = translation
                val translated = acc2.mapIndexed { idx, value ->
                    if (!processed[idx]
                        && value in source..(source + range)
                    ) {
                        processed[idx] = true
                        destination + (value - source)
                    } else {
                        value
                    }
                }
                println("translated: " + translated)
                translated
            }
        }.min()
    }

    val part2ExpectedResult = 46L


    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val initial: List<LongRange> = input.seeds.windowed(2, 2)
            .map { (it[0]..it[0] + it[1] - 1) }

        var min = Long.MAX_VALUE
        initial.forEach { testRange ->
            println("range" + testRange)
            testRange.forEach { seed ->

                val currentLocation = input.maps.fold(seed) { acc, map ->
//                    println(">>>>>map: " + map.name)
//                    println("acc2: " + acc)

                    var translated = acc
                    for (translation in map.translations) {
                        val (destination, source, range) = translation
                        if (acc in source..(source + range)) {
                            translated = destination + (acc - source)
                            break
                        }
                    }
                    translated

                }

                if (currentLocation < min) {
                    min = currentLocation
                    println("min: " + min)
                }
            }

        }
        return min

//        data class RangeProcess(val range: LongRange, val processed: Boolean)
//
//        return input.maps.fold(initial) { acc, map ->
//            println(">>>>>map: " + map.name)
//            println("acc2: " + acc)
//
//            val ranges = mutableListOf<RangeProcess>()
//            acc.forEach { ranges.add(RangeProcess(it, false)) }
//
//            map.translations.fold(ranges) { acc2, translation ->
//                println("acc2: " + acc2)
//                println("translation: " + translation)
//                val (destination, source, range) = translation
//
//                val translated = mutableListOf<RangeProcess>()
//                acc2.forEachIndexed { idx, value ->
//                    val rangeProcess = value
//                    val sourceRange = source until (source + range)
//                    if (!rangeProcess.processed
//                        && (rangeProcess.range.endInclusive in sourceRange
//                                || rangeProcess.range.start in sourceRange
//                                )
//                    ) {
//                        val mappedRange = rangeProcess.range.intersect(sourceRange)
//                        if (mappedRange.min() > sourceRange.start) {
//                            translated.add(RangeProcess((source until sourceRange.start), false))
//                        }
//                        if (mappedRange.max() > sourceRange.endExclusive) {
//                            translated.add(RangeProcess((source until sourceRange.start), false))
//                        }
//                        translated.add(RangeProcess(destination + (value - source), true))
//                    } else {
//                        translated.add(rangeProcess)
//                    }
//                }
//                println("translated: " + translated)
//                translated
//            }.map { it.range }
//
////            map.translations.fold(ranges) { acc2, translation ->
////                println("acc2: " + acc2)
////                println("translation: " + translation)
////                val (destination, source, range) = translation
////
////                val translated = mutableListOf<RangeProcess>()
//////                acc2.forEachIndexed { idx, value ->
//////                    val rangeProcess = value
//////                    if (!rangeProcess.processed
//////                        && (rangeProcess.range.endInclusive in source..(source + range)
//////                                || rangeProcess.range.start in source..(source + range)
//////                                )
//////                    ) {
//////                        translated.add(RangeProcess(destination + (value - source), true))
//////                    } else {
//////                        translated.add(rangeProcess)
//////                    }
//////                }
////                println("translated: " + translated)
////                translated
////            }
//        }.map { it.start }.min()
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
