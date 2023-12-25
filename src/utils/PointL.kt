package utils

import java.io.Serializable

data class PointL(val x: Long, val y: Long) :Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PointL

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.toInt()
        result = 31 * result + y.toInt()
        return result
    }

    operator fun plus(coord: PointL): PointL {
        return PointL(this.x + coord.x, this.y + coord.y)
    }

    operator fun times(distance: Long): PointL {
        return PointL(this.x * distance, this.y * distance)
    }

    override fun toString(): String {
        return "($x,$y)"
    }

    operator fun minus(coord: PointL): PointL {
        return PointL(this.x - coord.x, this.y - coord.y)
    }

    fun opposite(): PointL {
        return PointL(-x, -y)
    }

}
