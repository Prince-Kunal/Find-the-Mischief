package com.prince.findthemischief.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.prince.findthemischief.MischiefApp
import com.prince.findthemischief.game.data.WordRepository
import com.prince.findthemischief.game.domain.Game
import com.prince.findthemischief.game.domain.GameSetup
import com.prince.findthemischief.game.domain.MischiefEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Runs one game (and its "Play again" rematches) for a fixed [setup].
 * Screens read [uiState] and call the on… functions; all rules live in [MischiefEngine].
 */
class GameViewModel(
    private val setup: GameSetup,
    private val engine: MischiefEngine,
    private val words: WordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var game: Game? = null

    init {
        deal()
    }

    fun onRevealCard() = move(engine::revealCard)
    fun onNextPlayer() = move(engine::nextPlayer)
    fun onBeginTelling() = move(engine::beginTelling)
    fun onAnotherTellingRound() = move(engine::anotherTellingRound)
    fun onBeginJudgement() = move(engine::beginJudgement)
    fun onBanish(playerIndex: Int) = move { engine.banish(it, playerIndex) }
    fun onGuessTruth(guess: String) = move { engine.guessTruth(it, guess) }

    /** "Play again (same players)": a fresh Truth and fresh Tricksters. */
    fun onPlayAgain() = deal()

    private fun deal() {
        _uiState.value = GameUiState.Loading
        viewModelScope.launch {
            val dealt = engine.newGame(setup, words.categories())
            game = dealt
            _uiState.value = dealt.toUiState()
        }
    }

    private fun move(transition: (Game) -> Game) {
        val current = game ?: return
        val next = transition(current)
        if (next == current) return
        game = next
        _uiState.value = next.toUiState()
    }

    companion object {
        fun factory(setup: GameSetup): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as MischiefApp).container
                GameViewModel(setup, container.mischiefEngine, container.wordRepository)
            }
        }
    }
}
