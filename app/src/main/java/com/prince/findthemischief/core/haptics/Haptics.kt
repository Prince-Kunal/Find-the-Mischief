package com.prince.findthemischief.core.haptics

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Vibration cues. Uses the Vibrator directly (not touch feedback) so the buzz still fires
 * when the phone is on silent or the user has turned off "touch vibration".
 */
class Haptics(context: Context) {

    private val vibrator: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }

    /** A firm double pulse: "the phone has changed hands". */
    fun passBuzz() {
        val timings = longArrayOf(0, 70, 60, 110)
        vibrate(
            if (vibrator?.hasAmplitudeControl() == true) {
                VibrationEffect.createWaveform(timings, intArrayOf(0, 255, 0, 200), -1)
            } else {
                VibrationEffect.createWaveform(timings, -1)
            },
        )
    }

    /** A short, light tick, e.g. when a card glows open. */
    fun tick() = vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))

    private fun vibrate(effect: VibrationEffect) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // "Media" vibrations keep working in silent mode, unlike ring/notification ones.
            v.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_MEDIA))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(effect, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build())
        }
    }
}
