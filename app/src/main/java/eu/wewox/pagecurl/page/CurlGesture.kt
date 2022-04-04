package eu.wewox.pagecurl.page

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed

fun Modifier.curlGesture(
    onCurl: (Offset, Offset) -> Unit,
    onCancel: () -> Unit,
): Modifier = pointerInput(true) {
    forEachGesture {
        awaitPointerEventScope {
            awaitFirstDown(requireUnconsumed = false)
            do {
                val event = awaitPointerEvent()
                val canceled = event.changes.any { it.positionChangeConsumed() }
                val posA = event.changes.getOrNull(0)?.position
                val posB = event.changes.getOrNull(1)?.position
                if (posA != null && posB != null) {
                    onCurl(posA, posB)
                }
            } while (!canceled && event.changes.any { it.pressed })
            onCancel()
        }
    }
}
