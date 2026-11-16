package com.prince.findthemischief.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.prince.findthemischief.R
import java.util.concurrent.ConcurrentHashMap

enum class Sound(@param:RawRes val resId: Int) {
    PassChime(R.raw.pass_chime),
    RevealGlow(R.raw.reveal_glow),
    JudgementDrum(R.raw.judgement_drum),
    TimerEnd(R.raw.timer_end),
}

/**
 * Plays the app's short sound effects. All sounds are loaded into memory once, when this is
 * created, so they play instantly. Uses the media volume, so the ringer's silent switch
 * doesn't mute it (the volume buttons do).
 */
class SoundPlayer(context: Context) {

    /** Mirrors the "Sound" setting. When false, [play] does nothing (haptics are separate). */
    @Volatile
    var enabled: Boolean = true

    private val pool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val loaded: MutableSet<Int> = ConcurrentHashMap.newKeySet()
    private val soundIds: Map<Sound, Int>

    init {
        // The listener must be set before loading so no "loaded" event is missed.
        pool.setOnLoadCompleteListener { _, soundId, status -> if (status == 0) loaded += soundId }
        soundIds = Sound.entries.associateWith { pool.load(context, it.resId, 1) }
    }

    fun play(sound: Sound) {
        if (!enabled) return
        val id = soundIds[sound] ?: return
        if (id in loaded) pool.play(id, 1f, 1f, 1, 0, 1f)
    }
}
