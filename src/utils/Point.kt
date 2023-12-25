package utils

import java.io.Serializable

data class Point(val x: Int, val y: Int) :Serializable{
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

}
