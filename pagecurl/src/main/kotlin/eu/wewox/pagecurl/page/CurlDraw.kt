package eu.wewox.pagecurl.page

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
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
import androidx.compose.ui.unit.dp
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.CurlConfig
import eu.wewox.pagecurl.utils.Polygon
import eu.wewox.pagecurl.utils.lineLineIntersection
import eu.wewox.pagecurl.utils.rotate
import java.lang.Float.max
import kotlin.math.atan2

@ExperimentalPageCurlApi
internal fun Modifier.drawCurl(
    config: CurlConfig = CurlConfig(),
    posA: Offset,
    posB: Offset,
): Modifier = drawWithCache {
    fun drawOnlyContent() =
        onDrawWithContent {
            drawContent()
        }

    if (posA == size.toRect().topLeft && posB == size.toRect().bottomLeft) {
        return@drawWithCache onDrawWithContent { }
    }

    if (posA == size.toRect().topRight && posB == size.toRect().bottomRight) {
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

    val drawClippedContent = prepareClippedContent(topCurlOffset, bottomCurlOffset)
    val drawCurl = prepareCurl(config, topCurlOffset, bottomCurlOffset)

    onDrawWithContent {
        drawClippedContent()
        drawCurl()
    }
}

@ExperimentalPageCurlApi
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

@ExperimentalPageCurlApi
private fun CacheDrawScope.prepareCurl(
    config: CurlConfig,
    topCurlOffset: Offset,
    bottomCurlOffset: Offset,
): ContentDrawScope.() -> Unit {
    val polygon = Polygon(
        sequence {
            suspend fun SequenceScope<Offset>.yieldEndSideInterception() {
                val offset = lineLineIntersection(
                    topCurlOffset, bottomCurlOffset,
                    Offset(size.width, 0f), Offset(size.width, size.height)
                ) ?: return
                yield(offset)
                yield(offset)
            }
            if (topCurlOffset.x < size.width) {
                yield(topCurlOffset)
                yield(Offset(size.width, topCurlOffset.y))
            } else {
                yieldEndSideInterception()
            }
            if (bottomCurlOffset.x < size.width) {
                yield(Offset(size.width, size.height))
                yield(bottomCurlOffset)
            } else {
                yieldEndSideInterception()
            }
        }.toList()
    )

    val lineVector = topCurlOffset - bottomCurlOffset
    val angle = Math.PI.toFloat() + atan2(-lineVector.y, lineVector.x) * 2
    val drawShadow = prepareShadow(config, polygon, angle)

    return result@{
        withTransform({
            scale(-1f, 1f, pivot = bottomCurlOffset)
            rotateRad(angle, pivot = bottomCurlOffset)
        }) {
            this@result.drawShadow()

            clipPath(polygon.toPath()) {
                this@result.drawContent()

                val overlayAlpha = 1f - config.backPage.contentAlpha
                drawRect(config.backPage.color.copy(alpha = overlayAlpha))
            }
        }
    }
}

@ExperimentalPageCurlApi
private fun CacheDrawScope.prepareShadow(
    config: CurlConfig,
    polygon: Polygon,
    angle: Float
): ContentDrawScope.() -> Unit {
    val shadow = config.shadow

    if (shadow.alpha == 0f || shadow.radius == 0.dp) {
        return { /* No shadow is requested */ }
    }

    val radius = shadow.radius.toPx()
    val shadowColor = shadow.color.copy(alpha = shadow.alpha).toArgb()
    val transparent = shadow.color.copy(alpha = 0f).toArgb()
    val shadowOffset = Offset(-shadow.offset.x.toPx(), shadow.offset.y.toPx())
        .rotate(2 * Math.PI.toFloat() - angle)
    val paint = Paint().apply {
        val frameworkPaint = asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(
            shadow.radius.toPx(),
            shadowOffset.x,
            shadowOffset.y,
            shadowColor
        )
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        prepareShadowApi28(radius, paint, polygon)
    } else {
        prepareShadowImage(radius, paint, polygon)
    }
}

private fun prepareShadowApi28(
    radius: Float,
    paint: Paint,
    polygon: Polygon,
): ContentDrawScope.() -> Unit = {
    drawIntoCanvas {
        it.nativeCanvas.drawPath(
            polygon
                .offset(radius).toPath()
                .asAndroidPath(),
            paint.asFrameworkPaint()
        )
    }
}

private fun CacheDrawScope.prepareShadowImage(
    radius: Float,
    paint: Paint,
    polygon: Polygon,
): ContentDrawScope.() -> Unit {
    val bitmap = Bitmap.createBitmap(
        (size.width + radius * 4).toInt(),
        (size.height + radius * 4).toInt(),
        Bitmap.Config.ARGB_8888
    )
    Canvas(bitmap).apply {
        drawPath(
            polygon
                .translate(Offset(2 * radius, 2 * radius))
                .offset(radius).toPath()
                .asAndroidPath(),
            paint.asFrameworkPaint()
        )
    }

    return {
        drawIntoCanvas {
            it.nativeCanvas.drawBitmap(bitmap, -2 * radius, -2 * radius, null)
        }
    }
}
