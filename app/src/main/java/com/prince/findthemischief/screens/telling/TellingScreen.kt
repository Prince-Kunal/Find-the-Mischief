package com.prince.findthemischief.screens.telling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prince.findthemischief.R
import com.prince.findthemischief.game.GameUiState
import com.prince.findthemischief.game.PlayerLabel
import com.prince.findthemischief.game.TimerUiState
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.MischiefButtonStyle
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.theme.BloodRedLight
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.GlowGreen
import com.prince.findthemischief.ui.theme.NotoSansRunic
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.Parchment
import com.prince.findthemischief.ui.theme.ParchmentMuted

/** The clue round: who starts, the order around the ring, the timer, and what to do next. */
@Composable
fun TellingScreen(
    state: GameUiState.Telling,
    timer: TimerUiState,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAnotherRound: () -> Unit,
    onBeginJudgement: () -> Unit,
) {
    val starter = state.order.first().name
    val orderDescription = stringResource(
        R.string.telling_a11y_order,
        state.order.mapIndexed { i, p -> "${i + 1} ${p.name}" }.joinToString(", "),
    )
    MischiefBackground {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.telling_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = OldGold,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.telling_round, state.round),
                    style = MaterialTheme.typography.labelLarge,
                    color = ParchmentMuted,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.telling_begins, starter),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Parchment,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.telling_instructions),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentMuted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                RuneRing(
                    order = state.order,
                    timer = timer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearAndSetSemantics { contentDescription = orderDescription },
                ) { RingCenter(timer) }
                if (timer.enabled) TimerControls(timer, onToggleTimer, onResetTimer)
                if (state.order.size > NAMES_ON_RING_MAX) SpeakingOrderList(state.order)
            }
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                RuneDivider()
                Spacer(Modifier.height(12.dp))
                MischiefButton(
                    text = stringResource(R.string.telling_begin_judgement),
                    onClick = onBeginJudgement,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                MischiefButton(
                    text = stringResource(R.string.telling_another_round),
                    onClick = onAnotherRound,
                    modifier = Modifier.fillMaxWidth(),
                    style = MischiefButtonStyle.Secondary,
                )
            }
        }
    }
}

/** The middle of the ring: the countdown, or the rune of speech (Ansuz) when there's no timer. */
@Composable
private fun RingCenter(timer: TimerUiState) {
    when {
        !timer.enabled -> Text("ᚨ", fontFamily = NotoSansRunic, fontSize = 72.sp, color = OldGoldDim)
        timer.finished -> Text(
            text = stringResource(R.string.telling_time_up),
            style = MaterialTheme.typography.displaySmall,
            color = BloodRedLight,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        else -> Text(
            text = formatSeconds(timer.remainingSeconds),
            style = MaterialTheme.typography.displayMedium,
            color = if (timer.running) GlowGreen else Parchment,
        )
    }
}

@Composable
private fun TimerControls(timer: TimerUiState, onToggle: () -> Unit, onReset: () -> Unit) {
    val label = when {
        timer.running -> R.string.telling_timer_pause
        timer.finished -> R.string.telling_timer_again
        timer.remainingSeconds < timer.totalSeconds -> R.string.telling_timer_resume
        else -> R.string.telling_timer_start
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        MischiefButton(
            text = stringResource(label),
            onClick = onToggle,
            style = MischiefButtonStyle.Secondary,
            modifier = Modifier.width(200.dp),
        )
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = onReset, enabled = timer.remainingSeconds < timer.totalSeconds) {
            Text(
                text = stringResource(R.string.telling_timer_reset),
                style = MaterialTheme.typography.labelLarge,
                color = OldGold,
            )
        }
    }
}

/** With many players the ring shows numbers, so the names are listed here. */
@Composable
private fun SpeakingOrderList(order: List<PlayerLabel>) {
    Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(
            text = stringResource(R.string.telling_order_title),
            style = MaterialTheme.typography.titleMedium,
            color = OldGold,
        )
        Spacer(Modifier.height(8.dp))
        order.forEachIndexed { i, player ->
            Row(Modifier.padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${i + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (i == 0) OldGold else ParchmentMuted,
                    modifier = Modifier.width(24.dp),
                )
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (i == 0) OldGold else Parchment,
                )
            }
        }
    }
}

private fun formatSeconds(seconds: Int) = "%d:%02d".format(seconds / 60, seconds % 60)

@Preview(showSystemUi = true)
@Composable
private fun TellingPreview() {
    FindTheMischiefTheme {
        TellingScreen(
            state = GameUiState.Telling(1, listOf("Asha", "Bilal", "Chen", "Dev", "Esha").mapIndexed(::PlayerLabel), 60),
            timer = TimerUiState(60, 42, running = true),
            onToggleTimer = {}, onResetTimer = {}, onAnotherRound = {}, onBeginJudgement = {},
        )
    }
}
