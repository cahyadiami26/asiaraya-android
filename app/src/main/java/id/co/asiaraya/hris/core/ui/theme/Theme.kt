package id.co.asiaraya.hris.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = CardBg,
    primaryContainer = Navy900,
    onPrimaryContainer = CardBg,
    secondary = Teal600,
    onSecondary = CardBg,
    secondaryContainer = Teal50,
    onSecondaryContainer = Teal700,
    tertiary = Indigo600,
    onTertiary = CardBg,
    background = Bg,
    onBackground = Ink,
    surface = CardBg,
    onSurface = Ink,
    surfaceVariant = Bg2,
    onSurfaceVariant = Ink2,
    outline = Line,
    outlineVariant = LineStrong,
    error = Rose600,
    onError = CardBg,
    errorContainer = Rose50,
    onErrorContainer = Rose600
)

@Composable
fun HrisTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
