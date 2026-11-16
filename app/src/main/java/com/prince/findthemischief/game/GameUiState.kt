package com.prince.findthemischief.game

import com.prince.findthemischief.game.domain.Game
import com.prince.findthemischief.game.domain.GameState
import com.prince.findthemischief.game.domain.ResultReason
import com.prince.findthemischief.game.domain.RoleCard
import com.prince.findthemischief.game.domain.Team

/** A player as the screens show them: seat index plus name. */
data class PlayerLabel(val index: Int, val name: String)

/**
 * Exactly what the current game screen needs to draw, with names already resolved.
 * Secret information appears only in [Card] (one player's own card) and [Result] (the end).
 */
sealed interface GameUiState {
    data object Loading : GameUiState

    /** Hand the phone to [player]. Nothing secret here. */
    data class Pass(val player: PlayerLabel, val position: Int, val total: Int) : GameUiState

    data class Card(val player: PlayerLabel, val card: RoleCard, val isLastPlayer: Boolean) : GameUiState

    data object AllSealed : GameUiState

    data class Telling(
        val round: Int,
        /** Speaking order; the first is the starting player. */
        val order: List<PlayerLabel>,
        /** 0 = no timer. */
        val timerSeconds: Int,
    ) : GameUiState

    data class Judgement(val round: Int, val candidates: List<PlayerLabel>) : GameUiState

    data class LastLie(val trickster: PlayerLabel) : GameUiState

    data class Result(
        val winner: Team,
        val reason: ResultReason,
        val truth: String,
        val categoryName: String,
        val whisper: String?,
        val tricksters: List<PlayerLabel>,
        val banished: List<PlayerLabel>,
        val lastLieGuess: String?,
    ) : GameUiState
}

internal fun Game.toUiState(): GameUiState {
    fun label(index: Int) = PlayerLabel(index, players[index])
    return when (val s = state) {
        is GameState.Reveal ->
            if (s.showingCard) {
                GameUiState.Card(label(s.playerIndex), cardFor(s.playerIndex), s.playerIndex == players.lastIndex)
            } else {
                GameUiState.Pass(label(s.playerIndex), position = s.playerIndex + 1, total = players.size)
            }
        GameState.AllSealed -> GameUiState.AllSealed
        is GameState.Telling -> GameUiState.Telling(s.round, s.order.map(::label), setup.tellingTimer.seconds)
        is GameState.Judgement -> GameUiState.Judgement(s.round, activePlayers.map(::label))
        is GameState.LastLie -> GameUiState.LastLie(label(s.tricksterIndex))
        is GameState.Result -> GameUiState.Result(
            winner = s.winner,
            reason = s.reason,
            truth = truth,
            categoryName = categoryName,
            whisper = whisper,
            tricksters = tricksters.sorted().map(::label),
            banished = banished.map(::label),
            lastLieGuess = s.lastLieGuess,
        )
    }
}
