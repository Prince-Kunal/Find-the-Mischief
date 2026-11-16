package com.prince.findthemischief.game.domain

import kotlinx.serialization.Serializable
import kotlin.math.max

// Pure Kotlin: no Android imports. Shared by the Setup screen and (Phase 4) the engine.

/** How hard the Whisper makes things for the Trickster (see plan §5.1). */
enum class WhisperLevel { Easy, Medium, Hard }

/** Optional countdown for the Telling. */
enum class TellingTimer(val seconds: Int) { Off(0), Seconds30(30), Seconds60(60), Seconds90(90) }

/** A validated, ready-to-play setup. Build one with [SetupRules.validate]. */
@Serializable
data class GameSetup(
    val playerNames: List<String>,
    val tricksterCount: Int,
    val categoryIds: Set<String>,
    /** Null when Whispers are turned off. */
    val whisperLevel: WhisperLevel?,
    val tellingTimer: TellingTimer,
    val lastLieEnabled: Boolean,
)

sealed interface SetupProblem {
    data object TooFewPlayers : SetupProblem
    data object TooManyPlayers : SetupProblem
    /** [indices] are the positions (0-based) of players with no name. */
    data class BlankNames(val indices: List<Int>) : SetupProblem
    /** Two or more players share [name] (compared ignoring case and surrounding spaces). */
    data class DuplicateName(val name: String, val indices: List<Int>) : SetupProblem
    data object NoCategories : SetupProblem
}

object SetupRules {
    const val MIN_PLAYERS = 3
    const val MAX_PLAYERS = 20
    const val MAX_NAME_LENGTH = 20

    /** One Trickster per three players, rounded down, never fewer than one (plan §5.5). */
    fun maxTricksters(playerCount: Int): Int = max(1, (playerCount - 1) / 3)

    fun clampTricksters(count: Int, playerCount: Int): Int = count.coerceIn(1, maxTricksters(playerCount))

    fun problems(playerNames: List<String>, categoryIds: Set<String>): List<SetupProblem> = buildList {
        if (playerNames.size < MIN_PLAYERS) add(SetupProblem.TooFewPlayers)
        if (playerNames.size > MAX_PLAYERS) add(SetupProblem.TooManyPlayers)

        val trimmed = playerNames.map { it.trim() }
        val blanks = trimmed.indices.filter { trimmed[it].isEmpty() }
        if (blanks.isNotEmpty()) add(SetupProblem.BlankNames(blanks))

        trimmed.indices
            .filter { trimmed[it].isNotEmpty() }
            .groupBy { trimmed[it].lowercase() }
            .values
            .filter { it.size > 1 }
            .forEach { indices -> add(SetupProblem.DuplicateName(trimmed[indices.first()], indices)) }

        if (categoryIds.isEmpty()) add(SetupProblem.NoCategories)
    }

    /** Returns a clean [GameSetup] (trimmed names, legal Trickster count), or null if there are [problems]. */
    fun validate(
        playerNames: List<String>,
        tricksterCount: Int,
        categoryIds: Set<String>,
        whisperLevel: WhisperLevel?,
        tellingTimer: TellingTimer,
        lastLieEnabled: Boolean,
    ): GameSetup? {
        if (problems(playerNames, categoryIds).isNotEmpty()) return null
        return GameSetup(
            playerNames = playerNames.map { it.trim() },
            tricksterCount = clampTricksters(tricksterCount, playerNames.size),
            categoryIds = categoryIds,
            whisperLevel = whisperLevel,
            tellingTimer = tellingTimer,
            lastLieEnabled = lastLieEnabled,
        )
    }
}
