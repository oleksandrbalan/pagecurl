@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.HowToPageData
import eu.wewox.pagecurl.components.HowToPage
import eu.wewox.pagecurl.components.ZoomOutLayout
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.config.rememberPageCurlConfig
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.rememberPageCurlState
import eu.wewox.pagecurl.ui.SpacingMedium
import eu.wewox.pagecurl.ui.SpacingSmall

/**
 * Interactions Configurations In Page Curl.
 * Example interactions (drag / tap) can be customized.
 */
@Composable
fun InteractionConfigInPageCurlScreen() {
    Box(Modifier.fillMaxSize()) {
        val pages = remember { HowToPageData.interactionSettingsHowToPages }
        var zoomOut by remember { mutableStateOf(false) }
        val state = rememberPageCurlState()
        val config = rememberPageCurlConfig(
            onCustomTap = { size, position ->
                // When PageCurl is zoomed out then zoom back in
                // Else detect tap somewhere in the center with 64 radius and zoom out a PageCurl
                if (zoomOut) {
                    zoomOut = false
                    true
                } else if ((position - size.center.toOffset()).getDistance() < 64.dp.toPx()) {
                    zoomOut = true
                    true
                } else {
                    false
                }
            }
        )

        var selectedOption by remember { mutableStateOf(InteractionOption.entries.first()) }
        val interactionSettings = remember { InteractionSettings() }

        LaunchedEffect(
            selectedOption,
            interactionSettings.tap,
            interactionSettings.dragStartEnd,
            interactionSettings.dragGesture,
        ) {
            config.dragInteraction = when (selectedOption) {
                InteractionOption.DragRegion -> interactionSettings.dragStartEnd
                InteractionOption.DragGesture -> interactionSettings.dragGesture
                InteractionOption.Tap -> config.dragInteraction
            }

            config.tapInteraction = interactionSettings.tap
        }

        ZoomOutLayout(
            zoomOut = zoomOut,
            config = config,
            top = {
                SettingsOptionsRow(
                    selectedOption = selectedOption,
                    onSelectedOptionChange = { selectedOption = it }
                )
            },
            bottom = {
                SettingsRowSlider(
                    interactionSettings = interactionSettings,
                    selectedOption = selectedOption,
                )
            },
        ) {
            PageCurl(
                count = pages.size,
                state = state,
                config = config,
            ) { index ->
                HowToPage(index, pages[index])
            }
        }
    }
}

@Composable
private fun SettingsOptionsRow(
    selectedOption: InteractionOption,
    onSelectedOptionChange: (InteractionOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SpacingSmall),
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingMedium)
            .selectableGroup()
    ) {
        Text(
            text = "Interaction setting",
            style = MaterialTheme.typography.titleLarge
        )

        InteractionOption.entries.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = selectedOption == option,
                        onClick = { onSelectedOptionChange(option) },
                        role = Role.RadioButton
                    ),
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = null
                )
                Text(
                    text = option.name,
                    modifier = Modifier.padding(start = SpacingMedium)
                )
            }
        }
    }
}

@Composable
private fun SettingsRowSlider(
    selectedOption: InteractionOption,
    interactionSettings: InteractionSettings,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = selectedOption,
        label = "Sliders animation",
        modifier = modifier,
    ) { option ->
        when (option) {
            InteractionOption.Tap -> {
                DoubleSliders(
                    value = interactionSettings.tap.forward.target.left,
                    onValueChange = { interactionSettings.tap = createTapTargetInteraction(it) }
                )
            }

            InteractionOption.DragRegion -> {
                DoubleSliders(
                    value = interactionSettings.dragStartEnd.forward.start.left,
                    onValueChange = { interactionSettings.dragStartEnd = createDragStartEndInteraction(it) }
                )
            }

            InteractionOption.DragGesture -> {
                val from = interactionSettings.dragGesture.forward.target.left
                val to = interactionSettings.dragGesture.forward.target.right
                RangeSlider(
                    value = from..to,
                    onValueChange = { range ->
                        interactionSettings.dragGesture = createDragGestureInteraction(range.start, range.endInclusive)
                    },
                    modifier = Modifier.padding(horizontal = SpacingMedium)
                )
            }
        }
    }
}

@Composable
private fun DoubleSliders(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = SpacingMedium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
            )

            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
            )

            Slider(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private class InteractionSettings {

    var tap by mutableStateOf(createTapTargetInteraction(0.5f))

    var dragStartEnd by mutableStateOf(createDragStartEndInteraction(0.5f))

    var dragGesture by mutableStateOf(createDragGestureInteraction(0f, 1f))
}

private fun createTapTargetInteraction(value: Float): PageCurlConfig.TargetTapInteraction =
    PageCurlConfig.TargetTapInteraction(
        forward = PageCurlConfig.TargetTapInteraction.Config(
            Rect(value, 0.0f, 1.0f, 1.0f),
        ),
        backward = PageCurlConfig.TargetTapInteraction.Config(
            Rect(0.0f, 0.0f, value, 1.0f),
        )
    )

private fun createDragStartEndInteraction(value: Float): PageCurlConfig.StartEndDragInteraction =
    PageCurlConfig.StartEndDragInteraction(
        forward = PageCurlConfig.StartEndDragInteraction.Config(
            Rect(value, 0.0f, 1.0f, 1.0f),
            Rect(0.0f, 0.0f, value, 1.0f),
        ),
        backward = PageCurlConfig.StartEndDragInteraction.Config(
            Rect(0.0f, 0.0f, value, 1.0f),
            Rect(value, 0.0f, 1.0f, 1.0f),
        )
    )

private fun createDragGestureInteraction(from: Float, to: Float): PageCurlConfig.GestureDragInteraction =
    PageCurlConfig.GestureDragInteraction(
        forward = PageCurlConfig.GestureDragInteraction.Config(
            Rect(from, 0.0f, to, 1.0f),
        ),
        backward = PageCurlConfig.GestureDragInteraction.Config(
            Rect(from, 0.0f, to, 1.0f),
        )
    )

private enum class InteractionOption { Tap, DragRegion, DragGesture }
