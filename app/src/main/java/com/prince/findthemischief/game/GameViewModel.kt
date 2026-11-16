package com.prince.findthemischief.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.prince.findthemischief.MischiefApp
import com.prince.findthemischief.core.data.RevealMode
import com.prince.findthemischief.game.data.WordRepository
import com.prince.findthemischief.game.domain.Game
import com.prince.findthemischief.game.domain.GameSetup
import com.prince.findthemischief.game.domain.MischiefEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Runs one game (and its "Play again" rematches) for a fixed [setup].
 * Screens read [uiState] and call the on… functions; all rules live in [MischiefEngine].
 */
class GameViewModel(
    private val setup: GameSetup,
    private val engine: MischiefEngine,
    private val words: WordRepository,
    private val feedback: GameFeedback = GameFeedback.None,
    revealModes: Flow<RevealMode> = flowOf(RevealMode.Hold),
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /** Hold-to-reveal or tap-to-flip, from Settings. */
    val revealMode: StateFlow<RevealMode> =
        revealModes.stateIn(viewModelScope, SharingStarted.Eagerly, RevealMode.Hold)

    private var game: Game? = null

    init {
        deal()
    }

    /** Pass screen: "I am {name}, reveal my fate". */
    fun onRevealCard() {
        move(engine::revealCard)
    }

    /** Card screen: the stone glowed open for the first time for this player. */
    fun onCardRevealed() = feedback.cardRevealed()

    /** Card screen: "Next player". Chimes and buzzes only if the phone really moves on. */
    fun onNextPlayer() {
        if (move(engine::nextPlayer)) feedback.phonePassed()
    }

    fun onBeginTelling() {
        move(engine::beginTelling)
    }

    fun onAnotherTellingRound() {
        move(engine::anotherTellingRound)
    }

    fun onBeginJudgement() {
        move(engine::beginJudgement)
    }

    fun onBanish(playerIndex: Int) {
        move { engine.banish(it, playerIndex) }
    }

    fun onGuessTruth(guess: String) {
        move { engine.guessTruth(it, guess) }
    }

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

    /** Applies an engine move. Returns false if it didn't fit the current phase (nothing changed). */
    private fun move(transition: (Game) -> Game): Boolean {
        val current = game ?: return false
        val next = transition(current)
        if (next == current) return false
        game = next
        _uiState.value = next.toUiState()
        return true
    }

    companion object {
        fun factory(setup: GameSetup): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as MischiefApp).container
                GameViewModel(
                    setup = setup,
                    engine = container.mischiefEngine,
                    words = container.wordRepository,
                    feedback = container.gameFeedback,
                    revealModes = container.settingsRepository.appPreferences.map { it.revealMode },
                )
            }
        }
    }
}
