package eu.wewox.pagecurl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = LightBlue,
    primaryVariant = LightBlue,
    secondary = LightYellow,
    secondaryVariant = LightYellow,

    onPrimary = Color.Black,
    onSecondary = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = LightBlue,
    primaryVariant = LightBlue,
    secondary = LightYellow,
    secondaryVariant = LightYellow,

    onPrimary = Color.Black,
    onSecondary = Color.Black,
)

/**
 * The theme to use for demo application.
 */
@Composable
fun PageCurlTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val sysUiController = rememberSystemUiController()
    SideEffect {
        sysUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme,
            isNavigationBarContrastEnforced = false
        )
    }

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography(),
        shapes = Shapes,
        content = content
    )
}
