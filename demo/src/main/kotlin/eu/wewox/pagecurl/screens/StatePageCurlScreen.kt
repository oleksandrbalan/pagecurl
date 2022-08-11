@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.HowToPageData
import eu.wewox.pagecurl.components.HowToPage
import eu.wewox.pagecurl.components.ZoomOutLayout
import eu.wewox.pagecurl.config.InteractionConfig
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.config.copy
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.PageCurlState
import eu.wewox.pagecurl.page.rememberPageCurlState
import eu.wewox.pagecurl.ui.SpacingLarge
import eu.wewox.pagecurl.ui.SpacingMedium
import kotlinx.coroutines.launch

@Composable
fun StatePageCurlScreen() {
    Box(Modifier.fillMaxSize()) {
        val pages = remember { HowToPageData.interactionHowToPages }
        val state = rememberPageCurlState(max = pages.size)

        var zoomOut by remember { mutableStateOf(false) }

        val interactionConfig = remember {
            InteractionConfig(
                tap = InteractionConfig.Tap(
                    custom = InteractionConfig.Tap.CustomInteraction(true) { size, position ->
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
            )
        }

        ZoomOutLayout(
            zoomOut = zoomOut,
            bottom = { SettingsRow(state) }
        ) {
            // Animate radius and elevation with the same value, because we not :)
            val cornersAndElevation by animateDpAsState(if (zoomOut) 16.dp else 0.dp)

            Card(
                shape = RoundedCornerShape(cornersAndElevation),
                elevation = cornersAndElevation,
            ) {
                PageCurl(
                    state = state,
                    config = PageCurlConfig(
                        interaction = interactionConfig.copy(
                            dragForwardEnabled = !zoomOut,
                            dragBackwardEnabled = !zoomOut,
                            tapForwardEnabled = !zoomOut,
                            tapBackwardEnabled = !zoomOut,
                        )
                    )
                ) { index ->
                    HowToPage(index, pages[index])
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    state: PageCurlState,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SpacingMedium, Alignment.CenterHorizontally),
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(SpacingLarge)
    ) {
        SettingsRowButton("Snap to first") {
            state.snapTo(0)
        }

        SettingsRowButton("Snap to last") {
            state.snapTo(state.max)
        }

        SettingsRowButton("Snap forward") {
            state.snapTo(state.current + 1)
        }

        SettingsRowButton("Snap backward") {
            state.snapTo(state.current - 1)
        }

        SettingsRowButton("Animate forward") {
            state.next()
        }

        SettingsRowButton("Animate backward") {
            state.prev()
        }
    }
}

@Composable
private fun SettingsRowButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = { scope.launch { onClick() } },
        modifier = modifier
    ) {
        Text(text = text)
    }
}
