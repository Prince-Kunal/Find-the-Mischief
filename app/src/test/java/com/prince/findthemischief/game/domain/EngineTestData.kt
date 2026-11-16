package com.prince.findthemischief.game.domain

import com.prince.findthemischief.game.data.Hints
import com.prince.findthemischief.game.data.WordCategory
import com.prince.findthemischief.game.data.WordEntry

/** Small, predictable word bank and setups for engine tests. */
object EngineTestData {
    fun category(id: String, size: Int) = WordCategory(
        id = id,
        name = id.uppercase(),
        emoji = "*",
        words = (1..size).map { WordEntry("$id$it", Hints("easy-$id$it", "medium-$id$it", "hard-$id$it")) },
    )

    val categories = listOf(category("a", 5), category("b", 3))

    fun names(count: Int) = (1..count).map { "P$it" }

    fun setup(
        players: Int = 5,
        tricksters: Int = 1,
        categoryIds: Set<String> = setOf("a", "b"),
        whisper: WhisperLevel? = WhisperLevel.Medium,
        lastLie: Boolean = false,
    ) = GameSetup(
        playerNames = names(players),
        tricksterCount = tricksters,
        categoryIds = categoryIds,
        whisperLevel = whisper,
        tellingTimer = TellingTimer.Off,
        lastLieEnabled = lastLie,
    )
}
