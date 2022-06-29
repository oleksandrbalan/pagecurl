package eu.wewox.pagecurl.page

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Constraints
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.CurlDirection
import eu.wewox.pagecurl.config.PageCurlConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
                    key = internalState,
                    enabled = updatedCurrent < state.max - 1,
                    scope = scope,
                    direction = config.interaction.forward,
                    start = internalState.rightCurl,
                    end = internalState.leftCurl,
                    animatable = internalState.forward,
                    onChange = { state.current = updatedCurrent + 1 }
                )
                .curlGesture(
                    key = internalState,
                    enabled = updatedCurrent > 0,
                    scope = scope,
                    direction = config.interaction.backward,
                    start = internalState.leftCurl,
                    end = internalState.rightCurl,
                    animatable = internalState.backward,
                    onChange = { state.current = updatedCurrent - 1 }
                )
        ) {
            if (updatedCurrent + 1 < state.max) {
                content(updatedCurrent + 1)
            }

            if (updatedCurrent < state.max) {
                Box(Modifier.drawCurl(config.curl, internalState.forward.value.a, internalState.forward.value.b)) {
                    content(updatedCurrent)
                }
            }

            if (updatedCurrent > 0) {
                Box(Modifier.drawCurl(config.curl, internalState.backward.value.a, internalState.backward.value.b)) {
                    content(updatedCurrent - 1)
                }
            }
        }
    }
}

@ExperimentalPageCurlApi
@Composable
public fun rememberPageCurlState(
    max: Int,
    initialCurrent: Int = 0,
): PageCurlState =
    rememberSaveable(
        max, initialCurrent,
        saver = Saver(
            save = { it.current },
            restore = {
                PageCurlState(
                    max = it,
                    initialCurrent = initialCurrent
                )
            }
        )
    ) {
        PageCurlState(
            max = max,
            initialCurrent = initialCurrent
        )
    }

@ExperimentalPageCurlApi
public class PageCurlState(
    public val max: Int,
    initialCurrent: Int = 0,
) {
    public var current: Int by mutableStateOf(initialCurrent)
        internal set

    internal var internalState: InternalState? by mutableStateOf(null)

    internal fun setup(constraints: Constraints) {
        if (internalState?.constraints == constraints) {
            return
        }

        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        val left = Curl(Offset(0f, 0f), Offset(0f, maxHeightPx))
        val right = Curl(Offset(maxWidthPx, 0f), Offset(maxWidthPx, maxHeightPx))

        val forward = Animatable(right, Curl.VectorConverter, Curl.VisibilityThreshold)
        val backward = Animatable(left, Curl.VectorConverter, Curl.VisibilityThreshold)

        internalState = InternalState(constraints, left, right, forward, backward)
    }

    public suspend fun snapTo(value: Int) {
        internalState?.reset()
        current = value
    }

    internal data class InternalState(
        val constraints: Constraints,
        val leftCurl: Curl,
        val rightCurl: Curl,
        val forward: Animatable<Curl, AnimationVector4D>,
        val backward: Animatable<Curl, AnimationVector4D>,
    ) {
        internal suspend fun reset() {
            forward.snapTo(rightCurl)
            backward.snapTo(leftCurl)
        }
    }
}

@ExperimentalPageCurlApi
private fun Modifier.curlGesture(
    key: Any?,
    enabled: Boolean,
    scope: CoroutineScope,
    direction: CurlDirection,
    start: Curl,
    end: Curl,
    animatable: Animatable<Curl, AnimationVector4D>,
    onChange: () -> Unit,
): Modifier =
    curlGesture(
        key = key,
        enabled = enabled,
        direction = direction,
        onStart = {
            scope.launch {
                animatable.snapTo(start)
            }
        },
        onCurl = { a, b ->
            scope.launch {
                animatable.animateTo(Curl(a, b))
            }
        },
        onEnd = {
            scope.launch {
                try {
                    animatable.animateTo(end)
                } finally {
                    onChange()
                    animatable.snapTo(start)
                }
            }
        },
        onCancel = {
            scope.launch {
                animatable.animateTo(start)
            }
        },
    )

internal data class Curl(val a: Offset, val b: Offset) {
    companion object {
        val VectorConverter: TwoWayConverter<Curl, AnimationVector4D> =
            TwoWayConverter(
                convertToVector = { AnimationVector4D(it.a.x, it.a.y, it.b.x, it.b.y) },
                convertFromVector = { Curl(Offset(it.v1, it.v2), Offset(it.v3, it.v4)) }
            )

        val VisibilityThreshold: Curl =
            Curl(Offset.VisibilityThreshold, Offset.VisibilityThreshold)
    }
}
