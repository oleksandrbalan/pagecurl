package eu.wewox.pagecurl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import eu.wewox.pagecurl.screens.BackPagePageCurlScreen
import eu.wewox.pagecurl.screens.InteractionConfigInPageCurlScreen
import eu.wewox.pagecurl.screens.PagingPageCurlScreen
import eu.wewox.pagecurl.screens.SettingsPageCurlScreen
import eu.wewox.pagecurl.screens.ShadowInPageCurlScreen
import eu.wewox.pagecurl.screens.SimplePageCurlScreen
import eu.wewox.pagecurl.screens.StateInPageCurlScreen
import eu.wewox.pagecurl.ui.theme.PageCurlTheme

/**
 * Main activity for demo application.
 * Contains simple "Crossfade" based navigation to various examples.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PageCurlTheme {
                var example by rememberSaveable { mutableStateOf<Example?>(null) }

                BackHandler(enabled = example != null) {
                    example = null
                }

                Surface(color = MaterialTheme.colorScheme.background) {
                    Crossfade(targetState = example, Modifier.safeDrawingPadding(), label = "Crossfade") { selected ->
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
