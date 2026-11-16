package com.prince.findthemischief.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.ui.theme.DeepEmerald
import com.prince.findthemischief.ui.theme.DeepEmeraldLight
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim

private val CardShape = RoundedCornerShape(4.dp)
private val InnerInset = 7.dp

/**
 * A deep-emerald card with a gold border, a thin inner frame and filigree corners.
 * Used for the reveal card and any "important" panel.
 */
@Composable
fun GoldFrameCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 28.dp, vertical = 32.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(Brush.verticalGradient(listOf(DeepEmeraldLight, DeepEmerald)), CardShape)
            .border(1.5.dp, OldGold, CardShape)
            .drawWithContent {
                drawContent()
                drawFrame()
            }
            .padding(contentPadding),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

private fun DrawScope.drawFrame() {
    val inset = InnerInset.toPx()
    // Thin inner frame.
    drawRect(
        color = OldGoldDim,
        topLeft = Offset(inset, inset),
        size = Size(size.width - 2 * inset, size.height - 2 * inset),
        style = Stroke(width = 1.dp.toPx()),
    )
    // The same ornament, mirrored into each corner.
    for ((sx, sy) in listOf(1f to 1f, -1f to 1f, 1f to -1f, -1f to -1f)) {
        scale(scaleX = sx, scaleY = sy, pivot = center) { drawCornerOrnament(inset) }
    }
}

/** Draws the top-left filigree: a quarter-circle curl, a diamond and two dots. */
private fun DrawScope.drawCornerOrnament(inset: Float) {
    val radius = 16.dp.toPx()
    val stroke = 1.5.dp.toPx()
    drawArc(
        color = OldGold,
        startAngle = 0f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(inset - radius, inset - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = stroke),
    )
    val d = 3.5.dp.toPx()
    val c = inset + 6.dp.toPx()
    val diamond = Path().apply {
        moveTo(c, c - d)
        lineTo(c + d, c)
        lineTo(c, c + d)
        lineTo(c - d, c)
        close()
    }
    drawPath(diamond, OldGold)
    val dotOffset = radius + 6.dp.toPx()
    val dotRadius = 1.5.dp.toPx()
    drawCircle(OldGold, dotRadius, Offset(inset + dotOffset, inset))
    drawCircle(OldGold, dotRadius, Offset(inset, inset + dotOffset))
}

@Preview(backgroundColor = 0xFF0B0F0D, showBackground = true)
@Composable
private fun GoldFrameCardPreview() {
    FindTheMischiefTheme {
        GoldFrameCard(Modifier.padding(24.dp)) {
            Text("The Truth is:", style = MaterialTheme.typography.titleMedium)
            Text("Pizza", style = MaterialTheme.typography.displayMedium, color = OldGold)
        }
    }
}
