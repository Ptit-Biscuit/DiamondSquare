import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.math.IntVector2
import kotlin.math.pow
import kotlin.random.Random

tailrec fun diamondStepTest(points: MutableList<Double>, n: Int, step: Int) {
    (0 until n - 1 step step).forEach { xStep ->
        (0 until n - 1 step step).forEach { yStep ->
            val startPos = xStep * n + yStep
            val topRightCorner = xStep * (n - 1) + yStep
            val bottomRightCorner = xStep * (n - 1) + yStep * (n - 1)
            val bottomLeftCorner = xStep * n + yStep * (n - 1)
            val midPoint = (startPos + topRightCorner + bottomRightCorner + bottomLeftCorner) / 4

            points[midPoint] = ((points[startPos] + points[topRightCorner] + points[bottomRightCorner] + points[bottomLeftCorner]) / 4) + Random.nextDouble(360.0) % 360
        }
    }

    if (step > 2)
        diamondStepTest(points, n, step / 2)
}

fun main() = application {
    val n = ((2.0.pow(5)) + 1).toInt()

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