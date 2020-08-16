import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.colorBuffer
import org.openrndr.math.IntVector2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

tailrec fun diamondStep(points: MutableList<MutableList<Double>>, n: Int, step: Int) {
    (0 until n - 1 step step).forEach { x ->
        (0 until n - 1 step step).forEach { y ->
            val midPoint = IntVector2(x + step / 2, y + step / 2)

            points[midPoint.x][midPoint.y] =
                (points[x][y] + points[(x + step) % n][y] + points[x][(y + step) % n] + points[(x + step) % n][(y + step) % n]) / 4

            squareStep(points, n, step, midPoint.x, midPoint.y)
        }
    }

    if (step > 2) {
        diamondStep(points, n, step / 2)
    }
}

fun squareStep(points: MutableList<MutableList<Double>>, n: Int, step: Int, x: Int, y: Int) {
    // above
    points[x][y - step / 2] =
        (points[x][y] + points[x - step / 2][y - step / 2] + points[x][abs(y - step)] + points[x + step / 2][y - step / 2]) / 4

    // right
    points[x + step / 2][y] =
        (points[x][y] + points[x + step / 2][y - step / 2] + points[(x + step) % n][y] + points[x + step / 2][y + step / 2]) / 4

    // below
    points[x][y + step / 2] =
        (points[x][y] + points[x + step / 2][y + step / 2] + points[x][(y + step) % n] + points[x - step / 2][y + step / 2]) / 4

    // left
    points[x - step / 2][y] =
        (points[x][y] + points[x - step / 2][y + step / 2] + points[abs(x - step)][y] + points[x - step / 2][y - step / 2]) / 4
}

fun main() = application {
    val n = (2.0.pow(9) + 1).toInt()

    program {
        val points = MutableList(n) { MutableList(n) { .0 } }
        val cb = colorBuffer(n, n)

        points[0][0] = Random.nextDouble(360.0)
        points[n - 1][0] = Random.nextDouble(360.0)
        points[0][n - 1] = Random.nextDouble(360.0)
        points[n - 1][n - 1] = Random.nextDouble(360.0)

        diamondStep(points, n, n)

        cb.shadow.let {
            it.download()
            (0 until n).forEach { x ->
                (0 until n).forEach { y ->
                    it[x, y] = ColorHSVa(points[x][y], .4, 1.0).toRGBa()
                }
            }
            it.upload()
        }

        extend {
            drawer.image(cb)
        }
    }
}
