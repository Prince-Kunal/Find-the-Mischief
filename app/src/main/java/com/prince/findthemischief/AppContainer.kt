package com.prince.findthemischief

import android.content.Context
import com.prince.findthemischief.core.DeviceFeedback
import com.prince.findthemischief.core.audio.SoundPlayer
import com.prince.findthemischief.core.haptics.Haptics
import com.prince.findthemischief.core.data.SettingsRepository
import com.prince.findthemischief.core.data.settingsDataStore
import com.prince.findthemischief.game.data.WordRepository
import com.prince.findthemischief.game.GameFeedback
import com.prince.findthemischief.game.domain.MischiefEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manual dependency injection: the one place that creates the app's long-lived objects
 * (repositories, sound player, haptics) and hands them to ViewModels via their constructors.
 */
class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext

    /** Lives as long as the app process; for small background jobs that outlive any one screen. */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val wordRepository: WordRepository by lazy {
        WordRepository(readJson = {
            appContext.assets.open(WordRepository.ASSET_FILE).bufferedReader().use { it.readText() }
        })
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(appContext.settingsDataStore)
    }

    /** One engine per app session, so words aren't repeated across games until the pool runs out. */
    val mischiefEngine: MischiefEngine by lazy { MischiefEngine() }

    val soundPlayer: SoundPlayer by lazy {
        SoundPlayer(appContext).also { player ->
            // Keep the player in step with the "Sound" setting.
            appScope.launch { settingsRepository.appPreferences.collect { player.enabled = it.soundEnabled } }
        }
    }

    val haptics: Haptics by lazy { Haptics(appContext) }

    val gameFeedback: GameFeedback by lazy { DeviceFeedback(soundPlayer, haptics) }
}
