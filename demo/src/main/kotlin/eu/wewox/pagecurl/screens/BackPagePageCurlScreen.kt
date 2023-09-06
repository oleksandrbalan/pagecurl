@file:OptIn(ExperimentalPageCurlApi::class)
@file:Suppress("MagicNumber")

package eu.wewox.pagecurl.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import eu.wewox.pagecurl.ui.SpacingMedium
import eu.wewox.pagecurl.ui.SpacingSmall

/**
 * Back-Page Configuration in Page Curl.
 * Example how to customize the back-page (the back of the page user see during the drag or animation).
 */
@Composable
fun BackPagePageCurlScreen() {
    Box(Modifier.fillMaxSize()) {
        val pages = remember { HowToPageData.backPageHowToPages }
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
            bottom = { SettingsRow(config) },
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
    config: PageCurlConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingLarge)
    ) {
        Text(
            text = "Alpha",
            modifier = Modifier.padding(horizontal = SpacingLarge)
        )
        Slider(
            value = config.backPageContentAlpha,
            onValueChange = {
                config.backPageContentAlpha = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacingLarge)
        )

        Text(
            text = "Color",
            modifier = Modifier.padding(horizontal = SpacingLarge)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpacingMedium),
            modifier = Modifier
                .padding(top = SpacingSmall)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = SpacingLarge)
        ) {
            backPageColors.forEach { color ->
                Spacer(
                    modifier = Modifier
                        .size(64.dp)
                        .border(2.dp, color, CircleShape)
                        .background(color.copy(alpha = 0.8f), CircleShape)
                        .clip(CircleShape)
                        .clickable {
                            config.backPageColor = color
                        }
                )
            }
        }
    }
}

private val backPageColors: List<Color> = listOf(
    Color(0xFFF9CEEE),
    Color(0xFF68A7AD),
    Color(0xFFE5CB9F),
    Color(0xFFAC7D88),
    Color(0xFF9ADCFF),
    Color(0xFFFFF89A),
    Color(0xFFCDB699),
    Color(0xFFA267AC),
)
