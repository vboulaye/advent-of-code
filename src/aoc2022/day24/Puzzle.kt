package aoc2022.day24

import aoc2021.day15.PathPriorityQueue
import aoc2021.day15.WorkPathElement
import utils.Point
import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int


val WALL = '#'

enum class Direction {
    N, S, W, E
}


val cacheBlizzards = mutableMapOf<Set<Blizzard>, Set<Blizzard>>()

data class Expedition(val current: Point, val grid: Grid, val exit: Point)

data class Grid(val blizzards: Set<Blizzard>, val width: Int, val height: Int, val minute: Int) {
    constructor(lines: List<String>) : this(
        buildBlizzards(lines), lines[0].length - 2, lines.size - 2, 0
    ) {

    }

    fun hasBlizzardOrWall(p: Point): Boolean {
        if (p == Point(width - 1, height)) {
            return false
        }
        if (p == Point(0, -1)) {
            return false
        }
        if (p.x < 0) return true
        if (p.y < 0) return true
        if (p.x > width - 1) return true
        if (p.y > height - 1) return true
        return blizzards.any { it.pos == p }
    }

    companion object {
        private fun buildBlizzards(lines: List<String>): Set<Blizzard> {
            val blizzards = mutableListOf<Blizzard>();
            lines.forEachIndexed { y, line ->
                line.forEachIndexed { x, ch ->
                    val pos = Point(x - 1, y - 1)
                    when (ch) {
                        '<' -> blizzards.add(Blizzard(pos, Direction.W))
                        '>' -> blizzards.add(Blizzard(pos, Direction.E))
                        '^' -> blizzards.add(Blizzard(pos, Direction.N))
                        'v' -> blizzards.add(Blizzard(pos, Direction.S))
                    }
                }
            }
            return blizzards.toSet()
        }
    }

    fun modWidth(x: Int): Int {
        return (x + width) % width
    }

    fun modHeight(y: Int): Int {
        return (y + height) % height
    }

    fun moveAround(p: Point, dx: Int, dy: Int): Point {
        return Point(modWidth(p.x + dx), modHeight(p.y + dy))
    }

    fun moveInWalls(p: Point, dx: Int, dy: Int): Point {
        return Point(p.x + dx, p.y + dy)
    }


    fun nextMinute(): Grid {
        val nextBlizzards = cacheBlizzards.computeIfAbsent(blizzards) {
            it.map { blizzard ->
                when (blizzard.dir) {
                    Direction.N -> blizzard.copy(pos = moveAround(blizzard.pos, 0, -1))
                    Direction.S -> blizzard.copy(pos = moveAround(blizzard.pos, 0, +1))
                    Direction.W -> blizzard.copy(pos = moveAround(blizzard.pos, -1, 0))
                    Direction.E -> blizzard.copy(pos = moveAround(blizzard.pos, 1, 0))
                }
            }.toSet()
        }

        return this.copy(
            blizzards = nextBlizzards,
            minute = this.minute + 1
        )
    }

}

data class Blizzard(val pos: Point, val dir: Direction) {

}

class Finder() : FindRelated<Expedition, Pair<Expedition, Expedition>> {

