package eu.wewox.pagecurl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import eu.wewox.pagecurl.components.TopBar
import eu.wewox.pagecurl.screens.BackPagePageCurlScreen
import eu.wewox.pagecurl.screens.InteractionConfigInPageCurlScreen
import eu.wewox.pagecurl.screens.PagingPageCurlScreen
import eu.wewox.pagecurl.screens.SettingsPageCurlScreen
import eu.wewox.pagecurl.screens.ShadowInPageCurlScreen
import eu.wewox.pagecurl.screens.SimplePageCurlScreen
import eu.wewox.pagecurl.screens.StateInPageCurlScreen
import eu.wewox.pagecurl.ui.SpacingMedium
import eu.wewox.pagecurl.ui.theme.PageCurlTheme

/**
 * Main activity for demo application.
 * Contains simple "Crossfade" based navigation to various examples.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PageCurlTheme {
                var example by rememberSaveable { mutableStateOf<Example?>(null) }

                BackHandler(enabled = example != null) {
                    example = null
                }

                Surface(color = MaterialTheme.colors.background) {
                    Crossfade(targetState = example, Modifier.safeDrawingPadding()) { selected ->
                        when (selected) {
                            null -> RootScreen(onExampleClick = { example = it })
                            Example.SimplePageCurl -> SimplePageCurlScreen()
                            Example.PagingPageCurl -> PagingPageCurlScreen()
                            Example.SettingsPageCurl -> SettingsPageCurlScreen()
                            Example.StateInPageCurl -> StateInPageCurlScreen()
                            Example.InteractionConfigInPageCurl -> InteractionConfigInPageCurlScreen()
                            Example.ShadowPageCurl -> ShadowInPageCurlScreen()
                            Example.BackPagePageCurl -> BackPagePageCurlScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RootScreen(onExampleClick: (Example) -> Unit) {
    Scaffold(
        topBar = { TopBar("Page Curl Demo") }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(Example.values()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExampleClick(it) }
                        .padding(SpacingMedium)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = it.label,
                            style = MaterialTheme.typography.h6
                        )
                        Text(
                            text = it.description,
                            style = MaterialTheme.typography.body2
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
