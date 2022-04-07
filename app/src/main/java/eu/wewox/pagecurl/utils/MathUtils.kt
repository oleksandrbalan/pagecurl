package eu.wewox.pagecurl.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

data class Polygon(val vertices: List<Offset>) {

    private val size: Int = vertices.size

    fun translate(offset: Offset): Polygon =
        Polygon(vertices.map { it + offset })

    fun offset(value: Float): Polygon {
        val edgeNormals = List(size) {
            val edge = vertices[index(it + 1)] - vertices[index(it)]
            Offset(edge.y, -edge.x).normalized()
        }

        val vertexNormals = List(size) {
            (edgeNormals[index(it - 1)] + edgeNormals[index(it)]).normalized()
        }

        return Polygon(
            vertices.mapIndexed { index, vertex ->
                vertex + vertexNormals[index] * value
            }
        )
    }

    fun toPath(): Path =
        Path().apply {
            vertices.forEachIndexed { index, vertex ->
                if (index == 0) {
                    moveTo(vertex.x, vertex.y)
                } else {
                    lineTo(vertex.x, vertex.y)
                }
            }
        }

    private fun index(i: Int) = ((i % size) + size) % size
}

private fun Offset.normalized(): Offset {
    val distance = getDistance()
    return if (distance != 0f) this / distance else this
}

internal fun lineLineIntersection(
    line1a: Offset,
    line1b: Offset,
    line2a: Offset,
    line2b: Offset,
): Offset? {
    val denominator = (line1a.x - line1b.x) * (line2a.y - line2b.y) - (line1a.y - line1b.y) * (line2a.x - line2b.x)
    if (denominator == 0f) return null

    val x = ((line1a.x * line1b.y - line1a.y * line1b.x) * (line2a.x - line2b.x) -
            (line1a.x - line1b.x) * (line2a.x * line2b.y - line2a.y * line2b.x)) / denominator
    val y = ((line1a.x * line1b.y - line1a.y * line1b.x) * (line2a.y - line2b.y) -
            (line1a.y - line1b.y) * (line2a.x * line2b.y - line2a.y * line2b.x)) / denominator
    return Offset(x, y)
}
