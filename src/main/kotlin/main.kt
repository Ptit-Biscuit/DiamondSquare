import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.colorBuffer
import org.openrndr.math.IntVector2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

data class Node(var color: Double = .0, var set: Boolean = false)

class SquareBis(val startPos: IntVector2, size: Int) {
    val topRightCorner = IntVector2(startPos.x + size, startPos.y)
    val bottomRightCorner = IntVector2(startPos.x + size, startPos.y + size)
    val bottomLeftCorner = IntVector2(startPos.x, startPos.y + size)

    val midPoint = IntVector2(
        (startPos.x + topRightCorner.x + bottomRightCorner.x + bottomLeftCorner.x) / 4,
        (startPos.y + topRightCorner.y + bottomRightCorner.y + bottomLeftCorner.y) / 4
    )
}

tailrec fun diamondStepToto(points: MutableList<MutableList<Node>>, step: Int) {
    (0 until points.size - 1 step step).forEach { xStep ->
        (0 until points[xStep].size - 1 step step).forEach { yStep ->
            val square = SquareBis(IntVector2(xStep, yStep), step)
            val midPoint = square.midPoint

            points[midPoint.x][midPoint.y].color =
                ((points[square.startPos.x][square.startPos.y].color
                        + points[square.topRightCorner.x % points.size][square.topRightCorner.y % points.size].color
                        + points[square.bottomRightCorner.x % points.size][square.bottomRightCorner.y % points.size].color
                        + points[square.bottomLeftCorner.x % points.size][square.bottomLeftCorner.y % points.size].color) / 4)
            +Random.nextDouble(360.0) % 360
            points[midPoint.x][midPoint.y].set = true

            squareStep(points, step, midPoint)
        }
    }

    if (step > 2) {
        diamondStepToto(points, step / 2)
    }
}

fun squareStep(points: MutableList<MutableList<Node>>, step: Int, centerPoint: IntVector2) {
    // above
    val pointAbove = points[centerPoint.x][centerPoint.y - step / 2]
    pointAbove.color =
        ((points[centerPoint.x][centerPoint.y].color +
                points[abs(centerPoint.x - step / 2) % points.size][abs(centerPoint.y - step / 2) % points.size].color +
                points[centerPoint.x][abs(centerPoint.y - step) % points.size].color +
                points[abs(centerPoint.x + step / 2) % points.size][abs(centerPoint.y - step / 2) % points.size].color) / 4)
    +Random.nextDouble(360.0) % 360
    pointAbove.set = true

    // right
    val pointRight = points[centerPoint.x + step / 2][centerPoint.y]
    pointRight.color =
        ((points[centerPoint.x][centerPoint.y].color +
                points[abs(centerPoint.x + step / 2) % points.size][abs(centerPoint.y - step / 2) % points.size].color +
                points[abs(centerPoint.x + step) % points.size][centerPoint.y].color +
                points[abs(centerPoint.x + step / 2) % points.size][abs(centerPoint.y + step / 2) % points.size].color) / 4)
    +Random.nextDouble(360.0) % 360
    pointRight.set = true

    // below
    val pointBelow = points[centerPoint.x][centerPoint.y + step / 2]
    pointBelow.color =
        ((points[centerPoint.x][centerPoint.y].color +
                points[abs(centerPoint.x + step / 2) % points.size][abs(centerPoint.y + step / 2) % points.size].color +
                points[centerPoint.x][abs(centerPoint.y + step) % points.size].color +
                points[abs(centerPoint.x - step / 2) % points.size][abs(centerPoint.y + step / 2) % points.size].color) / 4)
    +Random.nextDouble(360.0) % 360
    pointBelow.set = true

    // left
    val pointLeft = points[centerPoint.x - step / 2][centerPoint.y]
    pointLeft.color =
        ((points[centerPoint.x][centerPoint.y].color +
                points[abs(centerPoint.x - step / 2) % points.size][abs(centerPoint.y + step / 2) % points.size].color +
                points[abs(centerPoint.x - step) % points.size][centerPoint.y].color +
                points[abs(centerPoint.x - step / 2) % points.size][abs(centerPoint.y - step / 2) % points.size].color) / 4)
    +Random.nextDouble(360.0) % 360
    pointLeft.set = true
}

fun main() = application {
    val n = (2.0.pow(9) + 1).toInt()

    program {
        configure {
            width = 900
            height = 900
        }

        val points = MutableList(n) { MutableList(n) { Node() } }
        val cb = colorBuffer(n, n)

        points[0][0] = Node(Random.nextDouble(360.0), true)
        points[n - 1][0] = Node(Random.nextDouble(360.0), true)
        points[0][n - 1] = Node(Random.nextDouble(360.0), true)
        points[n - 1][n - 1] = Node(Random.nextDouble(360.0), true)

        diamondStepToto(points, n)

        cb.shadow.let {
            it.download()
            (0 until n).forEach { x ->
                (0 until n).forEach { y ->
                    it[x, y] = ColorHSVa(points[x][y].color, .5, 1.0).toRGBa()
                }
            }
            it.upload()
        }

        extend {
            drawer.image(cb)
        }
    }
}
