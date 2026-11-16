package com.prince.findthemischief.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prince.findthemischief.game.GameUiState
import com.prince.findthemischief.game.GameViewModel
import com.prince.findthemischief.game.domain.GameSetup
import com.prince.findthemischief.screens.reveal.CardScreen
import com.prince.findthemischief.screens.reveal.PassScreen
import com.prince.findthemischief.screens.reveal.SealedScreen
import com.prince.findthemischief.screens.telling.TellingScreen
import com.prince.findthemischief.ui.components.KeepScreenOn
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.SecureWindow

/**
 * Hosts a whole game. Picks the screen for the current phase with a plain `when`, so one
 * screen replaces the next instantly: no transition that could show a card in passing.
 */
@Composable
fun GameScreen(setup: GameSetup, onExit: () -> Unit) {
    val viewModel: GameViewModel = viewModel(factory = GameViewModel.factory(setup))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val revealMode by viewModel.revealMode.collectAsStateWithLifecycle()
    val timer by viewModel.timer.collectAsStateWithLifecycle()
    var confirmAbandon by rememberSaveable { mutableStateOf(false) }

    KeepScreenOn()
    SecureWindow(enabled = state.isDuringReveal())
    // Back button and back gesture ask first, so nobody quits by accident mid-game.
    BackHandler(enabled = state !is GameUiState.Result) { confirmAbandon = true }

    when (val s = state) {
        GameUiState.Loading -> MischiefBackground { }
        is GameUiState.Pass -> PassScreen(s, onReveal = viewModel::onRevealCard)
        is GameUiState.Card -> CardScreen(
            state = s,
            revealMode = revealMode,
            forceHidden = confirmAbandon,
            onFirstReveal = viewModel::onCardRevealed,
            onNext = viewModel::onNextPlayer,
        )
        GameUiState.AllSealed -> SealedScreen(onBeginTelling = viewModel::onBeginTelling)
        is GameUiState.Telling -> TellingScreen(
            state = s,
            timer = timer,
            onToggleTimer = viewModel::onToggleTimer,
            onResetTimer = viewModel::onResetTimer,
            onAnotherRound = viewModel::onAnotherTellingRound,
            onBeginJudgement = viewModel::onBeginJudgement,
        )
        // Built in Phase 7.
        is GameUiState.Judgement,
        is GameUiState.LastLie,
        is GameUiState.Result -> NextPhasesScreen(onEndGame = onExit)
    }

    if (confirmAbandon) {
        AbandonDialog(
            onAbandon = {
                confirmAbandon = false
                onExit()
            },
            onKeepPlaying = { confirmAbandon = false },
        )
    }
}

private fun GameUiState.isDuringReveal() = when (this) {
    GameUiState.Loading, is GameUiState.Pass, is GameUiState.Card, GameUiState.AllSealed -> true
    else -> false
}
