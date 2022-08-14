package eu.wewox.pagecurl.page

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.utils.multiply
import eu.wewox.pagecurl.utils.rotate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI

@ExperimentalPageCurlApi
internal fun Modifier.curlGesture(
    state: PageCurlState.InternalState,
    enabled: Boolean,
    scope: CoroutineScope,
    targetStart: Rect,
    targetEnd: Rect,
    edgeStart: Edge,
    edgeEnd: Edge,
    edge: Animatable<Edge, AnimationVector4D>,
    onChange: () -> Unit,
): Modifier =
    curlGesture(
        key = state,
        enabled = enabled,
        targetStart = targetStart,
        targetEnd = targetEnd,
        onStart = {
            scope.launch {
                state.animateJob?.cancel()
                edge.snapTo(edgeStart)
            }
        },
        onCurl = { a, b ->
            scope.launch {
                edge.animateTo(Edge(a, b))
            }
        },
        onEnd = {
            scope.launch {
                try {
                    edge.animateTo(edgeEnd)
                } finally {
                    onChange()
                    edge.snapTo(edgeStart)
                }
            }
        },
        onCancel = {
            scope.launch {
                edge.animateTo(edgeStart)
            }
        },
    )

@ExperimentalPageCurlApi
internal fun Modifier.curlGesture(
    key: Any?,
    enabled: Boolean,
    targetStart: Rect,
    targetEnd: Rect,
    onStart: () -> Unit,
    onCurl: (Offset, Offset) -> Unit,
    onEnd: () -> Unit,
    onCancel: () -> Unit,
): Modifier = pointerInput(enabled, key) {
    if (!enabled) {
        return@pointerInput
    }

    // Use velocity tracker to support flings
    val velocityTracker = VelocityTracker()
    val startRect by lazy { targetStart.multiply(size) }
    val endRect by lazy { targetEnd.multiply(size) }
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (!startRect.contains(down.position)) {
                return@awaitPointerEventScope
            }

            // Change X position to be always on the right side for more convenient gesture tracking
            val dragStart = down.position.copy(x = size.width.toFloat())

            onStart()

            var dragCurrent = dragStart
            drag(down.id) { change ->
                dragCurrent = change.position
                velocityTracker.addPosition(System.currentTimeMillis(), dragCurrent)
                change.consume()
                val vector = (dragStart - dragCurrent).rotate(PI.toFloat() / 2)
                onCurl(dragCurrent - vector, dragCurrent + vector)
            }

            if (dragCurrent == dragStart) {
                onCancel()
                return@awaitPointerEventScope
            }

            val velocity = velocityTracker.calculateVelocity()
            val decay = splineBasedDecay<Offset>(this)
            val target = decay.calculateTargetValue(
                Offset.VectorConverter,
                dragCurrent,
                Offset(velocity.x, velocity.y)
            ).let {
                Offset(
                    it.x.coerceIn(0f, size.width.toFloat() - 1),
                    it.y.coerceIn(0f, size.height.toFloat() - 1)
                )
            }

            if (endRect.contains(target)) {
                onEnd()
            } else {
                onCancel()
            }
        }
    }
}
