import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.math.IntVector2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

tailrec fun diamondStepTest(points: MutableList<Double>, n: Int, step: Int) {
    (0 until n - 1 step step).forEach { x ->
        (0 until n - 1 step step).forEach { y ->
            val startPos = x * n + y
            val topRightCorner = startPos + if (n == step) step - 1 else step
            val bottomLeftCorner = startPos + if (n == step) step * (step - 1) else n * step
            val bottomRightCorner = startPos + if (n == step) step * step - 1 else (n * step) + step
            val midPoint = (startPos + bottomRightCorner) / 2

            points[midPoint] = ((points[startPos] + points[topRightCorner] + points[bottomLeftCorner] + points[bottomRightCorner]) / 4) + Random.nextDouble(360.0) % 360

            squareStepTest(points, n, step, midPoint)
        }
    }

    if (step > 2)
        diamondStepTest(points, n, step / 2)
}

fun squareStepTest(points: MutableList<Double>, n: Int, step: Int, midPoint: Int) {
    val two = midPoint // abs(centerPoint.x - step / 2) * n + abs(centerPoint.y - step / 2)
    val three = midPoint // (centerPoint.x + step / 2) * n + abs(centerPoint.y - step / 2)
    val four = midPoint // (centerPoint.x + step / 2) * n + (centerPoint.y + step / 2)
    val five = midPoint // abs(centerPoint.x - step / 2) * n + (centerPoint.y + step / 2)

    // above
    points[midPoint] = (
            (
                    points[midPoint]
                            + points[two]
                            + points[midPoint] // centerPoint.x + ((n * abs(centerPoint.y - step)) % n)
                            + points[three]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360

    // right
    points[midPoint] = ( // ((centerPoint.x + step / 2) % n) + n * centerPoint.y
            (
                    points[midPoint]
                            + points[three]
                            + points[midPoint] // ((centerPoint.x + step) % n) + n * centerPoint.y
                            + points[four]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360

    // below
    points[midPoint] = ( // centerPoint.x + ((n * (centerPoint.y + step / 2)) % n)
            (
                    points[midPoint]
                            + points[four]
                            + points[midPoint] // centerPoint.x + ((n * abs(centerPoint.y + step)) % n)
                            + points[five]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360

    // left
    points[midPoint] = ( // (abs(centerPoint.x - step / 2) % n) + n * centerPoint.y
            (
                    points[midPoint]
                            + points[five]
                            + points[midPoint] // (abs(centerPoint.x - step) % n) + n * centerPoint.y
                            + points[two]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360
}

fun main() = application {
    val n = ((2.0.pow(2)) + 1).toInt()

    program {
        configure {
            width = n
            height = n
        }

        val points = MutableList(n * n) { .0 }

        points[0] = Random.nextDouble(360.0)
        points[n - 1] = Random.nextDouble(360.0)
        points[n * (n - 1)] = Random.nextDouble(360.0)
        points[n * n - 1] = Random.nextDouble(360.0)

        diamondStepTest(points, n, n)

        extend {
            (0 until n).forEach { x ->
                (0 until n).forEach { y ->
                    drawer.fill =
                        if (points[x * n + y] != .0) ColorHSVa(points[x * n + y], .5, 1.0).toRGBa() else ColorRGBa.BLACK
                    drawer.circle((x * (width / (n - 1))).toDouble(), (y * (height / (n - 1))).toDouble(), 4.0)
                }
            }
        }
    }
}