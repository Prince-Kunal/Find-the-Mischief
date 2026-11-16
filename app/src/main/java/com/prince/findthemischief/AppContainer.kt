package com.prince.findthemischief

import android.content.Context
import com.prince.findthemischief.core.data.SettingsRepository
import com.prince.findthemischief.core.data.settingsDataStore
import com.prince.findthemischief.game.data.WordRepository

/**
 * Manual dependency injection: the one place that creates the app's long-lived objects
 * (repositories, sound player, haptics) and hands them to ViewModels via their constructors.
 *
 * Still to come: SoundPlayer and Haptics (Phase 5).
 */
class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext

    val wordRepository: WordRepository by lazy {
        WordRepository(readJson = {
            appContext.assets.open(WordRepository.ASSET_FILE).bufferedReader().use { it.readText() }
        })
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(appContext.settingsDataStore)
    }
}
