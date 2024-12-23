package utils

import java.util.*


fun <P> disktraCompute(
    start: P,
    nextGetter: (P) -> List<Pair<P, Int>>,
): MutableMap<P, Int> {
    return initDijkstra<P>()
        .compute(start, nextGetter)
}

fun <P> initDijkstra() = Dijkstra<P, Int>(
    minDistance = 0,
    maxDistance = Int.MAX_VALUE,
    comparator = Int::compareTo,
    distanceAdder = Int::plus,
)

fun <P> disktraComputeLong(
    start: P,
    nextGetter: (P) -> List<Pair<P, Long>>,
): MutableMap<P, Long> {
    return Dijkstra<P, Long>(
        minDistance = 0,
        maxDistance = Long.MAX_VALUE,
        comparator = Long::compareTo,
        distanceAdder = Long::plus,
    )
        .compute(start, nextGetter)
}

fun <P> disktraComputeDouble(
    start: P,
    nextGetter: (P) -> List<Pair<P, Double>>,
): MutableMap<P, Double> {
    return Dijkstra<P, Double>(
        minDistance = 0.0,
        maxDistance = Double.MAX_VALUE,
        comparator = Double::compareTo,
        distanceAdder = Double::plus,
    )
        .compute(start, nextGetter)
}


class Dijkstra<DijkstraPoint, DijkstraDistance>(
    val minDistance: DijkstraDistance,
    val maxDistance: DijkstraDistance,
    val comparator: Comparator<DijkstraDistance>,
    val distanceAdder: (DijkstraDistance, DijkstraDistance) -> DijkstraDistance
) {

    fun compute(
        start: DijkstraPoint,
        nextGetter: (DijkstraPoint) -> List<Pair<DijkstraPoint, DijkstraDistance>>
    ): MutableMap<DijkstraPoint, DijkstraDistance> {
        val distances = mutableMapOf<DijkstraPoint, DijkstraDistance>().withDefault { this.maxDistance }
        val priorityQueue =
            PriorityQueue<Pair<DijkstraPoint, DijkstraDistance>> { a, b -> comparator.compare(a.second, b.second) }
        val visited = mutableSetOf<Pair<DijkstraPoint, DijkstraDistance>>()

        priorityQueue.add(start to this.minDistance)
        distances[start] = this.minDistance

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (visited.add(node to currentDist)) {
                nextGetter(node).forEach { (adjacent, weight) ->
                    val totalDist = distanceAdder(currentDist, weight)
                    if (comparator.compare(totalDist, distances.getValue(adjacent)) < 0) {
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)
                    }
                }
            }
        }
        return distances
    }


    fun computePath(
        start: DijkstraPoint,
        end: DijkstraPoint,
        nextGetter: (DijkstraPoint, MutableMap<DijkstraPoint, DijkstraPoint>) -> List<Pair<DijkstraPoint, DijkstraDistance>>
    ): Pair<List<DijkstraPoint>, DijkstraDistance & Any> {
        val distances = mutableMapOf<DijkstraPoint, DijkstraDistance>().withDefault { this.maxDistance }
        val priorityQueue =
            PriorityQueue<Pair<DijkstraPoint, DijkstraDistance>> { a, b -> comparator.compare(a.second, b.second) }
        val visited = mutableSetOf<Pair<DijkstraPoint, DijkstraDistance>>()
        val cameFrom = mutableMapOf<DijkstraPoint, DijkstraPoint>()  // Used to generate path by back tracking


        priorityQueue.add(start to this.minDistance)
        distances[start] = this.minDistance

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (node == end) {
                // Backtrack to generate the most efficient path
                val path = generatePath(node, cameFrom)
                return Pair(path, distances.getValue(end)!!)
            }
            if (visited.add(node to currentDist)) {
                nextGetter(node, cameFrom).forEach { (adjacent, weight) ->
                    val totalDist = distanceAdder(currentDist, weight)
                    if (comparator.compare(totalDist, distances.getValue(adjacent)) < 0) {

                        cameFrom.put(adjacent, node)
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)
                    }
                }
            }
        }
        throw IllegalArgumentException("No Path from Start $start to Finish $end")
    }

    fun generatePath(currentPos: DijkstraPoint, cameFrom: Map<DijkstraPoint, DijkstraPoint>): List<DijkstraPoint> {
        val path = mutableListOf(currentPos)
        var current = currentPos
        while (cameFrom.containsKey(current)) {
            current = cameFrom.getValue(current)
            path.add(0, current)
        }
        return path.toList()
    }

    fun computePaths(
        start: DijkstraPoint,
        end: DijkstraPoint,
        nextGetter: (DijkstraPoint, MutableMap<DijkstraPoint, List<DijkstraPoint>>) -> List<Pair<DijkstraPoint, DijkstraDistance>>
    ): Pair<List<List<DijkstraPoint>>, DijkstraDistance> {
        val distances = mutableMapOf<DijkstraPoint, DijkstraDistance>().withDefault { this.maxDistance }
        val priorityQueue =
            PriorityQueue<Pair<DijkstraPoint, DijkstraDistance>> { a, b -> comparator.compare(a.second, b.second) }
        val visited = mutableSetOf<Pair<DijkstraPoint, DijkstraDistance>>()
        val cameFrom = mutableMapOf<DijkstraPoint, List<DijkstraPoint>>()  // Used to generate path by back tracking


        priorityQueue.add(start to this.minDistance)
        distances[start] = this.minDistance

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (node == end) {
                // Backtrack to generate the most efficient path
                val paths = generatePaths(node, cameFrom)
                return Pair(paths, distances.getValue(end)!!)
            }
            if (visited.add(node to currentDist)) {
                nextGetter(node, cameFrom).forEach { (adjacent, weight) ->
                    val totalDist = distanceAdder(currentDist, weight)
                    val compare = comparator.compare(totalDist, distances.getValue(adjacent))
                    if (compare < 0) {

                        cameFrom.put(adjacent, listOf(node))
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)
                    } else if (compare == 0) {
                        val previous = cameFrom.get(adjacent)!!.toMutableList()
                        previous.add(node)
                        cameFrom.put(adjacent, previous)

                    }
                }
            }
        }
        throw IllegalArgumentException("No Path from Start $start to Finish $end")
    }

    fun generatePaths(
        currentPos: DijkstraPoint,
        cameFrom: Map<DijkstraPoint, List<DijkstraPoint>>
    ): List<List<DijkstraPoint>> {
        if (!cameFrom.containsKey(currentPos)) {
            return listOf(listOf(currentPos))
        }
        val previousList = cameFrom.getValue(currentPos)
        var paths = previousList.flatMap { previous ->
            generatePaths(previous, cameFrom)
                .map { previousPaths ->
                    previousPaths + listOf(currentPos)
                }
        }
        return paths
    }
}

