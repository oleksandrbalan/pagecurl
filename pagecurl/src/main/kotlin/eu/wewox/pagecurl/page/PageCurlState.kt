package eu.wewox.pagecurl.page

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.config.rememberPageCurlConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Remembers the [PageCurlState].
 *
 * @param initialCurrent The initial current page.
 * @return The remembered [PageCurlState].
 */
@ExperimentalPageCurlApi
@Composable
public fun rememberPageCurlState(
    initialCurrent: Int = 0,
): PageCurlState =
    rememberSaveable(
        initialCurrent,
        saver = Saver(
            save = { it.current },
            restore = { PageCurlState(initialCurrent = it) }
        )
    ) {
        PageCurlState(
            initialCurrent = initialCurrent,
        )
    }

/**
 * Remembers the [PageCurlState].
 *
 * @param initialCurrent The initial current page.
 * @param config The configuration for PageCurl.
 * @return The remembered [PageCurlState].
 */
@ExperimentalPageCurlApi
@Composable
@Deprecated(
    message = "Specify 'config' as 'config' in PageCurl composable.",
    level = DeprecationLevel.ERROR,
)
@Suppress("UnusedPrivateMember")
public fun rememberPageCurlState(
    initialCurrent: Int = 0,
    config: PageCurlConfig,
): PageCurlState =
    rememberSaveable(
        initialCurrent,
        saver = Saver(
            save = { it.current },
            restore = { PageCurlState(initialCurrent = it) }
        )
    ) {
        PageCurlState(
            initialCurrent = initialCurrent,
        )
    }

/**
 * Remembers the [PageCurlState].
 *
 * @param max The max number of pages.
 * @param initialCurrent The initial current page.
 * @param config The configuration for PageCurl.
 * @return The remembered [PageCurlState].
 */
@ExperimentalPageCurlApi
@Composable
@Deprecated(
    message = "Specify 'max' as 'count' in PageCurl composable and 'config' as 'config' in PageCurl composable.",
    level = DeprecationLevel.ERROR,
)
@Suppress("UnusedPrivateMember")
public fun rememberPageCurlState(
    max: Int,
    initialCurrent: Int = 0,
    config: PageCurlConfig = rememberPageCurlConfig()
): PageCurlState =
    rememberSaveable(
        max, initialCurrent,
        saver = Saver(
            save = { it.current },
            restore = {
                PageCurlState(
                    initialCurrent = it,
                    initialMax = max,
                )
            }
        )
    ) {
        PageCurlState(
            initialCurrent = initialCurrent,
            initialMax = max,
        )
    }

/**
 * The state of the PageCurl.
 *
 * @param initialMax The initial max number of pages.
 * @param initialCurrent The initial current page.
 */
@ExperimentalPageCurlApi
public class PageCurlState(
    initialMax: Int = 0,
    initialCurrent: Int = 0,
) {
    /**
     * The observable current page.
     */
    public var current: Int by mutableStateOf(initialCurrent)
        internal set

    /**
     * The observable progress as page is turned.
     * When going forward it changes from 0 to 1, when going backward it is going from 0 to -1.
     */
    public val progress: Float get() = internalState?.progress ?: 0f

    internal var max: Int = initialMax
        private set

    internal var internalState: InternalState? by mutableStateOf(null)
        private set

    internal fun setup(count: Int, constraints: Constraints) {
        max = count
        if (current >= count) {
            current = (count - 1).coerceAtLeast(0)
        }

        if (internalState?.constraints == constraints) {
            return
        }

        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        val left = Edge(Offset(0f, 0f), Offset(0f, maxHeightPx))
        val right = Edge(Offset(maxWidthPx, 0f), Offset(maxWidthPx, maxHeightPx))

        val forward = Animatable(right, Edge.VectorConverter, Edge.VisibilityThreshold)
        val backward = Animatable(left, Edge.VectorConverter, Edge.VisibilityThreshold)

        internalState = InternalState(constraints, left, right, forward, backward)
    }

    /**
     * Instantly snaps the state to the given page.
     *
     * @param value The page to snap to.
     */
    public suspend fun snapTo(value: Int) {
        current = value.coerceIn(0, max - 1)
        internalState?.reset()
    }

    /**
     * Go forward with an animation.
     *
     * @param block The animation block to animate a change.
     */
    public suspend fun next(block: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = DefaultNext) {
        internalState?.animateTo(
            target = { current + 1 },
            animate = { forward.block(it) }
        )
    }

    /**
     * Go backward with an animation.
     *
     * @param block The animation block to animate a change.
     */
    public suspend fun prev(block: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = DefaultPrev) {
        internalState?.animateTo(
            target = { current - 1 },
            animate = { backward.block(it) }
        )
    }

    internal inner class InternalState(
        val constraints: Constraints,
        val leftEdge: Edge,
        val rightEdge: Edge,
        val forward: Animatable<Edge, AnimationVector4D>,
        val backward: Animatable<Edge, AnimationVector4D>,
    ) {

        var animateJob: Job? = null

        val progress: Float by derivedStateOf {
            if (forward.value != rightEdge) {
                1f - forward.value.centerX / constraints.maxWidth
            } else if (backward.value != leftEdge) {
                -backward.value.centerX / constraints.maxWidth
            } else {
                0f
            }
        }

        suspend fun reset() {
            forward.snapTo(rightEdge)
            backward.snapTo(leftEdge)
        }

        suspend fun animateTo(
            target: () -> Int,
            animate: suspend InternalState.(Size) -> Unit
        ) {
            animateJob?.cancel()

            val targetIndex = target()
            if (targetIndex < 0 || targetIndex >= max) {
                return
            }

            coroutineScope {
                animateJob = launch {
                    try {
                        reset()
                        animate(Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat()))
                    } finally {
                        withContext(NonCancellable) {
                            snapTo(target())
                        }
                    }
                }
            }
        }
    }
}

/**
 * The wrapper to represent a line with 2 points: [top] and [bottom].
 */
public data class Edge(val top: Offset, val bottom: Offset) {

    internal val centerX: Float = (top.x + bottom.x) * 0.5f

    internal companion object {
        val VectorConverter: TwoWayConverter<Edge, AnimationVector4D> =
            TwoWayConverter(
                convertToVector = { AnimationVector4D(it.top.x, it.top.y, it.bottom.x, it.bottom.y) },
                convertFromVector = { Edge(Offset(it.v1, it.v2), Offset(it.v3, it.v4)) }
            )

        val VisibilityThreshold: Edge =
            Edge(Offset.VisibilityThreshold, Offset.VisibilityThreshold)
    }
}

private val DefaultNext: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = { size ->
    animateTo(
        targetValue = size.start,
        animationSpec = keyframes {
            durationMillis = DefaultAnimDuration
            size.end at 0
            size.middle at DefaultMidPointDuration
        }
    )
}

private val DefaultPrev: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = { size ->
    animateTo(
        targetValue = size.end,
        animationSpec = keyframes {
            durationMillis = DefaultAnimDuration
            size.start at 0
            size.middle at DefaultAnimDuration - DefaultMidPointDuration
        }
    )
}

private const val DefaultAnimDuration: Int = 450
private const val DefaultMidPointDuration: Int = 150

private val Size.start: Edge
    get() = Edge(Offset(0f, 0f), Offset(0f, height))

private val Size.middle: Edge
    get() = Edge(Offset(width, height / 2f), Offset(width / 2f, height))

private val Size.end: Edge
    get() = Edge(Offset(width, height), Offset(width, height))
