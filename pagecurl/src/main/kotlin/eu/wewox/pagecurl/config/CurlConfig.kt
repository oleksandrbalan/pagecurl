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

/**
 * The configuration for PageCurl.
 *
 * @property curl Configures how page curl looks like.
 * @property interaction Configures interactions with a page curl. Such as if drag or tap to go on next or previous
 * page is allowed, etc.
 */
@ExperimentalPageCurlApi
public data class PageCurlConfig(
    val curl: CurlConfig = CurlConfig(),
    val interaction: InteractionConfig = InteractionConfig(),
)

/**
 * Configures how page curl looks like.
 *
 * @property backPage Configures how back-page looks like (color, content alpha).
 * @property shadow Configures how page shadow looks like (color, alpha, radius).
 */
@ExperimentalPageCurlApi
public data class CurlConfig(
    val backPage: BackPageConfig = BackPageConfig(),
    val shadow: ShadowConfig = ShadowConfig(),
)

/**
 * Configures how back-page of the page curl looks like.
 *
 * @property color Color of the back-page. In majority of use-cases it should be set to the content background color.
 * @property contentAlpha The alpha which defines how content is "seen through" the back-page. From 0 (nothing is
 * visible) to 1 (everything is visible).
 */
@ExperimentalPageCurlApi
public data class BackPageConfig(
    val color: Color = Color.White,
    val contentAlpha: Float = 0.1f,
)

/**
 * Configures how page shadow looks like.
 *
 * @property color The color of the shadow. In majority of use-cases it should be set to the inverted color to the
 * content background color. Should be a solid color, see [alpha] to adjust opacity.
 * @property alpha The alpha of the [color].
 * @property radius Defines how big the shadow is.
 * @property offset Defines how shadow is shifted from the page. A little shift may add more realism.
 */
@ExperimentalPageCurlApi
public data class ShadowConfig(
    val color: Color = Color.Black,
    val alpha: Float = 0.2f,
    val radius: Dp = 15.dp,
    val offset: DpOffset = DpOffset((-5).dp, 0.dp),
)

/**
 * Configures interactions with a page curl.
 *
 * @property drag Configures drag interactions.
 * @property tap Configures tap interactions.
 */
@ExperimentalPageCurlApi
public data class InteractionConfig(
    val drag: Drag = Drag(),
    val tap: Tap = Tap(),
) {

    /**
     * Configures drag interactions.
     *
     * @property forward Configures forward drag interaction.
     * @property backward Configures backward drag interaction.
     */
    @ExperimentalPageCurlApi
    public data class Drag(
        val forward: Interaction = Interaction(true, rightHalf(), leftHalf()),
        val backward: Interaction = Interaction(true, forward.end, forward.start),
    ) {

        /**
         * The drag interaction setting.
         *
         * @property enabled True if this interaction is enabled or not.
         * @property start Defines a rectangle where interaction should start. The rectangle coordinates are relative
         * (from 0 to 1) and then scaled to the PageCurl bounds.
         * @property end Defines a rectangle where interaction should end. The rectangle coordinates are relative
         * (from 0 to 1) and then scaled to the PageCurl bounds.
         */
        @ExperimentalPageCurlApi
        public data class Interaction(
            val enabled: Boolean,
            val start: Rect = Rect.Zero,
            val end: Rect = Rect.Zero,
        )
    }

    /**
     * Configures tap interactions.
     *
     * @property forward Configures forward tap interaction.
     * @property backward Configures backward tap interaction.
     * @property custom The custom tap interaction. Could be provided to implement custom taps in the PageCurl, e.g. to
     * capture taps in the center, etc.
     */
    @ExperimentalPageCurlApi
    public data class Tap(
        val forward: Interaction = Interaction(true, rightHalf()),
        val backward: Interaction = Interaction(true, leftHalf()),
        val custom: CustomInteraction = CustomInteraction(false)
    ) {

        /**
         * The tap interaction setting.
         *
         * @property enabled True if this interaction is enabled or not.
         * @property target Defines a rectangle where interaction captured. The rectangle coordinates are relative
         * (from 0 to 1) and then scaled to the PageCurl bounds.
         */
        @ExperimentalPageCurlApi
        public data class Interaction(
            val enabled: Boolean,
            val target: Rect = Rect.Zero,
        )

        /**
         * The custom tap interaction setting.
         *
         * @property enabled True if this interaction is enabled or not.
         * @property onTap The lambda to invoke to check if tap is handled by custom tap or not. Receives the density
         * scope, the PageCurl size and tap position. Returns true if tap is handled and false otherwise.
         */
        @ExperimentalPageCurlApi
        public data class CustomInteraction(
            val enabled: Boolean,
            val onTap: Density.(IntSize, Offset) -> Boolean = { _, _ -> false },
        )
    }
}

/**
 * The utility function to create a new copy of the [InteractionConfig] with different enabled states of each
 * interactions.
 *
 * @param dragForwardEnabled True to enable forward drag interaction.
 * @param dragBackwardEnabled True to enable backward drag interaction.
 * @param tapForwardEnabled True to enable forward tap interaction.
 * @param tapBackwardEnabled True to enable backward tap interaction.
 */
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

/**
 * The left half of the PageCurl.
 */
private fun leftHalf(): Rect = Rect(0.0f, 0.0f, 0.5f, 1.0f)

/**
 * The right half of the PageCurl.
 */
private fun rightHalf(): Rect = Rect(0.5f, 0.0f, 1.0f, 1.0f)
