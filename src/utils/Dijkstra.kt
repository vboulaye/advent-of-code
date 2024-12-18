package utils

import java.util.*


fun <P> disktraCompute(
    start: P,
    nextGetter: (P) -> List<Pair<P, Int>>,
): MutableMap<P, Int> {
    return Dijkstra<P, Int>(
        minDistance = 0,
        maxDistance = Int.MAX_VALUE,
        comparator = Int::compareTo,
        distanceAdder = Int::plus,
    )
        .compute(start, nextGetter)
}
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
}