    override fun findRelated(p: WorkPathElement<Expedition>?): List<WorkPathElement<Expedition>> {
        if (p == null) {
            return emptyList()
        }

        val neighbours: MutableList<WorkPathElement<Expedition>> = mutableListOf()

        val expedition = p.element
        val nextMinute = expedition.grid.nextMinute();

        if (!nextMinute.hasBlizzardOrWall(expedition.current)) {
            neighbours.add(
                WorkPathElement.buildNext(
                    expedition.copy(
                        grid = nextMinute, current = expedition.current
                    )
                )
            )
        }

        val moveWest = nextMinute.moveInWalls(expedition.current, -1, 0)
        if (!nextMinute.hasBlizzardOrWall(moveWest)) {
            neighbours.add(
                WorkPathElement.buildNext(
                    expedition.copy(
                        grid = nextMinute, current = moveWest
                    )
                )
            )
        }
        val moveEast = nextMinute.moveInWalls(expedition.current, 1, 0)
        if (!nextMinute.hasBlizzardOrWall(moveEast)) {
            neighbours.add(
                WorkPathElement.buildNext(
                    expedition.copy(
                        grid = nextMinute, current = moveEast
                    )
                )
            )
        }
        val moveNorth = nextMinute.moveInWalls(expedition.current, 0, -1)
        if (!nextMinute.hasBlizzardOrWall(moveNorth)) {
            neighbours.add(
                WorkPathElement.buildNext(
                    expedition.copy(
                        grid = nextMinute, current = moveNorth
                    )
                )
            )
        }
        val moveSouth = nextMinute.moveInWalls(expedition.current, 0, 1)
        if (!nextMinute.hasBlizzardOrWall(moveSouth)) {
            neighbours.add(
                WorkPathElement.buildNext(
                    expedition.copy(
                        grid = nextMinute, current = moveSouth
                    )
                )
            )
        }


//
//        val y = p.element.y
//        val x = p.element.x
//        val neighbours: List<WorkPathElement<Expedition>> = (-1..1).flatMap { dy ->
//            (-1..1).filter { dx ->
//                (abs(dx) + abs(dy) == 1)
//            }
//                .mapNotNull { dx ->
//                    try {
//                        val (x1, y1) = Point(x + dx, y + dy)
//                        val point = Point(x + dx, y + dy)
//                        val workPathElement = WorkPathElement(point)
//                        workPathElement.distance = 1
//                        workPathElement
//                    } catch (e: Exception) {
//                        null
//                    }
//                }
//        }


        return neighbours
    }
}

class Puzzle {
    fun clean(input: List<String>): Expedition {
        val lines = input.filter { line -> true }.map { line -> line }
        return Expedition(
            Point(0, -1), Grid(lines), Point(lines[0].length - 3, lines.size - 2)
        )

    }

    val part1ExpectedResult = 18
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        val pathFinder = PathFinder(Finder())
        val path = pathFinder.findPath(input)
        return path.size - 1
    }

    val part2ExpectedResult = 54
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val path = PathFinder(Finder()).findPath(input)
        val d1 = path.size - 1
        val expedition2 = Expedition(
            input.exit, path.first().element.grid, input.current
        )
        val path2 = PathFinder(Finder()).findPath(
            expedition2
        )
        val d2 = path2.size - 1
        val expedition3 = Expedition(
            input.current, path2.first().element.grid, input.exit
        )
        val path3 = PathFinder(Finder()).findPath(
            expedition3
        )
        val d3 = path3.size - 1

        return d1 + d2 + d3
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


/**
 * interface that must be implemented by callers to PathFinder in order to
 * describe the network For each node the find related shows where to go next
 */
interface FindRelated<E, R> {
    /**
     * return the list of path elements that exist in the network from the node
     * o the path element contains related objects and the relation to gets
     * there
     *
     * @param o
     * @return
     */
    fun findRelated(o: WorkPathElement<E>?): List<WorkPathElement<E>?>?
}

/**
 * utility class used to find the shortest path between 2 nodes inside a network
 * the FindRelated implementation gives a node by node description of the
 * network for one node the findRelated() method returns all related nodes The
 * search of the shortest path is a simplification of the dijkstra algorithm
 */
class PathFinder<E : Expedition, R>(findRelated: FindRelated<E, R>?) {
    /**
     * the FindRelated implementation that describes the network
     */
    private val findRelated: FindRelated<E, R>

    /**
     * constructor, sets the FindRelated implementation
     *
     * @param findRelated
     */
    init {
        requireNotNull(findRelated) { "findRelated implementation must be not null" }
        this.findRelated = findRelated
    }

    /**
     * build the shortest path from one node to another in the network described
     * by the FindRelated implementation
     *
     * @param sourceNode source node object
     * @param targetNode target node object
     * @return the path as a list of nodes
     */
    fun findPath(sourceNode: E): List<WorkPathElement<E>> {
        val remainingNodes = computePath(sourceNode)
        return buildPath(sourceNode, remainingNodes)
    }

//    fun findDistance(sourceNode: E, targetNode: E): Int {
//        val remainingNodes = computePath(sourceNode, targetNode)
//        return remainingNodes.getWorkPathElement(targetNode).distance
//    }

