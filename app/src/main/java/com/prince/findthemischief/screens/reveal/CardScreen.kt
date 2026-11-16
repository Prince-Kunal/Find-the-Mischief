package com.prince.findthemischief.screens.reveal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.prince.findthemischief.R
import com.prince.findthemischief.core.data.RevealMode
import com.prince.findthemischief.game.GameUiState
import com.prince.findthemischief.ui.components.GoldFrameCard
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.theme.GlowGreen
import com.prince.findthemischief.ui.theme.ParchmentMuted

/**
 * One player's card. The secret shows only while the stone is held (or, in tap mode, after a
 * tap) and disappears instantly, with no fade, the moment it should be hidden.
 *
 * @param forceHidden hide the secret no matter what, e.g. while the "Abandon?" dialog is open.
 */
@Composable
fun CardScreen(
    state: GameUiState.Card,
    revealMode: RevealMode,
    forceHidden: Boolean,
    onFirstReveal: () -> Unit,
    onNext: () -> Unit,
) {
    // key(): a different player always starts with fresh, hidden state.
    key(state.player.index) {
        var revealed by remember { mutableStateOf(false) }
        var seenOnce by remember { mutableStateOf(false) }
        val showing = revealed && !forceHidden

        LaunchedEffect(showing) {
            if (showing && !seenOnce) {
                seenOnce = true
                onFirstReveal()
            }
        }
        // If the app goes to the background, flip the card face down.
        LifecycleEventEffect(Lifecycle.Event.ON_STOP) { revealed = false }

        MischiefBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.card_only_you, state.player.name),
                    style = MaterialTheme.typography.titleMedium,
                    color = ParchmentMuted,
                    textAlign = TextAlign.Center,
                )
                RuneStone(
                    showing = showing,
                    revealMode = revealMode,
                    onRevealedChange = { revealed = it },
                ) {
                    CardFace(state.card)
                }
                MischiefButton(
                    text = stringResource(if (state.isLastPlayer) R.string.card_seal else R.string.card_next_player),
                    onClick = onNext,
                    enabled = seenOnce,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** The stone the card is carved on. Glows open while showing; goes dark instantly when hidden. */
@Composable
private fun RuneStone(
    showing: Boolean,
    revealMode: RevealMode,
    onRevealedChange: (Boolean) -> Unit,
    face: @Composable () -> Unit,
) {
    val glow = remember { Animatable(0f) }
    LaunchedEffect(showing) {
        if (showing) glow.animateTo(1f, tween(durationMillis = 250)) else glow.snapTo(0f)
    }

    val revealLabel = stringResource(R.string.card_a11y_reveal)
    val hiddenState = stringResource(R.string.card_a11y_hidden)
    val shownState = stringResource(R.string.card_a11y_shown)
    val gesture = when (revealMode) {
        RevealMode.Hold -> Modifier
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    onRevealedChange(true)
                    tryAwaitRelease()
                    onRevealedChange(false)
                })
            }
            // Screen readers can't "hold", so let a double-tap toggle the card instead.
            .semantics { onClick(revealLabel) { onRevealedChange(!showing); true } }
        RevealMode.Tap -> Modifier.clickable(
            interactionSource = null,
            indication = null,
            onClickLabel = revealLabel,
        ) { onRevealedChange(!showing) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .drawBehind {
                if (glow.value > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(GlowGreen.copy(alpha = 0.55f * glow.value), Color.Transparent),
                            center = center,
                            radius = size.maxDimension * 0.65f,
                        ),
                        radius = size.maxDimension * 0.65f,
                    )
                }
            }
            .then(gesture)
            .semantics { stateDescription = if (showing) shownState else hiddenState },
        contentAlignment = Alignment.Center,
    ) {
        GoldFrameCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            if (showing) face() else HiddenFace(revealMode)
        }
    }
}
