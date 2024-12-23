package aoc2024.day23

import utils.containsPoint
import utils.initDijkstra
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = String


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): String {
        return this
    }

    fun clean(input: List<String>): List<List<String>> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.split("-") }
    }

    val part1ExpectedResult = "7"
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val links = input
            .flatMap { listOf(it[0] to it[1], it[1] to it[0]) }
            .groupBy({ it.first }, { it.second })
        val tComputers = links.keys.filter { it.startsWith("t") }.distinct().sorted()

        val groups = tComputers.flatMap {
            getConnected(it, links.toMutableMap())
        }.distinct()
        return groups.count().toString()
    }

    private fun getConnected(computer: String, links: MutableMap<String, List<String>>): List<List<String>> {
        val next = links[computer]!!
        val map = next.flatMap { n ->
            val next2: List<List<String>> = links[n]!!
                .filter { links[it]!!.contains(computer) }
                .map { listOf(n, it) }
            next2
        }.map { listOf(computer) + it }
            .map { it.sorted() }
            .distinct()

        return map
    }

    private fun getConnected2(computer: String, links: MutableMap<String, List<String>>): List<String> {
        val connections = links.remove(computer)!!;
//        connections.forEach { nextComp ->
//            val l2 = links.get(nextComp)!!
//            links[nextComp] = l2.filter { it != computer }
//        }
        val d = initDijkstra<String>()

        val pa: List<List<String>> = connections.mapNotNull { nextComputer ->
            try {
                val l2 = links.get(nextComputer)!!
                links[nextComputer] = l2.filter { it != computer }

                val (path, i) = d.computePath(nextComputer, computer, { p, m ->
                    links[p]!!.map { it to 1 }
                })
                links[nextComputer] = l2
                path
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        return pa[0]

//        if (connections == null) {
//            return emptyList()
//        }
//        val next: List<String> = connections.flatMap { getConnected(it, links) }
//        return connections + next
    }

    val part2ExpectedResult = "co,de,ka,ta"
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val links = input
            .flatMap { listOf(it[0] to it[1], it[1] to it[0]) }
            .groupBy({ it.first }, { it.second })
            .entries
            .map { (k,v)-> k to v.toSet() }
            .toMap()
            .toMutableMap()
        val tComputers = links.keys.distinct().sorted()
        val groups = tComputers.mapIndexed { i,stqrt->
            println(i)
            val connected3 = getConnected3(stqrt, links.toMutableMap(), setOf(stqrt))
            connected3.forEach { conn ->
                conn.forEach {
                    links[it] =  links[it]!!.filter { !conn.contains(it) }.toSet()
                }
            }
            val maxBy = connected3
                .map { it.sorted().joinToString(",") }
                .maxBy { it.length }

            println(maxBy)
            maxBy
        }.distinct()
        return groups.maxBy { it.length }
    }
//aa, st, rv, ec, aw, tw, bf, sx, oi, zw, ex, vr
//aa,st,rv,ec,aw,sx,oi,zw,jc,vr,ex,bf
//aa,aw,bf,ec,ex,oi,rv,st,sx,tw,vr,zw
    private fun getConnected3(
        computer: String,
        links: MutableMap<String, Collection<String>>,
        targetLinks: Set<String>
    ): List<Set<String>> {

        val next = links[computer]!!


        val map = next

            .filter { !targetLinks.contains(it)  }
            .filter {  val strings = links[it]!!
                val containsAll = strings.containsAll(targetLinks)
                if(!containsAll) {
                    links[computer]= links[computer]!!.filter {  a-> a!=it }
                    links[it]= links[it]!!.filter {  a-> a!=computer }
                }
                containsAll
            }
            .flatMap { getConnected3(it, links, targetLinks + listOf(it)) }
            .map { it.toSet() }
            .distinct()
//        println("computer"+computer+" targetLikns"+targetLinks+" map "+map)
        if (map.isEmpty()) {
            return listOf(targetLinks)
        }

        return map
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
