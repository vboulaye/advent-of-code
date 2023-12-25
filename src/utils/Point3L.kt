package utils

import java.io.Serializable

data class Point3L(val x: Long, val y: Long, val z: Long) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point3L

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.toInt()
        result = 31 * result + y.toInt()
        return result
    }

    operator fun plus(coord: Point3L): Point3L {
        return Point3L(this.x + coord.x, this.y + coord.y, this.z + coord.z)
    }

    operator fun times(distance: Long): Point3L {
        return Point3L(this.x * distance, this.y * distance, this.z * distance)
    }

    fun translate(direction: Point3L): Point3L {
        return Point3L(x + direction.x, y + direction.y, z + direction.z)
    }

    override fun toString(): String {
        return "($x,$y,$z)"
    }

    fun opposite(): Point3L {
        return Point3L(-x, -y, -z)
    }

    fun toPointXY(): PointL {
        return PointL(x,y)
    }

    companion object {
        fun parse(s: String): Point3L {
            val split = s.split(Regex(",\\s*")).map { it.trim() }
            return Point3L(split[0].toLong(), split[1].toLong(), split[2].toLong())
        }
    }


}
