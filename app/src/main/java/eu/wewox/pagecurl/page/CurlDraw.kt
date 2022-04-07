package eu.wewox.pagecurl.page

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.wewox.pagecurl.utils.Polygon
import eu.wewox.pagecurl.utils.lineLineIntersection
import java.lang.Float.max
import kotlin.math.atan2

data class CurlConfig(
    val shadow: ShadowConfig = ShadowConfig()
) {
    data class ShadowConfig(
        val color: Color = Color.Black,
        val alpha: Float = 0.2f,
        val radius: Dp = 40.dp,
        val offsetX: Dp = 0.dp,
        val offsetY: Dp = 0.dp,
    )
}

fun Modifier.drawCurl(
    config: CurlConfig = CurlConfig(),
    posA: Offset?,
    posB: Offset?,
): Modifier = drawWithCache {
    fun drawOnlyContent() =
        onDrawWithContent {
            drawContent()
        }

    if (posA == null || posB == null) {
        return@drawWithCache drawOnlyContent()
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
        return@drawWithCache drawOnlyContent()
    }

    val topCurlOffset = Offset(max(0f, topIntersection.x), topIntersection.y)
    val bottomCurlOffset = Offset(max(0f, bottomIntersection.x), bottomIntersection.y)

    val clippedContent = prepareClippedContent(topCurlOffset, bottomCurlOffset)
    val shadowBelowCurl = prepareShadowBelowCurl(config.shadow, topCurlOffset, bottomCurlOffset)
    val curl = prepareCurl(topCurlOffset, bottomCurlOffset)

    onDrawWithContent {
        clippedContent()
        shadowBelowCurl()
        curl()
    }
}

private fun CacheDrawScope.prepareClippedContent(
    topCurlOffset: Offset,
    bottomCurlOffset: Offset,
): ContentDrawScope.() -> Unit {
    val path = Path()
    path.lineTo(topCurlOffset.x, topCurlOffset.y)
    path.lineTo(bottomCurlOffset.x, bottomCurlOffset.y)
    path.lineTo(0f, size.height)
    return result@{
        clipPath(path) {
            this@result.drawContent()
        }
    }
}

private fun CacheDrawScope.prepareShadowBelowCurl(
    shadow: CurlConfig.ShadowConfig,
    topCurlOffset: Offset,
    bottomCurlOffset: Offset,
): ContentDrawScope.() -> Unit {
    val shadowColor = shadow.color.copy(alpha = shadow.alpha).toArgb()
    val transparent = shadow.color.copy(alpha = 0f).toArgb()

    val paint = Paint()
    val frameworkPaint = paint.asFrameworkPaint()
    frameworkPaint.color = transparent

    val radius = shadow.radius.toPx()
    frameworkPaint.setShadowLayer(
        shadow.radius.toPx(),
        shadow.offsetX.toPx(),
        shadow.offsetY.toPx(),
        shadowColor
    )

    val bitmap = Bitmap.createBitmap((size.width + radius * 4).toInt(), (size.height + radius * 4).toInt(), Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(Color.Transparent.toArgb())
    val canvas = Canvas(bitmap)

    val path = Polygon(
        sequence {
            if (topCurlOffset.x < size.width) {
                yield(topCurlOffset)
                yield(Offset(size.width, topCurlOffset.y))
            } else {
                val a = lineLineIntersection(topCurlOffset, bottomCurlOffset, Offset(size.width, 0f), Offset(size.width, size.height))!!
                yield(a)
                yield(a)
            }
            if (bottomCurlOffset.x < size.width) {
                yield(Offset(size.width, size.height))
                yield(bottomCurlOffset)
            } else {
                val a = lineLineIntersection(topCurlOffset, bottomCurlOffset, Offset(size.width, 0f), Offset(size.width, size.height))!!
                yield(a)
                yield(a)
            }
        }.toList()
    ).translate(
        Offset(2 * radius, 2 * radius)
    ).offset(
        radius
    ).toPath()

    canvas.drawPath(path.asAndroidPath(), frameworkPaint)

    return {
        val lineVector = topCurlOffset - bottomCurlOffset
        val angle = atan2(-lineVector.y, lineVector.x)

        withTransform({
            scale(-1f, 1f, pivot = bottomCurlOffset)

            rotateRad(Math.PI.toFloat() + 2 * angle, pivot = bottomCurlOffset)
        }) {
            drawIntoCanvas {
                it.nativeCanvas.drawBitmap(bitmap, -2 * radius, -2 * radius, null)
            }
        }
    }
}

private fun CacheDrawScope.prepareCurl(
    topCurlOffset: Offset,
    bottomCurlOffset: Offset,
): ContentDrawScope.() -> Unit {
    val lineVector = topCurlOffset - bottomCurlOffset
    val angle = atan2(-lineVector.y, lineVector.x)

    val path = Path()
    path.moveTo(topCurlOffset.x, topCurlOffset.y)
    path.lineTo(max(size.width, topCurlOffset.x), topCurlOffset.y)
    path.lineTo(max(size.width, bottomCurlOffset.x), bottomCurlOffset.y)
    path.lineTo(bottomCurlOffset.x, bottomCurlOffset.y)

    return result@{
        withTransform({
            scale(-1f, 1f, pivot = bottomCurlOffset)

            rotateRad(Math.PI.toFloat() + 2 * angle, pivot = bottomCurlOffset)

            clipPath(path)
        }) {
            this@result.drawContent()
            drawRect(Color.White.copy(alpha = 0.8f))
        }
    }
}
