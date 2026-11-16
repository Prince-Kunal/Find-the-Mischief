package com.prince.findthemischief.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.R
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.MischiefButtonStyle
import com.prince.findthemischief.ui.theme.DeepEmerald
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.Parchment
import com.prince.findthemischief.ui.theme.ParchmentMuted

@Composable
fun AbandonDialog(onAbandon: () -> Unit, onKeepPlaying: () -> Unit) {
    AlertDialog(
        onDismissRequest = onKeepPlaying,
        containerColor = DeepEmerald,
        titleContentColor = OldGold,
        textContentColor = Parchment,
        title = { Text(stringResource(R.string.abandon_title)) },
        text = { Text(stringResource(R.string.abandon_body)) },
        confirmButton = {
            TextButton(onClick = onAbandon) {
                Text(stringResource(R.string.abandon_confirm), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onKeepPlaying) {
                Text(stringResource(R.string.abandon_keep_playing), color = OldGold)
            }
        },
    )
}

/** Stand-in for the Telling, Judgement and results until Phases 6 and 7 build them. */
@Composable
fun NextPhasesScreen(onEndGame: () -> Unit) {
    MischiefBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.next_phases_body),
                style = MaterialTheme.typography.bodyLarge,
                color = ParchmentMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            MischiefButton(
                text = stringResource(R.string.next_phases_end),
                onClick = onEndGame,
                modifier = Modifier.fillMaxWidth(),
                style = MischiefButtonStyle.Secondary,
            )
        }
    }
}
