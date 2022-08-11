@file:OptIn(ExperimentalPageCurlApi::class)

package eu.wewox.pagecurl.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.HowToPageData
import eu.wewox.pagecurl.components.HowToPage
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.rememberPageCurlState

@Composable
fun SimplePageCurlScreen() {
    Box(Modifier.fillMaxSize()) {
        val pages = remember { HowToPageData.simpleHowToPages }
        val state = rememberPageCurlState(max = pages.size)

        PageCurl(state = state) { index ->
            HowToPage(index, pages[index])
        }
    }
}
