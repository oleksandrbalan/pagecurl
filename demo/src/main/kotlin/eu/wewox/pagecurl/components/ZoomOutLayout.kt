package eu.wewox.pagecurl.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntSize

@Composable
fun ZoomOutLayout(
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
