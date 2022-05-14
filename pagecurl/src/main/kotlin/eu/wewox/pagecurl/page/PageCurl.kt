package eu.wewox.pagecurl.page

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.CurlDirection
import eu.wewox.pagecurl.config.PageCurlConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalPageCurlApi
@Composable
public fun PageCurl(
    current: Int,
    count: Int,
    modifier: Modifier = Modifier,
    onCurrentChange: (Int) -> Unit,
    config: PageCurlConfig = PageCurlConfig(),
    content: @Composable (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val updatedCurrent by rememberUpdatedState(current)

    BoxWithConstraints(modifier) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()
        val left = Curl(Offset(0f, 0f), Offset(0f, maxHeightPx))
        val right = Curl(Offset(maxWidthPx, 0f), Offset(maxWidthPx, maxHeightPx))

        val forward = remember { Animatable(right, Curl.VectorConverter, Curl.VisibilityThreshold) }
        val backward = remember { Animatable(left, Curl.VectorConverter, Curl.VisibilityThreshold) }

        Box(
            Modifier
                .curlGesture(
                    enabled = updatedCurrent < count - 1,
                    scope = scope,
                    direction = config.interaction.forward,
                    start = right,
                    end = left,
                    animatable = forward,
                    onChange = { onCurrentChange(updatedCurrent + 1) }
                )
                .curlGesture(
                    enabled = updatedCurrent > 0,
                    scope = scope,
                    direction = config.interaction.backward,
                    start = left,
                    end = right,
                    animatable = backward,
                    onChange = { onCurrentChange(updatedCurrent - 1) }
                )
        ) {
            if (updatedCurrent + 1 < count) {
                content(updatedCurrent + 1)
            }

            if (updatedCurrent < count) {
                Box(Modifier.drawCurl(config.curl, forward.value.a, forward.value.b)) {
                    content(updatedCurrent)
                }
            }

            if (updatedCurrent > 0) {
                Box(Modifier.drawCurl(config.curl, backward.value.a, backward.value.b)) {
                    content(updatedCurrent - 1)
                }
            }
        }
    }
}

@ExperimentalPageCurlApi
private fun Modifier.curlGesture(
    enabled: Boolean,
    scope: CoroutineScope,
    direction: CurlDirection,
    start: Curl,
    end: Curl,
    animatable: Animatable<Curl, AnimationVector4D>,
    onChange: () -> Unit,
): Modifier =
    curlGesture(
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


private data class Curl(val a: Offset, val b: Offset) {
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
