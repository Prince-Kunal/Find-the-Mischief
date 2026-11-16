package com.prince.findthemischief

import android.content.Context

/**
 * Manual dependency injection: the one place that creates the app's long-lived objects
 * (repositories, sound player, haptics) and hands them to ViewModels via their constructors.
 *
 * Filled in as phases land: WordRepository (Phase 2), SettingsRepository (Phase 3),
 * SoundPlayer and Haptics (Phase 5).
 */
class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext
}
