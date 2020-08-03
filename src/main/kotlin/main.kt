import org.openrndr.application
import org.openrndr.color.ColorRGBa
import kotlin.math.pow

data class Node(var color: Double = .0, var set: Boolean = false)

fun diamondStep(points: MutableList<MutableList<Node>>) {
}

fun squareStep(points: MutableList<MutableList<Node>>) {
}

fun main() = application {
    val n = (2.0.pow(2) + 1).toInt()

    program {
        configure {
            width = 900
            height = 600
        }

        val points = MutableList(n) { MutableList(n) { Node() } }

        val initialNode = Node(1.0, true)

        points[0][0] = initialNode
        points[n - 1][0] = initialNode
        points[0][n - 1] = initialNode
        points[n - 1][n - 1] = initialNode

        diamondStep(points)

        extend {
            points.forEachIndexed { ix, _ ->
                points[ix].forEachIndexed { iy, value ->
                    drawer.fill = ColorRGBa(255 * value.color, 255 * value.color, 255 * value.color)
                    drawer.circle((ix * (width / (n - 1))).toDouble(), (iy * (height / (n - 1))).toDouble(), 6.0)
                }
            }
        }
    }
}