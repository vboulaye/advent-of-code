package utils

import java.io.Serializable

data class Point(val x: Int, val y: Int) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    operator fun plus(coord: Point): Point {
        return Point(this.x + coord.x, this.y + coord.y)
    }

    operator fun times(distance: Int): Point {
        return Point(this.x * distance, this.y * distance)
    }

    override fun toString(): String {
        return "($x,$y)"
    }

    operator fun minus(coord: Point): Point {
        return Point(this.x - coord.x, this.y - coord.y)
    }

    fun opposite(): Point {
        return Point(-x, -y)
    }

    fun neighbours(): List<Point> {
    return    listOf(
            this+Point(0,1),
            this+Point(0,-1),
            this+Point(1,0),
            this+Point(-1,0),
            )
    }

}

fun <E> List<List<E>>.containsPoint(it: Point): Boolean {
    return this.indices.contains(it.y)
            && it.x in this[0].indices
}

fun <E> List<List<E>>.getPoint(it: Point, default: E? = null): E {
    if (!this.containsPoint(it)) {
        if (default != null) return default
        throw IllegalStateException("point $it is out of bounds")
    }
    return this[it.y][it.x]
}

fun <E> List<List<E>>.browsePoints(): List<Pair<Point,E>> {
    return this.indices
        .flatMap { y ->
            this[y].indices.map { x ->
                Point(x, y) to this[y][x]
            }
        }
}

