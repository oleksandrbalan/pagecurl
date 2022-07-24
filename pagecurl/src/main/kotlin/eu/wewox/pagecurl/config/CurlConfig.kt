package eu.wewox.pagecurl.config

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import eu.wewox.pagecurl.ExperimentalPageCurlApi

@ExperimentalPageCurlApi
public data class PageCurlConfig(
    val curl: CurlConfig = CurlConfig(),
    val interaction: InteractionConfig = InteractionConfig(),
)

@ExperimentalPageCurlApi
public data class CurlConfig(
    val backPage: BackPageConfig = BackPageConfig(),
    val shadow: ShadowConfig = ShadowConfig(),
)

@ExperimentalPageCurlApi
public data class BackPageConfig(
    val color: Color = Color.White,
    val contentAlpha: Float = 0.1f,
)

@ExperimentalPageCurlApi
public data class ShadowConfig(
    val color: Color = Color.Black,
    val alpha: Float = 0.2f,
    val radius: Dp = 15.dp,
    val offset: DpOffset = DpOffset((-5).dp, 0.dp),
)

@ExperimentalPageCurlApi
public data class InteractionConfig(
    val drag: Drag = Drag(),
    val tap: Tap = Tap(),
) {
    @ExperimentalPageCurlApi
    public data class Drag(
        val forward: Interaction = Interaction(true, rightHalf(), leftHalf()),
        val backward: Interaction = Interaction(true, forward.end, forward.start),
    ) {
        @ExperimentalPageCurlApi
        public data class Interaction(
            val enabled: Boolean,
            val start: Rect = Rect.Zero,
            val end: Rect = Rect.Zero,
        )
    }

    @ExperimentalPageCurlApi
    public data class Tap(
        val forward: Interaction = Interaction(true, rightHalf()),
        val backward: Interaction = Interaction(true, leftHalf()),
        val custom: CustomInteraction = CustomInteraction(false)
    ) {
        @ExperimentalPageCurlApi
        public data class Interaction(
            val enabled: Boolean,
            val target: Rect = Rect.Zero,
        )

        @ExperimentalPageCurlApi
        public data class CustomInteraction(
            val enabled: Boolean,
            val onTap: Density.(IntSize, Offset) -> Boolean = { _, _ -> false },
        )
    }
}

@ExperimentalPageCurlApi
public fun InteractionConfig.copy(
    dragForwardEnabled: Boolean = drag.forward.enabled,
    dragBackwardEnabled: Boolean = drag.backward.enabled,
    tapForwardEnabled: Boolean = tap.forward.enabled,
    tapBackwardEnabled: Boolean = tap.backward.enabled,
): InteractionConfig = copy(
    drag = drag.copy(
        forward = drag.forward.copy(enabled = dragForwardEnabled),
        backward = drag.backward.copy(enabled = dragBackwardEnabled)
    ),
    tap = tap.copy(
        forward = tap.forward.copy(enabled = tapForwardEnabled),
        backward = tap.backward.copy(enabled = tapBackwardEnabled)
    )
)

private fun leftHalf(): Rect = Rect(0.0f, 0.0f, 0.5f, 1.0f)

private fun rightHalf(): Rect = Rect(0.5f, 0.0f, 1.0f, 1.0f)
