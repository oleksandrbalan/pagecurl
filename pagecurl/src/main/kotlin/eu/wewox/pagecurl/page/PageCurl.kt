package eu.wewox.pagecurl.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.PageCurlConfig

/**
 * Shows the pages which may be turned by drag or tap gestures.
 *
 * @param state The state of the PageCurl. Use this to programmatically change the current page or observe changes.
 * @param modifier The modifier for this composable.
 * @param config The configuration for PageCurl. Configures how page curl looks like and interacts.
 * @param content The content lambda to provide the page composable. Receives the page number.
 */
@ExperimentalPageCurlApi
@Composable
public fun PageCurl(
    state: PageCurlState,
    modifier: Modifier = Modifier,
    config: PageCurlConfig = PageCurlConfig(),
    content: @Composable (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val updatedCurrent by rememberUpdatedState(state.current)

    BoxWithConstraints(modifier) {
        state.setup(constraints)

        val internalState by rememberUpdatedState(state.internalState ?: return@BoxWithConstraints)

        Box(
            Modifier
                .curlGesture(
                    state = internalState,
                    enabled = config.interaction.drag.forward.enabled && updatedCurrent < state.max - 1,
                    scope = scope,
                    direction = config.interaction.drag.forward,
                    start = internalState.rightEdge,
                    end = internalState.leftEdge,
                    edge = internalState.forward,
                    onChange = { state.current = updatedCurrent + 1 }
                )
                .curlGesture(
                    state = internalState,
                    enabled = config.interaction.drag.backward.enabled && updatedCurrent > 0,
                    scope = scope,
                    direction = config.interaction.drag.backward,
                    start = internalState.leftEdge,
                    end = internalState.rightEdge,
                    edge = internalState.backward,
                    onChange = { state.current = updatedCurrent - 1 }
                )
                .tapGesture(
                    state = internalState,
                    scope = scope,
                    interaction = config.interaction.tap,
                    onTapForward = state::next,
                    onTapBackward = state::prev,
                )
        ) {
            if (updatedCurrent + 1 < state.max) {
                content(updatedCurrent + 1)
            }

            if (updatedCurrent < state.max) {
                Box(Modifier.drawCurl(config.curl, internalState.forward.value.top, internalState.forward.value.bottom)) {
                    content(updatedCurrent)
                }
            }

            if (updatedCurrent > 0) {
                Box(Modifier.drawCurl(config.curl, internalState.backward.value.top, internalState.backward.value.bottom)) {
                    content(updatedCurrent - 1)
                }
            }
        }
    }
}