    /**
     * build the set of all nodes in the path and their distance from source the
     * evaluation stops when target is reached by following the shortest path
     *
     * @param sourceNode source of the searched path
     * @param targetNode target of the searched path
     * @return the queue with all evaluated nodes, the path can be build from
     * there
     */
    private fun computePath(sourceNode: E): PathPriorityQueue<E> {
        // set of nodes that are still possible choices for the path
        val remainingNodes = PathPriorityQueue<E>()

        // add source node with a distance of 0
        remainingNodes.getWorkPathElement(sourceNode).distance = 0
        remainingNodes.push(sourceNode)

        // work on the remaining node that is currently seen as the closest to
        // the source
        var closest = remainingNodes.pop()

        // loop until there is no more nodes or if the destination is reached
        while (closest != null && closest.current != closest.exit) {

            println("" + remainingNodes.workPathElements.size + ":" + cacheBlizzards.size)
            // flag the node in order not to evaluate it twice
            val closestNodeWorkPathElement = remainingNodes.getWorkPathElement(closest)
            closestNodeWorkPathElement.isEvaluated = true

            // update the distance of all related nodes of the work node by
            // evaluating the impact of using it as a predecessor
            val related = findRelated.findRelated(closestNodeWorkPathElement)
            if (related != null && !related.isEmpty()) {
                for (relatedPath in related) {
                    processRelatedNode(remainingNodes, closestNodeWorkPathElement, relatedPath)
                } // end for all related nodes
            } // if there exist related nodes
            closest = remainingNodes.pop()
        } // while there are still some nodes to evaluate
        return remainingNodes
    }

    /**
     * build shortest path from calculated nodes path elements
     *
     * @param sourceNode     source node
     * @param targetNode     target node
     * @param remainingNodes Map of evaluated nodes
     * @return List of WorkPathElement from source to target
     */
    private fun buildPath(
        sourceNode: E, remainingNodes: PathPriorityQueue<E>
    ): List<WorkPathElement<E>> {


        val workPathElement = remainingNodes.workPathElements.values.find { it.element.current == it.element.exit }

        // only build the path if the distance if not infinite (a path was  actually found)
        if (workPathElement!!.distance == Int.MAX_VALUE) {
            return emptyList<WorkPathElement<E>>()
        }

        val path = emptyList<WorkPathElement<E>>().toMutableList()

        // we add the destination node and all its predecessors (processing
        // backwards)
        var predecessor: E? = workPathElement?.element

        while (predecessor != null && predecessor != sourceNode) {
            // put the predecessor as the first node
            val predecessorElement = remainingNodes.getWorkPathElement(predecessor)
            path.add(predecessorElement)
            predecessor = predecessorElement.predecessor
        }

        // add the init node
        path.add(remainingNodes.getWorkPathElement(sourceNode))
        return path
    }

    private fun processRelatedNode(
        remainingNodes: PathPriorityQueue<E>,
        closestNodeWorkPathElement: WorkPathElement<E>,
        relatedPath: WorkPathElement<E>?
    ) {
        val relatedNode = relatedPath!!.element
        val closest = closestNodeWorkPathElement.element

        // Do not check items that are already evaluated
        val relatedNodeWorkPathElement = remainingNodes.getWorkPathElement(relatedNode)
        if (!relatedNodeWorkPathElement.isEvaluated) {

            // distance = current distance from source + distance(current note, neighbour)
            val distance = closestNodeWorkPathElement.distance + relatedPath.distance

            // check if the new distance is closer
            val oldDistance = relatedNodeWorkPathElement.distance
            if (oldDistance > distance) {
                // update the path element with ne distance/predecessor info
                relatedNodeWorkPathElement.distance = distance
                relatedNodeWorkPathElement.predecessor = closest

                // re-balance the related Node using the new shortest distance
                // found
                remainingNodes.push(relatedNode)
            }
        }
    }
}

