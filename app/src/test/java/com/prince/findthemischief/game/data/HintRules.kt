package com.prince.findthemischief.game.data

/**
 * Test helper: does a hint contain a *form* of the secret word?
 * Catches plurals and simple endings ("Darts" vs "Dartboard"), and each part of a
 * multi-word Truth ("Space station" vs "Outer space").
 */
object HintRules {
    private val SUFFIXES = listOf("ing", "es", "ed", "er", "s")
    private const val MIN_STEM = 4

    fun revealsWord(word: String, hint: String): Boolean {
        val hintText = hint.lowercase()
        val hintTokens = tokens(hint).toSet()
        return tokens(word).filter { it.length >= 3 }.any { token ->
            val stem = stem(token)
            token in hintTokens || (stem.length >= MIN_STEM && hintText.contains(stem))
        }
    }

    private fun tokens(text: String) = text.lowercase().split(Regex("[^\\p{L}\\p{N}]+")).filter { it.isNotEmpty() }

    private fun stem(token: String): String {
        for (suffix in SUFFIXES) {
            if (token.endsWith(suffix) && token.length - suffix.length >= MIN_STEM) {
                return token.dropLast(suffix.length)
            }
        }
        return token
    }
}
