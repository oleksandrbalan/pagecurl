package eu.wewox.pagecurl.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
 * Creates a PageCurlConfig with the default properties and memorizes it.
 *
 * @param backPageColor Color of the back-page. In majority of use-cases it should be set to the content background
 * color.
 * @param backPageContentAlpha The alpha which defines how content is "seen through" the back-page. From 0 (nothing
 * is visible) to 1 (everything is visible).
 * @param shadowColor The color of the shadow. In majority of use-cases it should be set to the inverted color to the
 * content background color. Should be a solid color, see [alpha] to adjust opacity.
 * @param shadowAlpha The alpha of the [color].
 * @param shadowRadius Defines how big the shadow is.
 * @param shadowOffset Defines how shadow is shifted from the page. A little shift may add more realism.
 * @param dragForwardEnabled True if forward drag interaction is enabled or not.
 * @param dragBackwardEnabled True if backward drag interaction is enabled or not.
 * @param tapForwardEnabled True if forward tap interaction is enabled or not.
 * @param tapBackwardEnabled True if backward tap interaction is enabled or not.
 * @param tapCustomEnabled True if custom tap interaction is enabled or not, see [onCustomTap].
 * @param dragForwardInteraction The forward drag interaction setting.
 * @param dragBackwardInteraction The backward drag interaction setting.
 * @param tapForwardInteraction The forward tap interaction setting.
 * @param tapBackwardInteraction The backward tap interaction setting.
 * @param onCustomTap The lambda to invoke to check if tap is handled by custom tap or not. Receives the density
 * scope, the PageCurl size and tap position. Returns true if tap is handled and false otherwise.
 */
