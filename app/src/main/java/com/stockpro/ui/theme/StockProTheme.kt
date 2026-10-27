package com.stockpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StockProColors = darkColorScheme(
    primary = Color(0xFF48F2A0), onPrimary = Color(0xFF04120D),
    secondary = Color(0xFF27D9C3), onSecondary = Color(0xFF04120D),
    background = Color(0xFF050A0D), onBackground = Color(0xFFF3F7F5),
    surface = Color(0xFF0B151A), onSurface = Color(0xFFF3F7F5),
    surfaceVariant = Color(0xFF14272B), onSurfaceVariant = Color(0xFF91A7A8),
    error = Color(0xFFFF8A80)
)

private val StockProTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.7).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = (-0.45).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.1).sp),
    bodyLarge = TextStyle(letterSpacing = 0.05.sp),
    bodyMedium = TextStyle(letterSpacing = 0.05.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = 0.15.sp)
)

@Composable fun StockProTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = StockProColors, typography = StockProTypography, content = content)
}
