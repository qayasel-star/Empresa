package com.stockpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StockProColors = darkColorScheme(
    primary = Color(0xFF63FF91), onPrimary = Color(0xFF031109),
    secondary = Color(0xFF38D878), onSecondary = Color(0xFF031109),
    background = Color(0xFF030907), onBackground = Color(0xFFF0F4EF),
    surface = Color(0xFF0A1711), onSurface = Color(0xFFF0F4EF),
    surfaceVariant = Color(0xFF163524), onSurfaceVariant = Color(0xFF91A99A),
    error = Color(0xFFFF8A80)
)

@Composable fun StockProTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = StockProColors, content = content)
}
