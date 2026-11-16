package com.prince.findthemischief.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.RuneTextStyle

/** The first six Elder Futhark runes ("futhark" itself). */
const val FUTHARK = "ᚠᚢᚦᚨᚱᚲ"

/**
 * A horizontal divider: gold lines that fade out towards the edges, with runes in the middle.
 * Purely decorative, so it is hidden from screen readers.
 */
@Composable
fun RuneDivider(
    modifier: Modifier = Modifier,
    runes: String = FUTHARK,
    color: Color = OldGold,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FadingLine(Modifier.weight(1f), fadeTowardsStart = true, color = color)
        Text(
            text = runes,
            style = RuneTextStyle,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        FadingLine(Modifier.weight(1f), fadeTowardsStart = false, color = color)
    }
}

@Composable
private fun FadingLine(modifier: Modifier, fadeTowardsStart: Boolean, color: Color) {
    val colors = listOf(Color.Transparent, color)
    Box(
        modifier
            .height(1.dp)
            .background(Brush.horizontalGradient(if (fadeTowardsStart) colors else colors.reversed())),
    )
}

@Preview(backgroundColor = 0xFF0B0F0D, showBackground = true)
@Composable
private fun RuneDividerPreview() {
    FindTheMischiefTheme { RuneDivider(Modifier.padding(16.dp)) }
}
