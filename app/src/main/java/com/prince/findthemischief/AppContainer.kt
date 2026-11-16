package com.prince.findthemischief

import android.content.Context
import com.prince.findthemischief.game.data.WordRepository

/**
 * Manual dependency injection: the one place that creates the app's long-lived objects
 * (repositories, sound player, haptics) and hands them to ViewModels via their constructors.
 *
 * Still to come: SettingsRepository (Phase 3), SoundPlayer and Haptics (Phase 5).
 */
class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext

    val wordRepository: WordRepository by lazy {
        WordRepository(readJson = {
            appContext.assets.open(WordRepository.ASSET_FILE).bufferedReader().use { it.readText() }
        })
    }
}
