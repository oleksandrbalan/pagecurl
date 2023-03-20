@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.config.PageCurlConfig

/**
 * Layout which could be zoomed out and zoomed in to show / hide the [bottom] bar.
 *
 * @param zoomOut True when layout is zoomed out.
 * @param config The [PageCurlConfig] to turn off interactions in the page curl.
 * @param bottom The content of the bottom bar.
 * @param modifier The modifier for this composable.
 * @param pageCurl The content where PageCurl should be placed.
 */
@Composable
fun ZoomOutLayout(
    zoomOut: Boolean,
    config: PageCurlConfig,
    bottom: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    pageCurl: @Composable () -> Unit,
) {
    // Disable all state interactions when PageCurl is zoomed out
    LaunchedEffect(zoomOut, config) {
        with(config) {
            dragForwardEnabled = !zoomOut
            dragBackwardEnabled = !zoomOut
            tapForwardEnabled = !zoomOut
            tapBackwardEnabled = !zoomOut
        }
    }

    ZoomOutLayout(
        zoomOut = zoomOut,
        bottom = bottom,
        modifier = modifier,
    ) {
        // Animate radius and elevation with the same value, because why not :)
        val cornersAndElevation by animateDpAsState(if (zoomOut) 16.dp else 0.dp)

        if (cornersAndElevation != 0.dp) {
            Card(
                shape = RoundedCornerShape(cornersAndElevation),
                elevation = cornersAndElevation,
                content = pageCurl,
            )
        } else {
            pageCurl()
        }
    }
}

@Composable
private fun ZoomOutLayout(
    zoomOut: Boolean,
    bottom: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            content()
            Box {
                AnimatedVisibility(
                    visible = zoomOut,
                    enter = expandIn(expandFrom = Alignment.Center, initialSize = { IntSize(it.width, 0) }),
                    exit = shrinkOut(shrinkTowards = Alignment.Center, targetSize = { IntSize(it.width, 0) })
                ) {
                    bottom()
                }
            }
        },
        measurePolicy = { measurables, constraints ->
            val (contentMeasurable, bottomMeasurable) = measurables

            val bottomPlaceable = bottomMeasurable.measure(constraints)
            val contentPlaceable = contentMeasurable.measure(constraints)

            layout(constraints.maxWidth, constraints.maxHeight) {
                bottomPlaceable.place(x = 0, y = constraints.maxHeight - bottomPlaceable.height)

                contentPlaceable.placeWithLayer(0, 0) {
                    val height = constraints.maxHeight - 2 * bottomPlaceable.height
                    val scale = height / contentPlaceable.height.toFloat()
                    scaleX = scale
                    scaleY = scale
                }
            }
        }
    )
}
