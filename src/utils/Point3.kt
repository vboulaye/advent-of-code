package utils

import java.io.Serializable

data class Point3(val x: Int, val y: Int, val z: Int) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point3

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    operator fun plus(coord: Point3): Point3 {
        return Point3(this.x + coord.x, this.y + coord.y, this.z + coord.z)
    }

    operator fun times(distance: Int): Point3 {
        return Point3(this.x * distance, this.y * distance, this.z * distance)
    }

    fun translate(direction: Point3): Point3 {
        return Point3(x + direction.x, y + direction.y, z + direction.z)
    }

    override fun toString(): String {
        return "($x,$y,$z)"
    }

    fun opposite(): Point3 {
        return Point3(-x, -y, -z)
    }

    fun toPointXY(): Point {
        return Point(x,y)
    }

    companion object {
        fun parse(s: String): Point3 {
            val split = s.split(Regex(",\\s*"))
            return Point3(split[0].toInt(), split[1].toInt(), split[2].toInt())
        }
    }


}
