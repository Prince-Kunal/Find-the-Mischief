package com.prince.findthemischief.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.RuneTextStyle

/** All 24 runes of the Elder Futhark. */
const val ELDER_FUTHARK = "ᚠᚢᚦᚨᚱᚲᚷᚹᚺᚾᛁᛃᛇᛈᛉᛊᛏᛒᛖᛗᛚᛜᛞᛟ"

private val EdgeInset = 12.dp
private val CornerGap = 18.dp

/** Padding that keeps content clear of the rune border. */
val RuneBorderContentPadding = 32.dp

/**
 * A faint frame of runes running along all four edges, with a small gold diamond in each corner.
 * Drawn on a canvas, so it never takes touches or shows up for screen readers.
 * Content is padded by [RuneBorderContentPadding] so it never overlaps the runes.
 */
@Composable
fun RuneBorder(
    modifier: Modifier = Modifier,
    color: Color = OldGold.copy(alpha = 0.35f),
    content: @Composable BoxScope.() -> Unit,
) {
    val measurer = rememberTextMeasurer()
    Box(
        modifier = modifier.drawWithCache {
            val inset = EdgeInset.toPx()
            val gap = CornerGap.toPx()
            val horizontal = measureRuneRow(measurer, size.width - 2 * (inset + gap), color)
            val vertical = measureRuneRow(measurer, size.height - 2 * (inset + gap), color)
            onDrawBehind {
                val half = horizontal.size.height / 2f
                // Top and bottom rows, centred along the edge.
                val xOffset = (size.width - horizontal.size.width) / 2f
                drawText(horizontal, topLeft = Offset(xOffset, inset - half))
                drawText(horizontal, topLeft = Offset(xOffset, size.height - inset - half))
                // Sides: rotate so the row runs along the edge.
                val yOffset = (size.height - vertical.size.width) / 2f
                translate(left = inset, top = size.height - yOffset) {
                    rotate(-90f, pivot = Offset.Zero) { drawText(vertical, topLeft = Offset(0f, -half)) }
                }
                translate(left = size.width - inset, top = yOffset) {
                    rotate(90f, pivot = Offset.Zero) { drawText(vertical, topLeft = Offset(0f, -half)) }
                }
                drawCornerDiamonds(inset, color)
            }
        },
    ) {
        Box(Modifier.padding(RuneBorderContentPadding), content = content)
    }
}

/** Repeats the futhark until it fills [maxWidth], clipped to whole runes that fit. */
private fun measureRuneRow(measurer: TextMeasurer, maxWidth: Float, color: Color): TextLayoutResult {
    val width = maxWidth.toInt().coerceAtLeast(0)
    return measurer.measure(
        text = ELDER_FUTHARK.repeat(6),
        style = RuneTextStyle.copy(color = color, fontSize = 12.sp, letterSpacing = 6.sp),
        overflow = TextOverflow.Clip,
        softWrap = false,
        maxLines = 1,
        constraints = Constraints(maxWidth = width),
    )
}

private fun DrawScope.drawCornerDiamonds(inset: Float, color: Color) {
    val d = 3.dp.toPx()
    for (corner in listOf(
        Offset(inset, inset),
        Offset(size.width - inset, inset),
        Offset(inset, size.height - inset),
        Offset(size.width - inset, size.height - inset),
    )) {
        val diamond = Path().apply {
            moveTo(corner.x, corner.y - d)
            lineTo(corner.x + d, corner.y)
            lineTo(corner.x, corner.y + d)
            lineTo(corner.x - d, corner.y)
            close()
        }
        drawPath(diamond, color.copy(alpha = (color.alpha * 2f).coerceAtMost(1f)))
    }
}
