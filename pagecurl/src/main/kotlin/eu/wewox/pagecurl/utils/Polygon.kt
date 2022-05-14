package eu.wewox.pagecurl.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

internal data class Polygon(val vertices: List<Offset>) {

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
