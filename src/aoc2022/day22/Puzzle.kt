package aoc2022.day22

import utils.readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Int

data class Grid(val lines: List<String>)


interface Move

data class Forward(val count: Int) : Move {

}

class Left : Move {
}

class Right : Move {

}

//or right (>), 1 for down (v), 2 for left (<), and 3 for up (^). The final

data class Vector(val dx: Int, val dy: Int)
enum class Dir(val vector: Vector) {
    R(Vector(1, 0)),
    D(Vector(0, 1)),
    L(Vector(-1, 0)),
    U(Vector(0, -1)),

}

enum class Sides {
    A,
    B,
    C,
    D,
    E,
    F,
}

data class Pos(val x: Int, val y: Int, val dir: Dir) {
    private fun getCol(lines: List<String>) = lines.map {
        if (it.length > x) it[x] else ' '
    }.joinToString("")

    fun next(lines: List<String>): Pos {
        val line = lines[y]
        val col = getCol(lines)
        return Pos(
            (line.length + x + dir.vector.dx) % line.length,
            (col.length + y + dir.vector.dy) % col.length,
            dir
        )
    }

    fun gridValue(lines: List<String>): Char {
        try {
            return lines[y][x]
        } catch (e: IndexOutOfBoundsException) {
            return ' '
        }
    }


