package eu.wewox.pagecurl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    secondary = LightBlue,
    tertiary = LightYellow,

    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    surface = Color.Black,
    background = Color.Black,

    onSurface = Color.White,
    onBackground = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    secondary = LightBlue,
    tertiary = LightYellow,

    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    surface = Color.White,
    background = Color.White,

    onSurface = Color.Black,
    onBackground = Color.Black,
)

/**
 * The theme to use for demo application.
 */
@Composable
fun PageCurlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
