package aoc2023.day20

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


class Puzzle {


    fun String.acceptInput() = true

    enum class NodeType {
        broadcaster, conjonction, flipflpop;
    }

    data class Node(val nodeName: String, val nodeType: NodeType, val targets: List<String>) {

    }


    fun String.parseInput(): Pair<String, Node> {
        //
        //&kx -> zs, br, jd, bj, vg
        val nodeDef = this.substringBefore(" -> ")
        val nodeType = when {
            nodeDef == "broadcaster" -> NodeType.broadcaster
            nodeDef.startsWith('&') -> NodeType.conjonction
            nodeDef.startsWith('%') -> NodeType.flipflpop
            else -> throw IllegalArgumentException("Unknown node type: $nodeDef")
        }
        val nodeName = when (nodeType) {
            NodeType.broadcaster -> "broadcaster"
            NodeType.conjonction -> nodeDef.substringAfter('&')
            NodeType.flipflpop -> nodeDef.substringAfter('%')
        }
        val targets = this.substringAfter(" -> ").split(", ")
        return nodeName to Node(nodeName, nodeType, targets)
    }

    fun clean(input: List<String>): Map<String, Node> {
        return input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() }
            .toMap()
    }


    data class Grid(
        val nodesDef: Map<String, Node>,
        val nodesState: Map<String, Boolean> = nodesDef.mapValues { (_, node) -> false },
        val conjonctionStates: Map<String, Map<String, Boolean>> = nodesDef
            .filter { (_, node) -> node.nodeType == NodeType.conjonction }
            .map { (conjKey, node) ->
                conjKey to nodesDef.filter { it.value.targets.contains(conjKey) }.map { it.key to false }.toMap()
            }
            .toMap(),
        val highPulses: Int = 0,
        val lowPulses: Int = 0,
        val outputPulses: Int = 0,
    ) {

        data class Action(val key: String, val state: Boolean, val previousKey: String)

        fun flipValues(pulse: Boolean): Grid {
            val newState = nodesState.toMutableMap()
            val newConjonctionState = conjonctionStates.toMutableMap()

            val actions = mutableListOf(Action("broadcaster", pulse, "button"))
            var newHighPulses = this.highPulses
            var newLowPulses = this.lowPulses
            var outputPulses = 0
            while (actions.isNotEmpty()) {
                val (key, state, previous) = actions.removeFirst()
                if (state) {
                    newHighPulses++
                } else {
                    newLowPulses++
                }

                if (key == "output" || key == "rx") {
                    if (!state)
                        outputPulses++
                    continue
                }
                if (key == "output" || nodesDef[key] == null) {
                    continue
                }
                val def = nodesDef[key]!!
                when (def.nodeType) {
                    NodeType.broadcaster -> {
                        actions.addAll(def.targets.map { Action(it, state, key) })
                    }

                    NodeType.flipflpop -> {
                        if (!state) {
                            newState[key] = !newState[key]!!
                            actions.addAll(def.targets.map { Action(it, newState[key]!!, key) })
                        }
                    }

                    NodeType.conjonction -> {
                        newConjonctionState[key] = newConjonctionState[key]!!.mapValues { (k, v) ->
                            if (k == previous) state else v
                        }
                        val conjState = newConjonctionState[key]!!.values.all { it }
                        actions.addAll(def.targets.map { Action(it, !conjState, key) })
                    }

                }
            }
            return Grid(nodesDef, newState, newConjonctionState, newHighPulses, newLowPulses, outputPulses)

        }


    }


    val part1ExpectedResult = 11687500
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val initialState = Grid(input)
        var newState = initialState
        var counter = 0
//        while (counter == 0 || (initialState.nodesState != newState.nodesState)) {
//            counter++
//            newState = newState.flipValues(false)
//            println("xxxxxxxxxxxxxxxxx "+ counter)
//            println(initialState.nodesState)
//            println(newState.nodesState)
//        }
//
//        return newState.highPulses * 1000 / counter * newState.lowPulses * 1000 / counter
        while (counter < 1000) {
            counter++
            newState = newState.flipValues(false)
//            println("xxxxxxxxxxxxxxxxx " + counter)
//            println(initialState.nodesState)
        }
        println(newState.nodesState)

        return newState.highPulses * newState.lowPulses
    }

    val part2ExpectedResult = 1
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        var newState = Grid(input)
        var counter = 0
        val grids = mutableSetOf(newState.nodesState)
        while (newState.outputPulses != 1) {
            counter++
            newState = newState.flipValues(false)
            if (counter%1000000==0) {
                println(" " + counter + " > " + newState.outputPulses)
            }
            if (!grids.add(newState.nodesState)) {
                throw IllegalStateException("Loop detected")
            }
            println(newState.nodesState.filter { it.value }.map { it.key })
        }

//        return newState.highPulses  * newState.lowPulses
        return counter
    }

}


@OptIn(
    ExperimentalTime::
    class
)

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
