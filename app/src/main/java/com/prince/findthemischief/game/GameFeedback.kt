package com.prince.findthemischief.game

/**
 * Sounds and vibrations the game asks for. An interface so the ViewModel stays testable
 * without a phone; the real one is core.DeviceFeedback.
 */
interface GameFeedback {
    /** "Next player" was pressed: chime + buzz so everyone knows the phone changed hands. */
    fun phonePassed()

    /** A player's card glowed open for the first time. */
    fun cardRevealed()

    companion object {
        val None: GameFeedback = object : GameFeedback {
            override fun phonePassed() = Unit
            override fun cardRevealed() = Unit
        }
    }
}
