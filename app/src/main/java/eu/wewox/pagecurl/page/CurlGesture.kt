package eu.wewox.pagecurl.page

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import eu.wewox.pagecurl.config.CurlDirection
import eu.wewox.pagecurl.utils.rotate
import kotlin.math.PI

fun Modifier.curlGesture(
    enabled: Boolean,
    direction: CurlDirection,
    onStart: () -> Unit,
    onCurl: (Offset, Offset) -> Unit,
    onEnd: () -> Unit,
    onCancel: () -> Unit,
): Modifier = pointerInput(enabled) {
    if (!enabled) {
        return@pointerInput
    }

    val startRect by lazy { direction.start.multiply(size) }
    val endRect by lazy { direction.end.multiply(size) }
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (!startRect.contains(down.position)) {
                return@awaitPointerEventScope
            }

            val dragStart = down.position.copy(x = size.width.toFloat())

            onStart()

            var dragCurrent = dragStart
            drag(down.id) { change ->
                dragCurrent = change.position
                change.consumeAllChanges()
                val vector = (dragStart - dragCurrent).rotate(PI.toFloat() / 2)
                onCurl(dragCurrent - vector, dragCurrent + vector)
            }

            if (endRect.contains(dragCurrent)) {
                onEnd()
            } else {
                onCancel()
            }
        }
    }
}

private fun Rect.multiply(size: IntSize): Rect =
    Rect(
        topLeft = Offset(size.width * left, size.height * top),
        bottomRight = Offset(size.width * right, size.height * bottom),
    )