@ExperimentalPageCurlApi
@Composable
public fun rememberPageCurlConfig(
    backPageColor: Color = Color.White,
    backPageContentAlpha: Float = 0.1f,
    shadowColor: Color = Color.Black,
    shadowAlpha: Float = 0.2f,
    shadowRadius: Dp = 15.dp,
    shadowOffset: DpOffset = DpOffset((-5).dp, 0.dp),
    dragForwardEnabled: Boolean = true,
    dragBackwardEnabled: Boolean = true,
    tapForwardEnabled: Boolean = true,
    tapBackwardEnabled: Boolean = true,
    tapCustomEnabled: Boolean = true,
    dragForwardInteraction: PageCurlConfig.DragInteraction = PageCurlConfig.DragInteraction(rightHalf(), leftHalf()),
    dragBackwardInteraction: PageCurlConfig.DragInteraction = PageCurlConfig.DragInteraction(leftHalf(), rightHalf()),
    tapForwardInteraction: PageCurlConfig.TapInteraction = PageCurlConfig.TapInteraction(rightHalf()),
    tapBackwardInteraction: PageCurlConfig.TapInteraction = PageCurlConfig.TapInteraction(leftHalf()),
    onCustomTap: Density.(IntSize, Offset) -> Boolean = { _, _ -> false },
): PageCurlConfig =
    rememberSaveable(
        saver = listSaver(
            save = {
                listOf(
                    (it.backPageColor.value shr 32).toInt(),
                    it.backPageContentAlpha,
                    (it.shadowColor.value shr 32).toInt(),
                    it.shadowAlpha,
                    it.shadowRadius.value,
                    it.shadowOffset.x.value,
                    it.shadowOffset.y.value,
                    it.dragForwardEnabled,
                    it.dragBackwardEnabled,
                    it.tapForwardEnabled,
                    it.tapBackwardEnabled,
                    it.tapCustomEnabled,
                    it.dragForwardInteraction.start.topLeft.x,
                    it.dragForwardInteraction.start.topLeft.y,
                    it.dragForwardInteraction.start.bottomRight.x,
                    it.dragForwardInteraction.start.bottomRight.y,
                    it.dragForwardInteraction.end.topLeft.x,
                    it.dragForwardInteraction.end.topLeft.y,
                    it.dragForwardInteraction.end.bottomRight.x,
                    it.dragForwardInteraction.end.bottomRight.y,
                    it.dragBackwardInteraction.start.topLeft.x,
                    it.dragBackwardInteraction.start.topLeft.y,
                    it.dragBackwardInteraction.start.bottomRight.x,
                    it.dragBackwardInteraction.start.bottomRight.y,
                    it.dragBackwardInteraction.end.topLeft.x,
                    it.dragBackwardInteraction.end.topLeft.y,
                    it.dragBackwardInteraction.end.bottomRight.x,
                    it.dragBackwardInteraction.end.bottomRight.y,
                    it.tapForwardInteraction.target.topLeft.x,
                    it.tapForwardInteraction.target.topLeft.y,
                    it.tapForwardInteraction.target.bottomRight.x,
                    it.tapForwardInteraction.target.bottomRight.y,
                    it.tapBackwardInteraction.target.topLeft.x,
                    it.tapBackwardInteraction.target.topLeft.y,
                    it.tapBackwardInteraction.target.bottomRight.x,
                    it.tapBackwardInteraction.target.bottomRight.y,
                )
            },
            restore = {
                PageCurlConfig(
                    Color(it[0] as Int),
                    it[1] as Float,
                    Color(it[2] as Int),
                    it[3] as Float,
                    Dp(it[4] as Float),
                    DpOffset(Dp(it[5] as Float), Dp(it[6] as Float)),
                    it[7] as Boolean,
                    it[8] as Boolean,
                    it[9] as Boolean,
                    it[10] as Boolean,
                    it[11] as Boolean,
                    PageCurlConfig.DragInteraction(
                        Rect(it[12] as Float, it[13] as Float, it[14] as Float, it[15] as Float),
                        Rect(it[16] as Float, it[17] as Float, it[18] as Float, it[19] as Float),
                    ),
                    PageCurlConfig.DragInteraction(
                        Rect(it[20] as Float, it[21] as Float, it[22] as Float, it[23] as Float),
                        Rect(it[24] as Float, it[25] as Float, it[26] as Float, it[27] as Float),
                    ),
                    PageCurlConfig.TapInteraction(
                        Rect(it[28] as Float, it[29] as Float, it[30] as Float, it[31] as Float),
                    ),
                    PageCurlConfig.TapInteraction(
                        Rect(it[32] as Float, it[33] as Float, it[34] as Float, it[35] as Float),
                    ),
                    onCustomTap
                )
            }
        )
    ) {
        PageCurlConfig(
            backPageColor = backPageColor,
            backPageContentAlpha = backPageContentAlpha,
            shadowColor = shadowColor,
            shadowAlpha = shadowAlpha,
            shadowRadius = shadowRadius,
            shadowOffset = shadowOffset,
            dragForwardEnabled = dragForwardEnabled,
            dragBackwardEnabled = dragBackwardEnabled,
            tapForwardEnabled = tapForwardEnabled,
            tapBackwardEnabled = tapBackwardEnabled,
            tapCustomEnabled = tapCustomEnabled,
            dragForwardInteraction = dragForwardInteraction,
            dragBackwardInteraction = dragBackwardInteraction,
            tapForwardInteraction = tapForwardInteraction,
            tapBackwardInteraction = tapBackwardInteraction,
            onCustomTap = onCustomTap
        )
    }

/**
 * The configuration for PageCurl.
 *
 * @param backPageColor Color of the back-page. In majority of use-cases it should be set to the content background
 * color.
 * @param backPageContentAlpha The alpha which defines how content is "seen through" the back-page. From 0 (nothing
 * is visible) to 1 (everything is visible).
 * @param shadowColor The color of the shadow. In majority of use-cases it should be set to the inverted color to the
 * content background color. Should be a solid color, see [shadowAlpha] to adjust opacity.
 * @param shadowAlpha The alpha of the [shadowColor].
 * @param shadowRadius Defines how big the shadow is.
 * @param shadowOffset Defines how shadow is shifted from the page. A little shift may add more realism.
 * @param dragForwardEnabled True if forward drag interaction is enabled or not.
 * @param dragBackwardEnabled True if backward drag interaction is enabled or not.
 * @param tapForwardEnabled True if forward tap interaction is enabled or not.
 * @param tapBackwardEnabled True if backward tap interaction is enabled or not.
 * @param tapCustomEnabled True if custom tap interaction is enabled or not, see [onCustomTap].
 * @param dragForwardInteraction The forward drag interaction setting.
 * @param dragBackwardInteraction The backward drag interaction setting.
 * @param tapForwardInteraction The forward tap interaction setting.
 * @param tapBackwardInteraction The backward tap interaction setting.
 * @param onCustomTap The lambda to invoke to check if tap is handled by custom tap or not. Receives the density
 * scope, the PageCurl size and tap position. Returns true if tap is handled and false otherwise.
 */
