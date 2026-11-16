package com.prince.findthemischief.screens.reveal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.R
import com.prince.findthemischief.game.GameUiState
import com.prince.findthemischief.game.PlayerLabel
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.components.SerpentEmblem
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.ParchmentMuted

/** "Pass the phone to {name}". Shows nothing secret. */
@Composable
fun PassScreen(state: GameUiState.Pass, onReveal: () -> Unit) {
    MischiefBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.pass_position, state.position, state.total),
                style = MaterialTheme.typography.labelLarge,
                color = ParchmentMuted,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SerpentEmblem(size = 160.dp)
                Spacer(Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.pass_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = ParchmentMuted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.player.name,
                    style = MaterialTheme.typography.displayMedium,
                    color = OldGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(16.dp))
                RuneDivider()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pass_look_away),
                    style = MaterialTheme.typography.bodyLarge,
                    color = ParchmentMuted,
                    textAlign = TextAlign.Center,
                )
            }
            MischiefButton(
                text = stringResource(R.string.pass_reveal_button, state.player.name),
                onClick = onReveal,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PassScreenPreview() {
    FindTheMischiefTheme { PassScreen(GameUiState.Pass(PlayerLabel(1, "Asha"), 2, 5), onReveal = {}) }
}
