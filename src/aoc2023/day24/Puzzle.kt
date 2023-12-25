package aoc2023.day24

import com.microsoft.z3.Context
import com.microsoft.z3.Solver
import utils.PointL
import utils.Point3L
import utils.readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Result = Long


class Puzzle {


    fun String.acceptInput() = true

    fun String.parseInput(): Pair<Point3L, Point3L> {
        val split = this.split(" @ ")
        return Point3L.parse(split[0]) to Point3L.parse(split[1])
    }

    fun clean(input: List<String>): Input {
        return Input(input
            .filter { line -> line.acceptInput() }
            .map { line -> line.parseInput() })
    }

    data class Input(val map: List<Pair<Point3L, Point3L>>) {
        fun countIntersections(start: Long, end: Long): Result {
            return map.mapIndexed { idx, stone ->
                (idx + 1..map.lastIndex
                        ).map { otherIdx -> map[otherIdx] }
                    .count { otherStone ->
                        val vector = stone.second.toPointXY()
                        val otherVector = otherStone.second.toPointXY()
                        if (vector.pente() == otherVector.pente()) {
                            return@count false;
                        }
//                        if (vector.pente() == otherVector.pente()) {
//                            return@count false;
//                        }
                        //  Y= dy1/dx1 *X + (y1 -dy1/dx1*x1)
                        //  Y= dy2/dx2 *X + (y2 -dy2/dx2*x2)
                        // Y = y1 + t*dy1
                        // X = x1 + t*dx1
                        // X =  ( (y2 -dy2/dx2*x2)-(y1 -dy1/dx1*x1) ) / (dy1/dx1-dy2/dx2 )
                        val intersectionX = (a(otherVector, otherStone.first) - a(
                            vector,
                            stone.first
                        )) / (vector.pente() - otherVector.pente())
                        val intersectionY =
                            vector.pente() * intersectionX.toDouble() + stone.first.y.toDouble() - vector.pente() * stone.first.x.toDouble()
                        val intersectionYD2 =
                            otherVector.pente() * intersectionX.toDouble() + otherStone.first.y.toDouble() - otherVector.pente() * otherStone.first.x.toDouble()
                        val t1 = if (vector.x != 0L) (intersectionX - stone.first.x) / vector.x else (intersectionY - stone.first.y) / vector.y
                        val t2 = if (otherVector.x != 0L) (intersectionX - otherStone.first.x) / otherVector.x else (intersectionY - otherStone.first.y) / otherVector.y
                        val result = t1 > 0 &&t2 > 0 && min(intersectionX, intersectionY) >= start && max(
                            intersectionX,
                            intersectionY
                        ) <= end
                        result
                    }.toLong()
            }
                .sum()
        }

        private fun a(otherVector: PointL, position: Point3L): Double {
            //y1 -dy1/dx1*x1
            return position.y.toDouble() - otherVector.pente() * position.x.toDouble()
        }

    }

    val part1ExpectedResult = 2L
    fun part1(rawInput: List<String>): Result {
        val input = clean(rawInput)

        if (input.map.size<100)
        return input.countIntersections(7L, 27L)
        return input.countIntersections(200000000000000L, 400000000000000L)
    }

    val part2ExpectedResult = 47L
    fun part2(rawInput: List<String>): Result {
        val input = clean(rawInput)
        val cfg = HashMap<String, String>()
        cfg["model"] = "true"
        val ctx = Context(cfg)
        // Create solver and add equation
        val solver: Solver = ctx.mkSolver()

        val vX = ctx.mkRealConst("X" );
        val vDX = ctx.mkRealConst("dX" );
        val vY = ctx.mkRealConst("Y" );
        val vDY = ctx.mkRealConst("dY" );
        val vZ = ctx.mkRealConst("Z" );
        val vDZ = ctx.mkRealConst("dZ" );
        input.map.mapIndexed{ i, stone ->
            val tk = ctx.mkRealConst("tk" + i);
            solver.add(ctx.mkGt(tk,ctx.mkReal(0)));

            val left = ctx.mkAdd(ctx.mkMul(tk, ctx.mkReal(stone.second.x)), ctx.mkReal(stone.first.x))
            val right = ctx.mkAdd(ctx.mkMul(tk, vDX), vX)
            solver.add(ctx.mkGt(tk,ctx.mkReal(0)));
            solver.add(ctx.mkEq(left,right));

            val leftY = ctx.mkAdd(ctx.mkMul(tk, ctx.mkReal(stone.second.y)), ctx.mkReal(stone.first.y))
            val rightY = ctx.mkAdd(ctx.mkMul(tk, vDY), vY)
            solver.add(ctx.mkEq(leftY,rightY));

            val leftZ = ctx.mkAdd(ctx.mkMul(tk, ctx.mkReal(stone.second.z)), ctx.mkReal(stone.first.z))
            val rightZ = ctx.mkAdd(ctx.mkMul(tk, vDZ), vZ)
            solver.add(ctx.mkEq(leftZ,rightZ));

        }


        // Check if the equation is solvable and print the solution
        when (solver.check()) {
            com.microsoft.z3.Status.SATISFIABLE -> {
                val model = solver.model
                val constX = model.getConstInterp(vX)
                val constDX = model.getConstInterp(vDX)
                val constY = model.getConstInterp(vY)
                val constDY = model.getConstInterp(vDY)
                val constZ = model.getConstInterp(vZ)
                val constDZ = model.getConstInterp(vDZ)
//                val solution = model.getConstInterp(x1)
                println("Solution: $constX $constY $constZ $constDX $constDY $constDZ")
                return constX.toString().toLong()+constY.toString().toLong()+constZ.toString().toLong()
            }
            com.microsoft.z3.Status.UNSATISFIABLE -> println("The equation is unsolvable")
            com.microsoft.z3.Status.UNKNOWN -> println("The solver could not determine if the equation is solvable")
        }
        return 0
    }

}

private fun PointL.pente(): Double {
    return (y.toDouble() / x.toDouble())
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
