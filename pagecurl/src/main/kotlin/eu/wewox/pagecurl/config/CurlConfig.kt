package eu.wewox.pagecurl.config

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import eu.wewox.pagecurl.ExperimentalPageCurlApi

@ExperimentalPageCurlApi
public data class PageCurlConfig(
    val curl: CurlConfig = CurlConfig(),
    val direction: PageCurlDirection = PageCurlDirection.StartToEnd,
    val interaction: InteractionConfig = InteractionConfig(forward = direction.forward()),
)

@ExperimentalPageCurlApi
public data class InteractionConfig(
    val forward: DragDirection,
    val backward: DragDirection = DragDirection(forward.end, forward.start),
)

@ExperimentalPageCurlApi
public data class DragDirection(
    val start: Rect,
    val end: Rect,
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
public enum class PageCurlDirection {
    StartToEnd,
    // TODO (Alex) Add support for reversed end-to-start direction
    //  EndToStart,
}

private fun left(): Rect = Rect(Offset(0.0f, 0.0f), Offset(0.5f, 1.0f))

private fun right(): Rect = Rect(Offset(0.5f, 0.0f), Offset(1.0f, 1.0f))

@ExperimentalPageCurlApi
private fun PageCurlDirection.forward(): DragDirection =
    when (this) {
        PageCurlDirection.StartToEnd -> DragDirection(right(), left())
    }
