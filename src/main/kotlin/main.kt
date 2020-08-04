import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.colorBuffer
import org.openrndr.math.IntVector2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

class Square(val startPos: IntVector2, n: Int, size: Int) {
    val topRightCorner = IntVector2((startPos.x + size) % n, startPos.y)
    val bottomRightCorner = IntVector2((startPos.x + size) % n, (startPos.y + size) % n)
    val bottomLeftCorner = IntVector2(startPos.x, (startPos.y + size) % n)

    val midPoint = IntVector2(
        (startPos.x + topRightCorner.x + bottomRightCorner.x + bottomLeftCorner.x) / 4,
        (startPos.y + topRightCorner.y + bottomRightCorner.y + bottomLeftCorner.y) / 4
    )
}

fun diamondStep(points: MutableList<Double>, n: Int, step: Int) {
    (0 until n - 1 step step).forEach { xStep ->
        (0 until n - 1 step step).toList().parallelStream().forEach { yStep ->
            val square = Square(IntVector2(xStep, yStep), n, step)

            points[square.midPoint.x + n * square.midPoint.y] = (
                    (
                            points[square.startPos.x + n * square.startPos.y]
                                    + points[square.topRightCorner.x + n * square.topRightCorner.y]
                                    + points[square.bottomRightCorner.x + n * square.bottomRightCorner.y]
                                    + points[square.bottomLeftCorner.x + n * square.bottomLeftCorner.y]
                            ) / 4
                    ) + Random.nextDouble(360.0) % 360

            squareStep(points, n, step, square.midPoint)
        }
    }
}

fun squareStep(points: MutableList<Double>, n: Int, step: Int, centerPoint: IntVector2) {
    val one = centerPoint.x + n * centerPoint.y
    val two = (abs(centerPoint.x - step / 2) % n) + ((n * abs(centerPoint.y - step / 2)) % n)
    val three = ((centerPoint.x + step / 2) % n) + ((n * abs(centerPoint.y - step / 2)) % n)
    val four = ((centerPoint.x + step / 2) % n) + ((n * (centerPoint.y + step / 2)) % n)
    val five = (abs(centerPoint.x - step / 2) % n) + ((n * (centerPoint.y + step / 2)) % n)

    // above
    points[centerPoint.x + ((n * abs(centerPoint.y - step / 2)) % n)] = (
            (
                    points[one]
                            + points[two]
                            + points[centerPoint.x + ((n * abs(centerPoint.y - step)) % n)]
                            + points[three]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360

    // right
    points[((centerPoint.x + step / 2) % n) + n * centerPoint.y] = (
            (
                    points[one]
                            + points[three]
                            + points[((centerPoint.x + step) % n) + n * centerPoint.y]
                            + points[four]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360

    // below
    points[centerPoint.x + ((n * (centerPoint.y + step / 2)) % n)] = (
            (
                    points[one]
                            + points[four]
                            + points[centerPoint.x + ((n * abs(centerPoint.y + step)) % n)]
                            + points[five]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360

    // left
    points[(abs(centerPoint.x - step / 2) % n) + n * centerPoint.y] = (
            (
                    points[one]
                            + points[five]
                            + points[(abs(centerPoint.x - step) % n) + n * centerPoint.y]
                            + points[two]
                    ) / 4
            ) + Random.nextDouble(360.0) % 360

    if (step > 2) {
        diamondStep(points, n, step / 2)
    }
}

fun main() = application {
    val n = (2.0.pow(7) + 1).toInt()

    program {
        configure {
            width = n
            height = n
        }

        val cb = colorBuffer(width, height)

        val points = MutableList(n * n) { .0 }

        points[0] = Random.nextDouble(360.0)
        points[n - 1] = Random.nextDouble(360.0)
        points[(n - 1) * (n - 1)] = Random.nextDouble(360.0)
        points[n * (n - 1)] = Random.nextDouble(360.0)

        diamondStep(points, n, n)

        cb.shadow.let {
            it.download()
            (0 until n).forEach { x ->
                (0 until n).forEach { y ->
                    it[x, y] = ColorHSVa(points[x + n * y], .5, 1.0).toRGBa()
                }
            }
            it.upload()
        }

        extend {
            drawer.image(cb)

            /*(0 until n).forEach { x ->
                (0 until n).forEach { y ->
                    drawer.fill = ColorHSVa(points[x + n * y], .5, 1.0).toRGBa()
                    drawer.circle((x * (width / (n - 1))).toDouble(), (y * (height / (n - 1))).toDouble(), 4.0)
                }
            }*/
        }
    }
}