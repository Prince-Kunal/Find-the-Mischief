package com.prince.findthemischief.game.domain

// Pure Kotlin: no Android imports. Players are referred to by their seat index (0-based),
// which is also the order the phone is passed in.

enum class Team { Honest, Tricksters }

enum class ResultReason {
    /** Every Trickster was banished (and no last lie was allowed). */
    AllTrickstersBanished,

    /** One-Trickster game: the group banished one of the Honest. */
    HonestBanished,

    /** Several Tricksters: they now equal (or outnumber) the Honest still in the game. */
    TrickstersEqualHonest,

    /** The final Trickster was banished but guessed the Truth. */
    LastLieGuessed,

    /** The final Trickster was banished and guessed wrong. */
    LastLieMissed,
}

/** Which phase the game is in. The engine moves between these; screens just draw them. */
sealed interface GameState {
    /**
     * Passing the phone. [showingCard] false = this player's Pass screen (nothing secret);
     * true = their card screen. There is no way from one player's card to the next player's card
     * without passing through the next player's Pass screen.
     */
    data class Reveal(val playerIndex: Int, val showingCard: Boolean) : GameState

    /** "All fates are sealed." Everyone has seen their card. */
    data object AllSealed : GameState

    /** A round of one-word clues. [order] lists active players, starting with an Honest player. */
    data class Telling(val round: Int, val order: List<Int>) : GameState

    /** The group chooses someone to banish. */
    data class Judgement(val round: Int) : GameState

    /** The final Trickster was banished and may guess the Truth ("The Trickster's last lie"). */
    data class LastLie(val tricksterIndex: Int) : GameState

    data class Result(
        val winner: Team,
        val reason: ResultReason,
        val lastLieGuess: String? = null,
    ) : GameState
}

/** What one player sees on their card. */
sealed interface RoleCard {
    data class Honest(val truth: String) : RoleCard

    /** [whisper] is null when Whispers are off. */
    data class Trickster(val whisper: String?) : RoleCard
}

/** One dealt game: who the Tricksters are, the Truth, and where play has got to. */
data class Game(
    val setup: GameSetup,
    val categoryId: String,
    val categoryName: String,
    val truth: String,
    /** The Whisper the Tricksters see, or null when Whispers are off. */
    val whisper: String?,
    val tricksters: Set<Int>,
    /** Banished players, in the order they were banished. */
    val banished: List<Int> = emptyList(),
    val state: GameState,
) {
    val players: List<String> get() = setup.playerNames
    val activePlayers: List<Int> get() = players.indices.filterNot { it in banished }
    val remainingTricksters: Int get() = activePlayers.count { it in tricksters }
    val remainingHonest: Int get() = activePlayers.size - remainingTricksters

    fun isTrickster(player: Int): Boolean = player in tricksters

    fun cardFor(player: Int): RoleCard =
        if (isTrickster(player)) RoleCard.Trickster(whisper) else RoleCard.Honest(truth)
}
