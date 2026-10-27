package com.stockpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StockProColors = darkColorScheme(
    primary = Color(0xFFB6FF68),
    onPrimary = Color(0xFF07190F),
    primaryContainer = Color(0xFF174C31),
    onPrimaryContainer = Color(0xFFD9FFE4),
    secondary = Color(0xFF39E989),
    onSecondary = Color(0xFF052016),
    secondaryContainer = Color(0xFF103F2D),
    onSecondaryContainer = Color(0xFFC5F8D8),
    background = Color(0xFF030E0C),
    onBackground = Color(0xFFF3F8F4),
    surface = Color(0xFF081C17),
    onSurface = Color(0xFFF3F8F4),
    surfaceVariant = Color(0xFF12352A),
    onSurfaceVariant = Color(0xFFB6C9BC),
    outline = Color(0xFF315A48),
    error = Color(0xFFFF7777),
    onError = Color(0xFF2B0808)
)

@Composable
fun StockProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StockProColors,
        content = content
    )
}
