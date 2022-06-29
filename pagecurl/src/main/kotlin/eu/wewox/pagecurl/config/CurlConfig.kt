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
    val interaction: InteractionConfig = InteractionConfig()
)

@ExperimentalPageCurlApi
public data class InteractionConfig(
    val forward: CurlDirection = CurlDirection(
        Rect(Offset(0.5f, 0.0f), Offset(1.0f, 1.0f)),
        Rect(Offset(0.0f, 0.0f), Offset(0.5f, 1.0f)),
    ),
    val backward: CurlDirection = CurlDirection(forward.end, forward.start),
)

@ExperimentalPageCurlApi
public data class CurlDirection(
    val start: Rect,
    val end: Rect
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
