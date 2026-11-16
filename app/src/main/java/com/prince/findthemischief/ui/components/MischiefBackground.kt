package com.prince.findthemischief.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.prince.findthemischief.ui.theme.DeepEmerald
import com.prince.findthemischief.ui.theme.GlowGreen
import com.prince.findthemischief.ui.theme.NearBlack

/** Full-screen near-black backdrop with a faint green "smoke" glow. Put every screen inside one. */
@Composable
fun MischiefBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(NearBlack)
                // Upper glow, roughly behind the emblem / title.
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(GlowGreen.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.28f),
                        radius = size.maxDimension * 0.55f,
                    ),
                )
                // Low emerald haze rising from the bottom edge.
                drawRect(
                    Brush.verticalGradient(
                        0.6f to Color.Transparent,
                        1f to DeepEmerald.copy(alpha = 0.55f),
                    ),
                )
            },
        content = content,
    )
}
