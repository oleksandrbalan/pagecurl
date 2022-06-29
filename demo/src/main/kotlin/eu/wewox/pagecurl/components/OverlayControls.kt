package eu.wewox.pagecurl.components

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset

internal fun Modifier.overlayControls(
    next: () -> Unit,
    prev: () -> Unit,
    center: () -> Unit,
): Modifier = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown().also { it.consume() }
            val up = waitForUpOrCancellation() ?: return@awaitPointerEventScope
            up.consume()
            if ((down.position - up.position).getDistance() > viewConfiguration.touchSlop) {
                return@awaitPointerEventScope
            }

            if ((up.position - size.center.toOffset()).getDistance() < 100f) {
                center()
                return@awaitPointerEventScope
            }

            if (up.position.x < size.width / 2) {
                prev()
            } else {
                next()
            }
        }
    }
}
