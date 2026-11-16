package com.prince.findthemischief.core

import com.prince.findthemischief.core.audio.Sound
import com.prince.findthemischief.core.audio.SoundPlayer
import com.prince.findthemischief.core.haptics.Haptics
import com.prince.findthemischief.game.GameFeedback

/** The real, on-device [GameFeedback]: sound (if enabled) plus vibration (always). */
class DeviceFeedback(
    private val sounds: SoundPlayer,
    private val haptics: Haptics,
) : GameFeedback {

    override fun phonePassed() {
        haptics.passBuzz()
        sounds.play(Sound.PassChime)
    }

    override fun cardRevealed() {
        haptics.tick()
        sounds.play(Sound.RevealGlow)
    }
}