@ExperimentalPageCurlApi
public class PageCurlConfig(
    backPageColor: Color,
    backPageContentAlpha: Float,
    shadowColor: Color,
    shadowAlpha: Float,
    shadowRadius: Dp,
    shadowOffset: DpOffset,
    dragForwardEnabled: Boolean,
    dragBackwardEnabled: Boolean,
    tapForwardEnabled: Boolean,
    tapBackwardEnabled: Boolean,
    tapCustomEnabled: Boolean,
    dragForwardInteraction: DragInteraction,
    dragBackwardInteraction: DragInteraction,
    tapForwardInteraction: TapInteraction,
    tapBackwardInteraction: TapInteraction,
    public val onCustomTap: Density.(IntSize, Offset) -> Boolean,
) {
    /**
     * The color of the back-page. In majority of use-cases it should be set to the content background color.
     */
    public var backPageColor: Color by mutableStateOf(backPageColor)

    /**
     * The alpha which defines how content is "seen through" the back-page. From 0 (nothing is visible) to
     * 1 (everything is visible).
     */
    public var backPageContentAlpha: Float by mutableStateOf(backPageContentAlpha)

    /**
     * The color of the shadow. In majority of use-cases it should be set to the inverted color to the content
     * background color. Should be a solid color, see [shadowAlpha] to adjust opacity.
     */
    public var shadowColor: Color by mutableStateOf(shadowColor)

    /**
     * The alpha of the [shadowColor].
     */
    public var shadowAlpha: Float by mutableStateOf(shadowAlpha)

    /**
     * Defines how big the shadow is.
     */
    public var shadowRadius: Dp by mutableStateOf(shadowRadius)

    /**
     * Defines how shadow is shifted from the page. A little shift may add more realism.
     */
    public var shadowOffset: DpOffset by mutableStateOf(shadowOffset)

    /**
     * True if forward drag interaction is enabled or not.
     */
    public var dragForwardEnabled: Boolean by mutableStateOf(dragForwardEnabled)

    /**
     * True if backward drag interaction is enabled or not.
     */
    public var dragBackwardEnabled: Boolean by mutableStateOf(dragBackwardEnabled)

    /**
     * True if forward tap interaction is enabled or not.
     */
    public var tapForwardEnabled: Boolean by mutableStateOf(tapForwardEnabled)

    /**
     * True if backward tap interaction is enabled or not.
     */
    public var tapBackwardEnabled: Boolean by mutableStateOf(tapBackwardEnabled)

    /**
     * True if custom tap interaction is enabled or not, see [onCustomTap].
     */
    public var tapCustomEnabled: Boolean by mutableStateOf(tapCustomEnabled)

    /**
     * The forward drag interaction setting.
     */
    public var dragForwardInteraction: DragInteraction by mutableStateOf(dragForwardInteraction)

    /**
     * The backward drag interaction setting.
     */
    public var dragBackwardInteraction: DragInteraction by mutableStateOf(dragBackwardInteraction)

    /**
     * The forward tap interaction setting.
     */
    public var tapForwardInteraction: TapInteraction by mutableStateOf(tapForwardInteraction)

    /**
     * The backward tap interaction setting.
     */
    public var tapBackwardInteraction: TapInteraction by mutableStateOf(tapBackwardInteraction)

    /**
     * The drag interaction setting.
     *
     * @property start Defines a rectangle where interaction should start. The rectangle coordinates are relative
     * (from 0 to 1) and then scaled to the PageCurl bounds.
     * @property end Defines a rectangle where interaction should end. The rectangle coordinates are relative
     * (from 0 to 1) and then scaled to the PageCurl bounds.
     */
    public data class DragInteraction(val start: Rect, val end: Rect)

    /**
     * The tap interaction setting.
     *
     * @property target Defines a rectangle where interaction captured. The rectangle coordinates are relative
     * (from 0 to 1) and then scaled to the PageCurl bounds.
     */
    public data class TapInteraction(val target: Rect)
}

/**
 * The left half of the PageCurl.
 */
private fun leftHalf(): Rect = Rect(0.0f, 0.0f, 0.5f, 1.0f)

/**
 * The right half of the PageCurl.
 */
private fun rightHalf(): Rect = Rect(0.5f, 0.0f, 1.0f, 1.0f)
