package eu.wewox.pagecurl.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

internal fun Offset.rotate(angle: Float): Offset {
    val sin = sin(angle)
    val cos = cos(angle)
    return Offset(x * cos - y * sin, x * sin + y * cos)
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
