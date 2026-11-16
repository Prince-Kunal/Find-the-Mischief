package com.prince.findthemischief.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.GlowGreen
import com.prince.findthemischief.ui.theme.NearBlack
import com.prince.findthemischief.ui.theme.NotoSansRunic
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * An original ouroboros: a gold serpent coiled into a ring, about to bite its own tail,
 * with a glowing green rune in the middle. Drawn entirely in code (no image files).
 *
 * Pass [contentDescription] = null (the default) when it's purely decorative.
 */
@Composable
fun SerpentEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    rune: String = "ᛚ",
    contentDescription: String? = null,
) {
    val runeSize = with(LocalDensity.current) { (size * 0.36f).toSp() }
    Box(
        modifier = modifier
            .size(size)
            .clearAndSetSemantics { contentDescription?.let { this.contentDescription = it } },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawGlow()
            drawSerpent()
        }
        Text(
            text = rune,
            color = GlowGreen,
            fontSize = runeSize,
            fontFamily = NotoSansRunic,
            style = androidx.compose.ui.text.TextStyle(
                shadow = Shadow(color = GlowGreen, blurRadius = 24f),
            ),
        )
    }
}

private fun DrawScope.drawGlow() {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(GlowGreen.copy(alpha = 0.22f), Color.Transparent),
            center = center,
            radius = size.minDimension / 2,
        ),
    )
}

// Angles are in degrees, measured clockwise from 3 o'clock (screen coordinates).
private const val TAIL_TIP_ANGLE = -72f
private const val BODY_SWEEP = 330f
private const val BODY_SAMPLES = 120

private fun DrawScope.drawSerpent() {
    val dim = size.minDimension
    val ringRadius = dim * 0.36f
    val bodyHalfWidth = dim * 0.05f
    val tailHalfWidth = dim * 0.006f

    // Body: a ring whose thickness tapers from the tail tip up to full width.
    val outer = ArrayList<Offset>(BODY_SAMPLES + 1)
    val inner = ArrayList<Offset>(BODY_SAMPLES + 1)
    for (i in 0..BODY_SAMPLES) {
        val t = i / BODY_SAMPLES.toFloat()
        val taper = smoothStep((t / 0.4f).coerceAtMost(1f))
        val halfWidth = tailHalfWidth + (bodyHalfWidth - tailHalfWidth) * taper
        val angle = toRadians(TAIL_TIP_ANGLE + BODY_SWEEP * t)
        outer += polar(ringRadius + halfWidth, angle)
        inner += polar(ringRadius - halfWidth, angle)
    }
    val body = Path().apply {
        moveTo(outer.first().x, outer.first().y)
        outer.drop(1).forEach { lineTo(it.x, it.y) }
        inner.asReversed().forEach { lineTo(it.x, it.y) }
        close()
    }
    val goldSheen = Brush.sweepGradient(
        listOf(OldGold, OldGoldDim, OldGold, Color(0xFFE6C55A), OldGold),
        center = center,
    )
    drawPath(body, goldSheen)
    drawPath(body, OldGoldDim, style = Stroke(width = 1.dp.toPx()))

    // Scales: a dashed line along the spine.
    drawArc(
        color = NearBlack.copy(alpha = 0.45f),
        startAngle = TAIL_TIP_ANGLE + 70f,
        sweepAngle = BODY_SWEEP - 90f,
        useCenter = false,
        topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
        size = Size(ringRadius * 2, ringRadius * 2),
        style = Stroke(
            width = 1.2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx())),
        ),
    )

    drawHead(ringRadius, bodyHalfWidth, toRadians(TAIL_TIP_ANGLE + BODY_SWEEP))
}

/** A leaf-shaped head at the end of the body, pointing towards the tail. */
private fun DrawScope.drawHead(ringRadius: Float, bodyHalfWidth: Float, angle: Float) {
    val base = polar(ringRadius, angle)
    val radial = Offset(cos(angle), sin(angle))       // points away from the centre
    val forward = Offset(-sin(angle), cos(angle))     // direction the serpent travels
    val length = ringRadius * 0.42f
    val width = bodyHalfWidth * 1.7f

    val backOuter = base - forward * (length * 0.15f) + radial * bodyHalfWidth
    val backInner = base - forward * (length * 0.15f) - radial * bodyHalfWidth
    val tip = base + forward * length
    val head = Path().apply {
        moveTo(backOuter.x, backOuter.y)
        val c1 = base + forward * (length * 0.45f) + radial * (width * 1.3f)
        quadraticTo(c1.x, c1.y, tip.x, tip.y)
        val c2 = base + forward * (length * 0.45f) - radial * (width * 1.3f)
        quadraticTo(c2.x, c2.y, backInner.x, backInner.y)
        close()
    }
    drawPath(head, OldGold)
    drawPath(head, OldGoldDim, style = Stroke(width = 1.dp.toPx()))
    val eye = base + forward * (length * 0.42f) + radial * (width * 0.4f)
    drawCircle(NearBlack, radius = bodyHalfWidth * 0.32f, center = eye)
}

private fun DrawScope.polar(radius: Float, angle: Float) =
    Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))

private fun toRadians(degrees: Float) = (degrees * PI / 180.0).toFloat()

private fun smoothStep(x: Float) = x * x * (3 - 2 * x)

@Preview(backgroundColor = 0xFF0B0F0D, showBackground = true)
@Composable
private fun SerpentEmblemPreview() {
    FindTheMischiefTheme { SerpentEmblem(Modifier.padding(16.dp), size = 200.dp) }
}
