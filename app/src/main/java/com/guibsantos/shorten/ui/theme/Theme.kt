package com.guibsantos.shorten.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkBgStart = Color(0xFF120C34)
val DarkBgEnd = Color(0xFF241744)

val DarkOrb1 = Color(0xFF6200EA).copy(alpha = 0.5f)
val DarkOrb2 = Color(0xFFC51162).copy(alpha = 0.5f)
val DarkGlassColor = Color(0xFF2D2D2D).copy(alpha = 0.30f)
val DarkBorderColor = Color.White.copy(alpha = 0.2f)
val DarkTextColor = Color(0xFFF5F5F5)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF0F172A),
    surface = Color(0xFF0F172A),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ShortenerAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
