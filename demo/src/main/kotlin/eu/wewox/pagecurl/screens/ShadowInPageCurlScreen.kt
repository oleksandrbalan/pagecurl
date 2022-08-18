@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
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
import eu.wewox.pagecurl.ui.SpacingLarge

@Composable
fun ShadowInPageCurlScreen() {
    Box(Modifier.fillMaxSize()) {
        val pages = remember { HowToPageData.shadowHowToPages }
        var zoomOut by remember { mutableStateOf(false) }
        val state = rememberPageCurlState(
            max = pages.size,
            config = rememberPageCurlConfig(
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
        )

        ZoomOutLayout(
            zoomOut = zoomOut,
            config = state.config,
            bottom = { SettingsRow(state.config) },
        ) {
            PageCurl(state = state) { index ->
                HowToPage(index, pages[index])
            }
        }
    }
}

@Composable
private fun SettingsRow(
    config: PageCurlConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingLarge)
    ) {
        Text(text = "Alpha")
        Slider(
            value = config.shadowAlpha,
            onValueChange = {
                config.shadowAlpha = it
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Radius")
        Slider(
            value = config.shadowRadius.value,
            onValueChange = {
                config.shadowRadius = it.dp
            },
            valueRange = 0f..32f,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Horizontal offset")
        Slider(
            value = config.shadowOffset.x.value,
            onValueChange = {
                config.shadowOffset = DpOffset(it.dp, 0.dp)
            },
            valueRange = -20f..20f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
