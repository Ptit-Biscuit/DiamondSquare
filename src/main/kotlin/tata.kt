import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.colorBuffer
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

            points[midPoint] = (
                    (points[startPos]
                            + points[topRightCorner]
                            + points[bottomLeftCorner]
                            + points[bottomRightCorner]
                            ) / 4
                    ) + Random.nextDouble(360.0) % 360

            squareStepTest(points, midPoint, listOf(startPos, topRightCorner, bottomLeftCorner, bottomRightCorner))
        }
    }

    if (step > 2)
        diamondStepTest(points, n, step / 2)
}

fun squareStepTest(points: MutableList<Double>, midPoint: Int, square: List<Int>) {
    // above
    val above = (square[0] + square[1]) / 2
    points[above] = ((points[midPoint] + points[0] + points[1] + points[midPoint]) / 4) + Random.nextDouble(360.0) % 360

    // right
    val right = (square[1] + square[3]) / 2
    points[right] = ((points[midPoint] + points[1] + points[3] + points[midPoint]) / 4) + Random.nextDouble(360.0) % 360

    // below
    val below = (square[2] + square[3]) / 2
    points[below] = ((points[midPoint] + points[2] + points[3] + points[midPoint]) / 4) + Random.nextDouble(360.0) % 360

    // left
    val left = (square[0] + square[2]) / 2
    points[left] = ((points[midPoint] + points[0] + points[2] + points[midPoint]) / 4) + Random.nextDouble(360.0) % 360
}

fun main() = application {
    val n = ((2.0.pow(9)) + 1).toInt()

    program {
        configure {
            width = n
            height = n
        }

        val points = MutableList(n * n) { .0 }
        val cb = colorBuffer(n, n)

        points[0] = Random.nextDouble(360.0)
        points[n - 1] = Random.nextDouble(360.0)
        points[n * (n - 1)] = Random.nextDouble(360.0)
        points[n * n - 1] = Random.nextDouble(360.0)

        diamondStepTest(points, n, n)

        cb.shadow.let {
            it.download()
            (0 until n).forEach { x ->
                (0 until n).forEach { y ->
                    it[x, y] = ColorHSVa(points[x * n + y], .5, 1.0).toRGBa()
                }
            }
            it.upload()
        }

        extend {
            drawer.image(cb)
        }
    }
}