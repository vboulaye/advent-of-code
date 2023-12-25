package aoc2023.day07

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


var part1: Boolean = true

class Puzzle {

    data class Strength(val strengthType: StrengthType, val highCard: String) : Comparable<Strength> {

        override fun compareTo(other: Strength): Int {
            val compareTo = strengthType.compareTo(other.strengthType)
            if (compareTo != 0) {
                return -compareTo
            }
            if (highCard.equals(    other.highCard)) {
                return 0
            }
            return compare(highCard, other.highCard)
        }

        private fun compare(highCard: String, highCard1: String): Int {
            highCard.zip(highCard1).forEach {
                val firstCardCompare = kindValue(it.first) - kindValue(it.second)
                if (firstCardCompare != 0) {
                    return firstCardCompare
                }
            }
            return 0
//            val firstCardCompare = kindValue(highCard[0]) - kindValue(highCard1[0])
//            if (highCard.length == 1 || firstCardCompare != 0) {
//                return firstCardCompare
//            }
//            return kindValue(highCard[1]) - kindValue(highCard1[1])
        }

        private fun kindValue(c: Char): Int {
            if (c.isDigit()) {
                return c - '0'
            }
            return when (c) {
                'T' -> 10
                'J' -> if (part1) 11 else 0
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw IllegalArgumentException("Unknown card $c")
            }
        }

    }

    data class Game(val hand: String, val bid: Int) : Comparable<Game> {

        val strength: Strength = if (part1) computeStrength(this.hand) else computeStrength2(this.hand)

        private fun computeStrength(hand: String): Strength {
            val groupByCard = hand.groupingBy { it }.eachCount()
            val groupByCount = buildMap<Int, String> {
                groupByCard.forEach { (card, count) ->
                    if (containsKey(count))
                        put(count, get(count) + card)
                    else
                        put(count, card.toString())
                }
            }

            if (groupByCount.containsKey(5)) {
                return Strength(StrengthType.fiveOfAKind, hand)
            }
            if (groupByCount.containsKey(4)) {
                return Strength(StrengthType.fourOfAKind, hand)
            }
            if (groupByCount.containsKey(3) && groupByCount.containsKey(2)) {
                return Strength(StrengthType.fullHouse, hand)
            }
            if (groupByCount.containsKey(3)) {
                return Strength(StrengthType.threeOfAKind, hand)
            }
            if (groupByCount.containsKey(2)) {
                val highCard = groupByCount[2]!!
                if (highCard.length == 2) {
                    return Strength(StrengthType.twoPair, hand)
                }
                return Strength(StrengthType.onePair, hand)
            }
            return Strength(StrengthType.highCard, hand)
        }

        private fun computeStrength2(hand: String): Strength {
            val groupByCard = hand.groupingBy { it }.eachCount()
            if (groupByCard.containsKey('J')) {
                var otherKeys = groupByCard.keys.filter { it != 'J' }
                if(otherKeys.isEmpty()){
                    return Strength(StrengthType.fiveOfAKind, hand)
                }
                val maxStrength = mutableListOf<Strength>()
                hand.forEachIndexed { idx, c ->
                    if (c == 'J') {
                        otherKeys.forEach{o ->
                            val newHand = hand.substring(0, idx) + o + hand.substring(idx + 1)
                            val strength = Strength(computeStrength2(newHand).strengthType, hand)
                            maxStrength.add(strength)
                        }
                    }
                }
                val maxOrNull = maxStrength.maxOrNull()!!
                return maxOrNull

            }
            return computeStrength(hand)
        }


        override fun compareTo(other: Game): Int {
            return strength.compareTo(other.strength)
        }
    }


    fun String.acceptInput() = true


    enum class StrengthType {
        fiveOfAKind,
        fourOfAKind,
        fullHouse,
        threeOfAKind,
        twoPair,
        onePair,
        highCard
    }

    fun String.parseInput(): Game {
        val it = this.split(" ")
        return Game(it[0], it[1].toInt())
    }


    fun clean(input: List<String>): List<Game> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
    }

    val part1ExpectedResult = 6440
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val sorted = input.sorted()
        // 245693161 low
//        246163188
        return sorted
            .foldIndexed(0) { index, acc, game ->
                acc + (index + 1) * game.bid
            }

    }

    val part2ExpectedResult = 5905
    fun part2(rawInput: List<String>): Result {
        part1 = false
        val input = clean(rawInput)
        val sorted = input.sorted()
        // 245693161 low
//        246163188
        return sorted
            .foldIndexed(0) { index, acc, game ->
                acc + (index + 1) * game.bid
            }
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
