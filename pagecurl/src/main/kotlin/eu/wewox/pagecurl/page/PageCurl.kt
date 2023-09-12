package eu.wewox.pagecurl.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.config.rememberPageCurlConfig

/**
 * Shows the pages which may be turned by drag or tap gestures.
 *
 * @param count The count of pages.
 * @param modifier The modifier for this composable.
 * @param state The state of the PageCurl. Use this to programmatically change the current page or observe changes.
 * @param config The configuration for PageCurl.
 * @param content The content lambda to provide the page composable. Receives the page number.
 */
@ExperimentalPageCurlApi
@Composable
public fun PageCurl(
    count: Int,
    modifier: Modifier = Modifier,
    state: PageCurlState = rememberPageCurlState(),
    config: PageCurlConfig = rememberPageCurlConfig(),
    content: @Composable (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier) {
        state.setup(count, constraints)

        val updatedCurrent by rememberUpdatedState(state.current)
        val internalState by rememberUpdatedState(state.internalState ?: return@BoxWithConstraints)

        val config by rememberUpdatedState(config)

        Box(
            Modifier
                .curlGesture(
                    state = internalState,
                    enabled = config.dragForwardEnabled && updatedCurrent < state.max - 1,
                    scope = scope,
                    targetStart = config.dragForwardInteraction.start,
                    targetEnd = config.dragForwardInteraction.end,
                    edgeStart = internalState.rightEdge,
                    edgeEnd = internalState.leftEdge,
                    edge = internalState.forward,
                    onChange = { state.current = updatedCurrent + 1 }
                )
                .curlGesture(
                    state = internalState,
                    enabled = config.dragBackwardEnabled && updatedCurrent > 0,
                    scope = scope,
                    targetStart = config.dragBackwardInteraction.start,
                    targetEnd = config.dragBackwardInteraction.end,
                    edgeStart = internalState.leftEdge,
                    edgeEnd = internalState.rightEdge,
                    edge = internalState.backward,
                    onChange = { state.current = updatedCurrent - 1 }
                )
                .tapGesture(
                    config = config,
                    scope = scope,
                    onTapForward = state::next,
                    onTapBackward = state::prev,
                )
        ) {
            // Wrap in key to synchronize state updates
            key(updatedCurrent, internalState.forward.value, internalState.backward.value) {
                if (updatedCurrent + 1 < state.max) {
                    content(updatedCurrent + 1)
                }

                if (updatedCurrent < state.max) {
                    val forward = internalState.forward.value
                    Box(Modifier.drawCurl(config, forward.top, forward.bottom)) {
                        content(updatedCurrent)
                    }
                }

                if (updatedCurrent > 0) {
                    val backward = internalState.backward.value
                    Box(Modifier.drawCurl(config, backward.top, backward.bottom)) {
                        content(updatedCurrent - 1)
                    }
                }
            }
        }
    }
}

/**
 * Shows the pages which may be turned by drag or tap gestures.
 *
 * @param count The count of pages.
 * @param key The lambda to provide stable key for each item. Useful when adding and removing items before current page.
 * @param modifier The modifier for this composable.
 * @param state The state of the PageCurl. Use this to programmatically change the current page or observe changes.
 * @param config The configuration for PageCurl.
 * @param content The content lambda to provide the page composable. Receives the page number.
 */
@ExperimentalPageCurlApi
@Composable
public fun PageCurl(
    count: Int,
    key: (Int) -> Any,
    modifier: Modifier = Modifier,
    state: PageCurlState = rememberPageCurlState(),
    config: PageCurlConfig = rememberPageCurlConfig(),
    content: @Composable (Int) -> Unit
) {
    var lastKey by remember(state.current) { mutableStateOf(if (count > 0) key(state.current) else null) }

    remember(count) {
        val newKey = if (count > 0) key(state.current) else null
        if (newKey != lastKey) {
            val index = List(count, key).indexOf(lastKey).coerceIn(0, count - 1)
            lastKey = newKey
            state.current = index
        }
        count
    }

    PageCurl(
        count = count,
        state = state,
        config = config,
        content = content,
        modifier = modifier,
    )
}

/**
 * Shows the pages which may be turned by drag or tap gestures.
 *
 * @param state The state of the PageCurl. Use this to programmatically change the current page or observe changes.
 * @param modifier The modifier for this composable.
 * @param content The content lambda to provide the page composable. Receives the page number.
 */
@ExperimentalPageCurlApi
@Composable
@Deprecated("Specify 'max' as 'count' in PageCurl composable.")
public fun PageCurl(
    state: PageCurlState,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    PageCurl(
        count = state.max,
        state = state,
        modifier = modifier,
        content = content,
    )
}
