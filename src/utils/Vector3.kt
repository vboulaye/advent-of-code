package utils

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


data class Vector3(val from: Point3, val to: Point3) {
    fun manhatanlength(): Int = abs(from.x - to.x) + abs(from.y - to.y) + abs(from.z - to.z)
    fun isVertical(): Boolean = from.z == to.z
    fun isDiagonal(): Boolean = abs(from.x - to.x) == abs(from.y - to.y) && abs(from.x - to.x) == abs(from.z - to.z)

    fun pointMinZ() = if (from.z == minZ()) from else to
    fun pointMaxZ() = if (from.z == maxZ()) from else to
    fun minZ() = min(from.z, to.z)
    fun maxZ() = max(from.z, to.z)

    /**
     * the list of points in the vector, assuming they go in one direction only
     */
    fun pointsStraight(): List<Point3> {
        if (from.x != to.x) {
            return (min(from.x, to.x)..max(from.x, to.x)).map { x -> Point3(x, from.y, from.z) }
        }
        if (from.y != to.y) {
            return (min(from.y, to.y)..max(from.y, to.y)).map { y -> Point3(from.x, y, from.z) }
        }
        if (from.z != to.z) {
            return (min(from.z, to.z)..max(from.z, to.z)).map { z -> Point3(from.x, from.y, z) }
        }
        return listOf(from)
    }

    override fun toString(): String {
        return "($from->$to)"
    }

    fun translate(direction: Point3): Vector3 {
        return Vector3(from.translate(direction), to.translate(direction))
    }

    fun intervalX(translate:Int=0) = (min(from.x,to.x)+translate .. max(from.x,to.x)+translate )
    fun intervalY(translate:Int=0) = (min(from.y,to.y)+translate .. max(from.y,to.y)+translate )
    fun intervalZ(translate:Int=0) = (min(from.z,to.z)+translate .. max(from.z,to.z)+translate )
}
