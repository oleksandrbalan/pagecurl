@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.HowToPageData
import eu.wewox.pagecurl.components.HowToPage
import eu.wewox.pagecurl.components.SettingsPopup
import eu.wewox.pagecurl.config.InteractionConfig
import eu.wewox.pagecurl.config.PageCurlConfig
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.rememberPageCurlState

@Composable
fun SettingsPageCurlScreen() {
    Box(Modifier.fillMaxSize()) {
        val pages = remember { HowToPageData.interactionHowToPages }
        val state = rememberPageCurlState(max = pages.size)

        var showPopup by remember { mutableStateOf(false) }

        // Create a mutable interaction config with custom tap interaction
        // In SettingsPopup config is mutated
        var interaction by remember {
            mutableStateOf(
                InteractionConfig(
                    tap = InteractionConfig.Tap(
                        custom = InteractionConfig.Tap.CustomInteraction(true) { size, position ->
                            // Detect tap somewhere in the center with 64 radius and show popup
                            if ((position - size.center.toOffset()).getDistance() < 64.dp.toPx()) {
                                showPopup = true
                                true
                            } else {
                                false
                            }
                        }
                    )
                )
            )
        }

        PageCurl(
            state = state,
            config = PageCurlConfig(
                interaction = interaction
            )
        ) { index ->
            HowToPage(index, pages[index])
        }

        if (showPopup) {
            SettingsPopup(
                interaction = interaction,
                onConfigChange = {
                    interaction = it
                },
                onDismiss = {
                    showPopup = false
                }
            )
        }
    }
}
