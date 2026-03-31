package com.guibsantos.shorten.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ModernBackground(
    content: @Composable () -> Unit
) {
    val bgColor = Color(0xFF050511)
    val orb1Color = Color(0xFFFF0080)
    val orb2Color = Color(0xFF7928CA)
    val orb3Color = Color(0xFF00C6FF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orb1Color.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = 1000f
                ),
                center = Offset(0f, 0f),
                radius = 1000f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orb2Color.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width, size.height * 0.5f),
                    radius = 800f
                ),
                center = Offset(size.width, size.height * 0.5f),
                radius = 800f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orb3Color.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height),
                    radius = 900f
                ),
                center = Offset(size.width * 0.2f, size.height),
                radius = 900f
            )
        }
        content()
    }
}
