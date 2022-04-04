package eu.wewox.pagecurl.page

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.graphics.drawscope.withTransform
import java.lang.Float.max
import kotlin.math.atan2

fun Modifier.drawCurl(
    posA: Offset?,
    posB: Offset?,
): Modifier = drawWithContent {
    if (posA == null || posB == null) {
        drawContent()
        return@drawWithContent
    }

    val topIntersection = lineLineIntersection(
        Offset(0f, 0f), Offset(size.width, 0f),
        posA, posB
    )
    val bottomIntersection = lineLineIntersection(
        Offset(0f, size.height), Offset(size.width, size.height),
        posA, posB
    )
    if (topIntersection == null || bottomIntersection == null) {
        drawContent()
        return@drawWithContent
    }

    val topCurlOffset = Offset(max(0f, topIntersection.x), topIntersection.y)
    val bottomCurlOffset = Offset(max(0f, bottomIntersection.x), bottomIntersection.y)

    val lineVector = topCurlOffset - bottomCurlOffset

    val path = Path()
    path.lineTo(topCurlOffset.x, topCurlOffset.y)
    path.lineTo(bottomCurlOffset.x, bottomCurlOffset.y)
    path.lineTo(0f, size.height)
    clipPath(path) { this@drawWithContent.drawContent() }

    withTransform({
        scale(-1f, 1f, pivot = bottomCurlOffset)

        val angle = atan2(-lineVector.y, lineVector.x)
        rotateRad(Math.PI.toFloat() + 2 * angle, pivot = bottomCurlOffset)

        val path2 = Path()
        path2.moveTo(topCurlOffset.x, topCurlOffset.y)
        path2.lineTo(max(size.width, topCurlOffset.x), topCurlOffset.y)
        path2.lineTo(max(size.width, bottomCurlOffset.x), bottomCurlOffset.y)
        path2.lineTo(bottomCurlOffset.x, bottomCurlOffset.y)
        clipPath(path2)
    }) {
        this@drawWithContent.drawContent()
        drawRect(Color.White.copy(alpha = 0.8f))
    }
}

private fun lineLineIntersection(
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
