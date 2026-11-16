package com.prince.findthemischief.screens.reveal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prince.findthemischief.R
import com.prince.findthemischief.core.data.RevealMode
import com.prince.findthemischief.game.domain.RoleCard
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.theme.GlowGreen
import com.prince.findthemischief.ui.theme.NotoSansRunic
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.Parchment
import com.prince.findthemischief.ui.theme.ParchmentMuted

/** The face-down stone: a single rune and how to reveal it. */
@Composable
fun HiddenFace(revealMode: RevealMode) {
    Text(
        text = "ᛟ",
        fontFamily = NotoSansRunic,
        fontSize = 96.sp,
        color = OldGoldDim,
        modifier = Modifier.clearAndSetSemantics { },
    )
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(if (revealMode == RevealMode.Hold) R.string.card_hold_hint else R.string.card_tap_hint),
        style = MaterialTheme.typography.bodyLarge,
        color = ParchmentMuted,
        textAlign = TextAlign.Center,
    )
}

/** The secret side: the Truth for the Honest, the Trickster card (and maybe a Whisper) otherwise. */
@Composable
fun CardFace(card: RoleCard) {
    when (card) {
        is RoleCard.Honest -> {
            Text(
                text = stringResource(R.string.card_truth_label),
                style = MaterialTheme.typography.titleMedium,
                color = ParchmentMuted,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = card.truth,
                style = MaterialTheme.typography.displaySmall,
                color = OldGold,
                textAlign = TextAlign.Center,
            )
        }
        is RoleCard.Trickster -> {
            Text(
                text = stringResource(R.string.card_trickster_you_are),
                style = MaterialTheme.typography.titleMedium,
                color = ParchmentMuted,
            )
            Text(
                text = stringResource(R.string.card_trickster_title),
                style = MaterialTheme.typography.displaySmall,
                color = GlowGreen,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.card_trickster_motto),
                style = MaterialTheme.typography.bodyLarge,
                color = Parchment,
                textAlign = TextAlign.Center,
            )
            if (card.whisper != null) {
                RuneDivider(Modifier.padding(vertical = 16.dp))
                Text(
                    text = stringResource(R.string.card_whisper_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ParchmentMuted,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = card.whisper,
                    style = MaterialTheme.typography.headlineSmall,
                    color = OldGold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
