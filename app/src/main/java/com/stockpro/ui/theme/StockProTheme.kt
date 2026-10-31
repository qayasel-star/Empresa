package com.stockpro.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Design system central de StockPro: oscuro, legible y con acentos esmeralda/cian. */
private val StockProColors = darkColorScheme(
    primary = Color(0xFF35F59B), onPrimary = Color(0xFF04120D),
    primaryContainer = Color(0xFF123E32), onPrimaryContainer = Color(0xFFB8FFDB),
    secondary = Color(0xFF20DCC6), onSecondary = Color(0xFF04120D),
    secondaryContainer = Color(0xFF103A39), onSecondaryContainer = Color(0xFFB4FFF2),
    tertiary = Color(0xFF46BDEB), onTertiary = Color(0xFF04141B),
    background = Color(0xFF030908), onBackground = Color(0xFFF3F7F5),
    surface = Color(0xFF091713), onSurface = Color(0xFFF3F7F5),
    surfaceVariant = Color(0xFF102A25), onSurfaceVariant = Color(0xFFAAC0C0),
    surfaceTint = Color(0xFF35F59B),
    outline = Color(0xFF3B7770), outlineVariant = Color(0xFF23433F),
    error = Color(0xFFFF8A80), onError = Color(0xFF3B0907),
    errorContainer = Color(0xFF5B1B19), onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFE0E8E5), inverseOnSurface = Color(0xFF17201D),
    inversePrimary = Color(0xFF176B49), scrim = Color(0xCC000000)
)

private val StockProTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.1).sp, lineHeight = 58.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.ExtraBold, letterSpacing = (-.8).sp, lineHeight = 48.sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = (-.65).sp, lineHeight = 40.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, letterSpacing = (-.7).sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = (-.45).sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = (-.3).sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = (-.2).sp, lineHeight = 27.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = (-.1).sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = .05.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, letterSpacing = .05.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, letterSpacing = .05.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, letterSpacing = .1.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = .15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = .2.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.SemiBold, letterSpacing = .35.sp, lineHeight = 14.sp)
)

private val StockProShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun StockProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StockProColors,
        typography = StockProTypography,
        shapes = StockProShapes,
        content = content
    )
}