    fun nextOnCube(lines: List<String>): Pos {

        //println(this)
        val potentialNext = Pos(
            (x + dir.vector.dx),
            (y + dir.vector.dy),
            dir
        )
        if (potentialNext.gridValue(lines) != ' ') {
            return potentialNext
        }

        if (lines.size == 12) {
            val blocSize = 4
            val colBloc = x / blocSize
            val lineBloc = y / blocSize
            //println("colBloc" + colBloc + "lineBloc" + lineBloc)
            return when (lineBloc) {
                0 -> {
                    when (colBloc) {
                        2 -> {
                            when (dir) {
                                Dir.R -> {
                                    Pos(
                                        4 * blocSize - 1,
                                        3 * blocSize - 1 - y,
                                        Dir.L
                                    )
                                }

                                Dir.L -> {
                                    Pos(
                                        1 * blocSize + y,
                                        1 * blocSize,
                                        Dir.D
                                    )
                                }

                                Dir.U -> {
                                    Pos(
                                        1 * blocSize - 1 - (x % blocSize),
                                        1 * blocSize,
                                        Dir.D
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        else -> TODO("no")
                    }
                }

                1 -> {
                    when (colBloc) {
                        0 -> {
                            when (dir) {
                                Dir.L -> {
                                    Pos(
                                        4 * blocSize - 1 - (y % blocSize),
                                        lines.size - 1,
                                        Dir.U
                                    )
                                }

                                Dir.U -> {
                                    Pos(
                                        3 * blocSize - 1 - x,
                                        0,
                                        Dir.D
                                    )
                                }

                                Dir.D -> {
                                    Pos(
                                        3 * blocSize - 1 - x,
                                        3 * blocSize - 1,
                                        Dir.U
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        1 -> {
                            when (dir) {
                                Dir.U -> {
                                    Pos(
                                        2 * blocSize,
                                        x - blocSize,
                                        Dir.R
                                    )
                                }

                                Dir.D -> {
                                    Pos(
                                        2 * blocSize,
                                        3 * blocSize - 1 - (x % blocSize),
                                        Dir.R
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        2 -> {
                            when (dir) {
                                Dir.R -> {
                                    Pos(
                                        4 * blocSize - 1 - (y % blocSize),
                                        2 * blocSize,
                                        Dir.D
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        else -> TODO("no")
                    }
                }

                2 -> {
                    when (colBloc) {
                        2 -> {
                            when (dir) {
                                Dir.L -> {
                                    Pos(
                                        2 * blocSize - 1 - (y - 2 * blocSize),
                                        2 * blocSize - 1,
                                        Dir.U
                                    )
                                }

                                Dir.D -> {
                                    Pos(
                                        1 * blocSize - 1 - (x - 2 * blocSize),
                                        2 * blocSize - 1,
                                        Dir.U
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        3 -> {
                            when (dir) {
                                Dir.U -> {
                                    Pos(
                                        3 * blocSize - 1,
                                        2 * blocSize - 1 - (x - 3 * blocSize),
                                        Dir.L
                                    )
                                }

                                Dir.D -> {
                                    Pos(
                                        0,
                                        2 * blocSize - 1 - (x - 3 * blocSize),
                                        Dir.R
                                    )
                                }

                                Dir.R -> {
                                    Pos(
                                        3 * blocSize - 1,
                                        1 * blocSize - 1 - (y - 2 * blocSize),
                                        Dir.L
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        else -> TODO("no")
                    }

                }

                else -> TODO("no")
            }
        }



        if (lines.size == 50 * 4) {
            val blocSize = 50
            val colBloc = x / blocSize
            val lineBloc = y / blocSize
            //println("colBloc" + colBloc + "lineBloc" + lineBloc)
            return when (lineBloc) {
                0 -> {
                    when (colBloc) {
                        1 -> {
                            when (dir) {
                                Dir.L -> {
                                    Pos(
                                        0,
                                        3 * blocSize - 1 - y,
                                        Dir.R
                                    )
                                }

                                Dir.U -> {
                                    Pos(
                                        0,
                                        3 * blocSize + x % blocSize,
                                        Dir.R
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        2 -> {
                            when (dir) {
                                Dir.U -> {
                                    Pos(
                                        x % blocSize,
                                        4*blocSize-1,
                                        Dir.U
                                    )
                                }

                                Dir.R -> {
                                    Pos(
                                        2 * blocSize - 1,
                                        3 * blocSize - 1 - y % blocSize,
                                        Dir.L
                                    )
                                }

                                Dir.D -> {
                                    Pos(
                                        2 * blocSize - 1,
                                        1 * blocSize + x % blocSize,
                                        Dir.L
                                    )
                                }

                                else -> TODO("no")
                            }
                        }
                        //72385 too low

                        else -> TODO("no")
                    }
                }

                1 -> {
                    when (colBloc) {
                        1 -> {
                            when (dir) {
                                Dir.L -> {
                                    Pos(
                                        y % blocSize,
                                        2 * blocSize,
                                        Dir.D
                                    )
                                }

                                Dir.R -> {
                                    Pos(
                                        2 * blocSize + y % blocSize,
                                        1 * blocSize - 1,
                                        Dir.U
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        else -> TODO("no")
                    }
                }

                2 -> {
                    when (colBloc) {
                        0 -> {
                            when (dir) {
                                Dir.L -> {
                                    Pos(
                                        1 * blocSize,
                                        1 * blocSize - 1 - y % blocSize,
                                        Dir.R
                                    )
                                }

                                Dir.U -> {
                                    Pos(
                                        1 * blocSize,
                                        1 * blocSize + x,
                                        Dir.R
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        1 -> {
                            when (dir) {
                                Dir.R -> {
                                    Pos(
                                        3 * blocSize - 1,
                                        1 * blocSize - 1 - y % blocSize,
                                        Dir.L
                                    )
                                }

                                Dir.D -> {
                                    Pos(
                                        1 * blocSize - 1,
                                        3 * blocSize + x % blocSize,
                                        Dir.L
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        else -> TODO("no")
                    }
                }

                3 -> {
                    when (colBloc) {
                        0 -> {
                            when (dir) {
                                Dir.L -> {
                                    Pos(
                                        1 * blocSize + y % blocSize,
                                        0,
                                        Dir.D
                                    )
                                }

                                Dir.R -> {
                                    Pos(
                                        1 * blocSize + y % blocSize,
                                        3 * blocSize - 1,
                                        Dir.U
                                    )
                                }

                                Dir.D -> {
                                    Pos(
                                        2 * blocSize + x % blocSize,
                                        0,
                                        Dir.D
                                    )
                                }

                                else -> TODO("no")
                            }
                        }

                        else -> TODO("no")
                    }
                }

                else -> TODO("no")
            }
        }
        // 32318 too low
        TODO("no")
    }
}

val DOT = '.'
val DASH = '#'

data class Ctx(val lines: List<String>, val moves: List<Move>) {

    fun getStart(line: String): Int {
        return line.indexOf(DOT)
    }

    fun applyMove(pos: Pos, move: Move, cube: Boolean): Pos {
        return when {
            move is Right -> {
                val nextDir = when {
                    pos.dir == Dir.L -> Dir.U
                    pos.dir == Dir.U -> Dir.R
                    pos.dir == Dir.R -> Dir.D
                    pos.dir == Dir.D -> Dir.L
                    else -> TODO("no")
                }
                Pos(pos.x, pos.y, nextDir)
            }

            move is Left -> {
                val nextDir = when {
                    pos.dir == Dir.U -> Dir.L
                    pos.dir == Dir.R -> Dir.U
                    pos.dir == Dir.D -> Dir.R
                    pos.dir == Dir.L -> Dir.D
                    else -> TODO("no")
                }
                Pos(pos.x, pos.y, nextDir)
            }

            move is Forward -> {
                var current = pos
                for (i in 1..move.count) {
                    current = nextCell(current) { x: Pos ->
                        if (!cube) {
                            x.next(lines)
                        } else {
                            x.nextOnCube(lines)
                        }
                    }
                }
                current
            }

            else -> TODO("no")
        }
    }

    fun gridValue(current: Pos): Char {
        try {
            return lines[current.y][current.x]
        } catch (e: IndexOutOfBoundsException) {
            return ' '
        }
    }

    private fun nextCell(current: Pos, nextGetter: (Pos) -> Pos): Pos {
        var potentialNext = nextGetter(current)
        while (gridValue(potentialNext) == ' ') {
            potentialNext = nextGetter(potentialNext)
        }
        if (lines[potentialNext.y][potentialNext.x] == '.') {
            return potentialNext
        }
        return current
    }


    private fun getLine(current: Pos) = lines[current.y]
    private fun getCol(current: Pos) = lines.map { if (it.length > current.x) it[current.x] else ' ' }.joinToString("")

    fun move(cube: Boolean): Pos {
        return moves.fold(Pos(getStart(lines.first()), 0, Dir.R)) { pos, move ->
            val applyMove = applyMove(pos, move, cube)
            //println(":"+pos+"+"+move+"=>"+applyMove)
            applyMove
        }
    }
}

class Puzzle {
    fun clean(input: List<String>): Ctx {
        val allLines = input
            .filter { line -> true }
            .map { line -> line }

        var inGrid = true
        val gridInput = allLines.filter {
            if (it.isEmpty()) {
                inGrid = false
            }
            inGrid
        }

        val moveInput = Regex("(\\d+|L|R)").findAll(allLines.last())
            .map {
                when {
                    it.value == "L" -> Left()
                    it.value == "R" -> Right()
                    else -> Forward(it.value.toInt())
                }
            }
            .toList()
        return Ctx(gridInput, moveInput)
    }

    val part1ExpectedResult = 6032
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val final = input.move(false)
        return 1000 * (final.y + 1) + 4 * (final.x + 1) + final.dir.ordinal
    }

    val part2ExpectedResult = 5031
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val final = input.move(true)
        return 1000 * (final.y + 1) + 4 * (final.x + 1) + final.dir.ordinal
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

    runPart("part1", puzzle.part1ExpectedResult, puzzle::part1)
    runPart("part2", puzzle.part2ExpectedResult, puzzle::part2)

}
