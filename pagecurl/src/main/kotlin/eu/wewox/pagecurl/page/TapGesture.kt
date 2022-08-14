package eu.wewox.pagecurl.page

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.utils.multiply
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalPageCurlApi
internal fun Modifier.tapGesture(
    config: PageCurlConfig,
    scope: CoroutineScope,
    onTapForward: suspend () -> Unit,
    onTapBackward: suspend () -> Unit,
): Modifier = pointerInput(config) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown().also { it.consume() }
            val up = waitForUpOrCancellation() ?: return@awaitPointerEventScope

            if ((down.position - up.position).getDistance() > viewConfiguration.touchSlop) {
                return@awaitPointerEventScope
            }

            if (config.tapCustomEnabled && config.onCustomTap(this, size, up.position)) {
                return@awaitPointerEventScope
            }

            if (config.tapForwardEnabled && config.tapForwardInteraction.target.multiply(size).contains(up.position)) {
                scope.launch {
                    onTapForward()
                }
                return@awaitPointerEventScope
            }

            if (config.tapBackwardEnabled && config.tapBackwardInteraction.target.multiply(size).contains(up.position)) {
                scope.launch {
                    onTapBackward()
                }
                return@awaitPointerEventScope
            }
        }
    }
}
