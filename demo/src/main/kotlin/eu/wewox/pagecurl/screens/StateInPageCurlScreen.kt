@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
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
import eu.wewox.pagecurl.config.rememberPageCurlConfig
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.PageCurlState
import eu.wewox.pagecurl.page.rememberPageCurlState
import eu.wewox.pagecurl.ui.SpacingLarge
import eu.wewox.pagecurl.ui.SpacingMedium
import kotlinx.coroutines.launch

/**
 * Page Curl With State Management.
 * Example how state can be used to change current page (snap / animate).
 */
@Composable
fun StateInPageCurlScreen() {
    Box(Modifier.fillMaxSize()) {
        val pages = remember { HowToPageData.stateHowToPages }
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

        ZoomOutLayout(
            zoomOut = zoomOut,
            config = config,
            bottom = { SettingsRow(pages.size, state) },
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
private fun SettingsRow(
    max: Int,
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
            state.snapTo(max)
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
