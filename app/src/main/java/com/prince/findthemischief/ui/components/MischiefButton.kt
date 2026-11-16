package com.prince.findthemischief.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.ui.theme.DeepEmerald
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.MischiefGreen
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.Parchment
import com.prince.findthemischief.ui.theme.ParchmentMuted

enum class MischiefButtonStyle {
    /** Filled mischief-green with a gold edge: the main action on a screen. */
    Primary,

    /** Transparent with a gold edge and gold text: secondary actions. */
    Secondary,
}

/** The app's standard button. At least 56dp tall so it is easy to hit while passing the phone. */
@Composable
fun MischiefButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MischiefButtonStyle = MischiefButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val colors = when (style) {
        MischiefButtonStyle.Primary -> ButtonDefaults.buttonColors(
            containerColor = MischiefGreen,
            contentColor = Parchment,
            disabledContainerColor = DeepEmerald,
            disabledContentColor = ParchmentMuted,
        )
        MischiefButtonStyle.Secondary -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = OldGold,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = ParchmentMuted,
        )
    }
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        colors = colors,
        border = BorderStroke(1.5.dp, if (enabled) OldGold else OldGoldDim),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(backgroundColor = 0xFF0B0F0D, showBackground = true)
@Composable
private fun MischiefButtonPreview() {
    FindTheMischiefTheme {
        Column(Modifier.padding(16.dp)) {
            MischiefButton("Play", onClick = {}, modifier = Modifier.fillMaxWidth())
            MischiefButton(
                "How to play",
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                style = MischiefButtonStyle.Secondary,
            )
        }
    }
}
