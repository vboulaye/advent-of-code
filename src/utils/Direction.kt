package utils

enum class Direction {

    U,
    D,
    L,
    R;

    fun coord(): Point {
        return when (this) {
            U -> Point(0, -1)
            D -> Point(0, 1)
            L -> Point(-1, 0)
            R -> Point(1, 0)
        }
    }

    fun opposite(): Direction {
        return when (this) {
            U -> D
            D -> U
            L -> R
            R -> L
        }
    }
}