package eu.wewox.pagecurl.page

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
    val tapInteraction = config.tapInteraction as? PageCurlConfig.TargetTapInteraction ?: return@pointerInput

    awaitEachGesture {
        val down = awaitFirstDown().also { it.consume() }
        val up = waitForUpOrCancellation() ?: return@awaitEachGesture

        if ((down.position - up.position).getDistance() > viewConfiguration.touchSlop) {
            return@awaitEachGesture
        }

        if (config.tapCustomEnabled && config.onCustomTap(this, size, up.position)) {
            return@awaitEachGesture
        }

        if (config.tapForwardEnabled && tapInteraction.forward.target.multiply(size).contains(up.position)) {
            scope.launch {
                onTapForward()
            }
            return@awaitEachGesture
        }

        if (config.tapBackwardEnabled && tapInteraction.backward.target.multiply(size).contains(up.position)) {
            scope.launch {
                onTapBackward()
            }
            return@awaitEachGesture
        }
    }
}
