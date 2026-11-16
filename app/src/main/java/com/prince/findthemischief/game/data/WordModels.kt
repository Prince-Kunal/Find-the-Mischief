package com.prince.findthemischief.game.data

import kotlinx.serialization.Serializable

// Mirrors app/src/main/assets/words.json. Pure Kotlin: no Android imports.

@Serializable
data class WordBank(
    val version: Int,
    val categories: List<WordCategory>,
)

@Serializable
data class WordCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val words: List<WordEntry>,
)

/** One possible Truth and the three Whispers the Trickster might hear for it. */
@Serializable
data class WordEntry(
    val word: String,
    val hints: Hints,
)

/** Whisper strength, from the Trickster's point of view: easy = a close clue, hard = a vague one. */
@Serializable
data class Hints(
    val easy: String,
    val medium: String,
    val hard: String,
)
