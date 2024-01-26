@file:Suppress("MatchingDeclarationName")

package eu.wewox.pagecurl.page

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.utils.rotate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI

internal data class DragConfig(
    val edge: Animatable<Edge, AnimationVector4D>,
    val start: Edge,
    val end: Edge,
    val isEnabled: () -> Boolean,
    val isDragSucceed: (Offset, Offset) -> Boolean,
    val onChange: () -> Unit,
)

@ExperimentalPageCurlApi
internal suspend fun PointerInputScope.detectCurlGestures(
    scope: CoroutineScope,
    getConfig: (Offset, Offset) -> DragConfig?,
) {
    // Use velocity tracker to support flings
    val velocityTracker = VelocityTracker()

    var config: DragConfig? = null
    var startOffset: Offset = Offset.Zero

    detectCustomDragGestures(
        onDragStart = { start, end ->
            startOffset = start
            config = getConfig(start, end)
            config != null
        },
        onDragEnd = { endOffset, complete ->
            config?.apply {
                val velocity = velocityTracker.calculateVelocity()
                val decay = splineBasedDecay<Offset>(this@detectCurlGestures)
                val flingEndOffset = decay.calculateTargetValue(
                    Offset.VectorConverter,
                    endOffset,
                    Offset(velocity.x, velocity.y)
                ).let {
                    Offset(
                        it.x.coerceIn(0f, size.width.toFloat() - 1),
                        it.y.coerceIn(0f, size.height.toFloat() - 1)
                    )
                }

                scope.launch {
                    if (complete && isDragSucceed(startOffset, flingEndOffset)) {
                        try {
                            edge.animateTo(end)
                        } finally {
                            onChange()
                            edge.snapTo(start)
                        }
                    } else {
                        try {
                            edge.animateTo(start)
                        } finally {
                            edge.snapTo(start)
                        }
                    }
                }
            }
        },
        onDrag = { change, _ ->
            config?.apply {
                if (!isEnabled()) {
                    throw CancellationException()
                }

                velocityTracker.addPosition(System.currentTimeMillis(), change.position)

                scope.launch {
                    val vector = Offset(size.width.toFloat(), startOffset.y) - change.position
                    val rotatedVector = vector.rotate(PI.toFloat() / 2)
                    edge.animateTo(Edge(change.position - rotatedVector, change.position + rotatedVector))
                }
            }
        }
    )
}

internal suspend fun PointerInputScope.detectCustomDragGestures(
    onDragStart: (Offset, Offset) -> Boolean,
    onDragEnd: (Offset, Boolean) -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var drag: PointerInputChange?
        var overSlop = Offset.Zero
        do {
            drag = awaitTouchSlopOrCancellation(down.id) { change, over ->
                change.consume()
                overSlop = over
            }
        } while (drag != null && !drag.isConsumed)
        if (drag != null) {
            if (!onDragStart.invoke(down.position, drag.position)) {
                return@awaitEachGesture
            }
            onDrag(drag, overSlop)
            val completed = drag(drag.id) {
                drag = it
                onDrag(it, it.positionChange())
                it.consume()
            }
            onDragEnd(drag?.position ?: down.position, completed)
        }
    }
}
