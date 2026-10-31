package com.stockpro

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Fondo ambiental premium compartido por todos los módulos. */
internal fun Modifier.stockProBackdrop(): Modifier = drawBehind {
    val w = size.width
    val h = size.height.coerceAtLeast(1f)
    drawRect(Brush.verticalGradient(listOf(Color(0xFF020807), Color(0xFF06120F), Color(0xFF03090B)), endY = h))
    val glowRadius = size.maxDimension * .82f
    drawCircle(Brush.radialGradient(listOf(Color(0x5530F5A0), Color(0x1820CDB6), Color.Transparent), Offset(w * .98f, h * .04f), glowRadius), glowRadius, Offset(w * .98f, h * .04f))
    drawCircle(Brush.radialGradient(listOf(Color(0x3022E8A0), Color.Transparent), Offset(w * .02f, h * .82f), glowRadius * .78f), glowRadius * .78f, Offset(w * .02f, h * .82f))
    // Subtle technical grid and fine horizon highlights, kept behind content.
    val grid = Color(0xFF38E7A4).copy(alpha = .035f)
    for (i in 0..12) {
        val x = w * i / 12f
        drawLine(grid, Offset(x, 0f), Offset(x, h), 1f)
    }
    for (i in 1..16) {
        val y = h * i / 16f
        drawLine(grid.copy(alpha = .7f), Offset(0f, y), Offset(w, y), 1f)
    }
    drawLine(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF20E7A2).copy(alpha = .18f), Color.Transparent)), Offset(0f, h * .18f), Offset(w, h * .18f), 1.2f)
}
