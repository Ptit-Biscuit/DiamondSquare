import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.colorBuffer
import org.openrndr.math.IntVector2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

tailrec fun diamondStepToto(points: MutableList<MutableList<Double>>, n: Int, step: Int) {
    (0 until n - 1 step step).forEach { x ->
        (0 until n - 1 step step).forEach { y ->
            val midPoint = IntVector2(x + step / 2, y + step / 2)

            points[midPoint.x][midPoint.y] =
                ((points[x][y]
                        + points[(x + step) % n][y]
                        + points[x][(y + step) % n]
                        + points[(x + step) % n][(y + step) % n]) / 4)
            +Random.nextDouble(360.0) % 360

            squareStep(points, n, step, midPoint.x, midPoint.y)
        }
    }

    if (step > 2) {
        diamondStepToto(points, n, step / 2)
    }
}

fun squareStep(points: MutableList<MutableList<Double>>, n: Int, step: Int, x: Int, y: Int) {
    val one = abs(x - step / 2) % n
    val two = (x + step / 2) % n
    val three = abs(y - step / 2) % n
    val four = (y + step / 2) % n

    val mid = points[x][y]
    val a = points[one][three]
    val b = points[two][three]
    val c = points[two][four]
    val d = points[one][four]

    // above
    points[x][y - step / 2] = ((mid + a + points[x][abs(y - step) % n] + b) / 4) + Random.nextDouble(360.0) % 360

    // right
    points[x + step / 2][y] = ((mid + b + points[(x + step) % n][y] + c) / 4) + Random.nextDouble(360.0) % 360

    // below
    points[x][y + step / 2] = ((mid + c + points[x][(y + step) % n] + d) / 4) + Random.nextDouble(360.0) % 360

    // left
    points[x - step / 2][y] = ((mid + d + points[abs(x - step) % n][y] + a) / 4) + Random.nextDouble(360.0) % 360
}

fun main() = application {
    val n = (2.0.pow(9) + 1).toInt()

    program {
        configure {
            width = 900
            height = 900
        }

        val points = MutableList(n) { MutableList(n) { .0 } }
        val cb = colorBuffer(n, n)

        points[0][0] = Random.nextDouble(360.0)
        points[n - 1][0] = Random.nextDouble(360.0)
        points[0][n - 1] = Random.nextDouble(360.0)
        points[n - 1][n - 1] = Random.nextDouble(360.0)

        extend {
            diamondStepToto(points, n, n)

            cb.shadow.let {
                it.download()
                (0 until n).forEach { x ->
                    (0 until n).forEach { y ->
                        it[x, y] = ColorHSVa(points[x][y], .5, 1.0).toRGBa()
                    }
                }
                it.upload()
            }

            drawer.image(cb)

            points[0][0] += 1.0
            points[n - 1][0] += 1.0
            points[0][n - 1] += 1.0
            points[n - 1][n - 1] += 1.0
        }
    }
}
