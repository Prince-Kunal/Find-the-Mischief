package com.prince.findthemischief.game.domain

import com.prince.findthemischief.game.data.Hints
import com.prince.findthemischief.game.data.WordCategory
import com.prince.findthemischief.game.data.WordEntry
import kotlin.random.Random

/**
 * The rules of Find the Mischief (plan §5.6 and Phase 7). Pure Kotlin, no Android.
 *
 * Every move takes a [Game] and returns the next [Game]. A move that doesn't fit the current
 * phase (for example a double tap) returns the game unchanged instead of crashing.
 *
 * One engine lives for the whole app session so it can remember which words were already used.
 * Pass a seeded [random] in tests to make every game predictable.
 */
class MischiefEngine(private val random: Random = Random.Default) {

    private val usedWords = mutableSetOf<String>()
    private var lastWordKey: String? = null

    /** Deals a new game: picks the Truth and the Tricksters, then starts with player 1's Pass screen. */
    fun newGame(setup: GameSetup, categories: List<WordCategory>): Game {
        val pool = categories
            .filter { it.id in setup.categoryIds }
            .flatMap { category -> category.words.map { PoolWord(category, it) } }
        require(pool.isNotEmpty()) { "The selected categories have no words" }
        require(setup.playerNames.size >= SetupRules.MIN_PLAYERS) { "Not enough players" }

        val pick = pickWord(pool)
        val tricksterCount = SetupRules.clampTricksters(setup.tricksterCount, setup.playerNames.size)
        val tricksters = setup.playerNames.indices.shuffled(random).take(tricksterCount).toSet()

        return Game(
            setup = setup,
            categoryId = pick.category.id,
            categoryName = pick.category.name,
            truth = pick.entry.word,
            whisper = setup.whisperLevel?.let { pick.entry.hints.forLevel(it) },
            tricksters = tricksters,
            state = GameState.Reveal(playerIndex = 0, showingCard = false),
        )
    }

    /** Pass screen → that same player's card. */
    fun revealCard(game: Game): Game {
        val state = game.state as? GameState.Reveal ?: return game
        if (state.showingCard) return game
        return game.copy(state = state.copy(showingCard = true))
    }

    /** Card → the *next* player's Pass screen (never their card), or "All fates are sealed." after the last. */
    fun nextPlayer(game: Game): Game {
        val state = game.state as? GameState.Reveal ?: return game
        if (!state.showingCard) return game
        val next = state.playerIndex + 1
        val newState = if (next < game.players.size) GameState.Reveal(next, showingCard = false) else GameState.AllSealed
        return game.copy(state = newState)
    }

    /** "All fates are sealed." → the first Telling. */
    fun beginTelling(game: Game): Game {
        if (game.state != GameState.AllSealed) return game
        return game.copy(state = GameState.Telling(round = 1, order = tellingOrder(game)))
    }

    /** "Another round": more clues before judging, with a new (Honest) starting player. */
    fun anotherTellingRound(game: Game): Game {
        val state = game.state as? GameState.Telling ?: return game
        return game.copy(state = GameState.Telling(round = state.round + 1, order = tellingOrder(game)))
    }

    fun beginJudgement(game: Game): Game {
        val state = game.state as? GameState.Telling ?: return game
        return game.copy(state = GameState.Judgement(round = state.round))
    }

    /** Banishes [player] and decides whether the game is over, goes to the last lie, or carries on. */
    fun banish(game: Game, player: Int): Game {
        val state = game.state as? GameState.Judgement ?: return game
        if (player !in game.activePlayers) return game
        val after = game.copy(banished = game.banished + player)

        val newState = when {
            after.isTrickster(player) && after.remainingTricksters == 0 ->
                if (game.setup.lastLieEnabled) {
                    GameState.LastLie(player)
                } else {
                    GameState.Result(Team.Honest, ResultReason.AllTrickstersBanished)
                }
            !after.isTrickster(player) && game.tricksters.size == 1 ->
                GameState.Result(Team.Tricksters, ResultReason.HonestBanished)
            after.remainingTricksters >= after.remainingHonest ->
                GameState.Result(Team.Tricksters, ResultReason.TrickstersEqualHonest)
            // Several Tricksters and the game goes on: another Telling (same Truth), then judge again.
            else -> GameState.Telling(round = state.round + 1, order = tellingOrder(after))
        }
        return after.copy(state = newState)
    }

    /** The final Trickster's guess. Ignores capital letters and spaces around the guess. */
    fun guessTruth(game: Game, guess: String): Game {
        if (game.state !is GameState.LastLie) return game
        val correct = guess.trim().equals(game.truth.trim(), ignoreCase = true)
        val result = if (correct) {
            GameState.Result(Team.Tricksters, ResultReason.LastLieGuessed, guess.trim())
        } else {
            GameState.Result(Team.Honest, ResultReason.LastLieMissed, guess.trim())
        }
        return game.copy(state = result)
    }

    /** Active players in seat order, rotated so a random Honest player goes first. */
    private fun tellingOrder(game: Game): List<Int> {
        val active = game.activePlayers
        val honest = active.filterNot { game.isTrickster(it) }
        check(honest.isNotEmpty()) { "No Honest players left to start the Telling" }
        val starter = honest.random(random)
        val startAt = active.indexOf(starter)
        return active.drop(startAt) + active.take(startAt)
    }

    /**
     * Picks a word not yet used this session. When the pool runs out, the used list for that
     * pool is cleared, but the word from the previous game is still avoided so it never repeats
     * back to back (unless it's the only word there is).
     */
    private fun pickWord(pool: List<PoolWord>): PoolWord {
        var candidates = pool.filter { it.key !in usedWords }
        if (candidates.isEmpty()) {
            usedWords.removeAll(pool.map { it.key }.toSet())
            candidates = pool.filter { it.key != lastWordKey }.ifEmpty { pool }
        }
        val pick = candidates.random(random)
        usedWords += pick.key
        lastWordKey = pick.key
        return pick
    }

    private data class PoolWord(val category: WordCategory, val entry: WordEntry) {
        val key: String get() = "${category.id}/${entry.word}"
    }
}

fun Hints.forLevel(level: WhisperLevel): String = when (level) {
    WhisperLevel.Easy -> easy
    WhisperLevel.Medium -> medium
    WhisperLevel.Hard -> hard
}
