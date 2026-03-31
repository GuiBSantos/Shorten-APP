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
import com.guibsantos.shorten.ui.theme.DarkBgEnd
import com.guibsantos.shorten.ui.theme.DarkBgStart
import com.guibsantos.shorten.ui.theme.DarkOrb1
import com.guibsantos.shorten.ui.theme.DarkOrb2

@Composable
fun OrbitalBackground(
    content: @Composable () -> Unit
) {
    val bgBrush = Brush.verticalGradient(listOf(DarkBgStart, DarkBgEnd))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DarkOrb1, Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DarkOrb2, Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width, size.height),
                radius = size.width * 0.7f
            )
        }
        content()
    }
}
