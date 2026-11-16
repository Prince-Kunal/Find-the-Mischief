package com.prince.findthemischief.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.prince.findthemischief.game.domain.TellingTimer
import com.prince.findthemischief.game.domain.WhisperLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

/** The one DataStore file for the whole app, created lazily the first time it's used. */
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * The Setup screen's choices exactly as last left, so they're pre-filled next time.
 * Names may still be blank or duplicated here; validation happens when a game begins.
 */
data class SavedSetup(
    val playerNames: List<String> = emptyList(),
    val tricksterCount: Int = 1,
    /**
     * Categories the group switched *off*. Stored this way round so that "all" is the default
     * and any category added in a future update starts switched on.
     */
    val excludedCategoryIds: Set<String> = emptySet(),
    val whisperEnabled: Boolean = true,
    val whisperLevel: WhisperLevel = WhisperLevel.Medium,
    val tellingTimer: TellingTimer = TellingTimer.Off,
    val lastLieEnabled: Boolean = false,
)

/** How a card is revealed: shown only while a finger is held down, or flipped with a tap. */
enum class RevealMode { Hold, Tap }

/** App-wide preferences (the Settings screen, Phase 8). */
data class AppPreferences(
    val soundEnabled: Boolean = true,
    val revealMode: RevealMode = RevealMode.Hold,
)

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private val safeData: Flow<Preferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    val appPreferences: Flow<AppPreferences> = safeData.map {
        AppPreferences(
            soundEnabled = it[Keys.SOUND_ENABLED] ?: true,
            revealMode = enumOrDefault(it[Keys.REVEAL_MODE], RevealMode.Hold),
        )
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setRevealMode(mode: RevealMode) {
        dataStore.edit { it[Keys.REVEAL_MODE] = mode.name }
    }

    val savedSetup: Flow<SavedSetup> = safeData.map { it.toSavedSetup() }

    suspend fun saveSetup(setup: SavedSetup) {
        dataStore.edit { prefs ->
            prefs[Keys.PLAYER_NAMES] = Json.encodeToString(setup.playerNames)
            prefs[Keys.TRICKSTER_COUNT] = setup.tricksterCount
            prefs[Keys.EXCLUDED_CATEGORIES] = setup.excludedCategoryIds
            prefs[Keys.WHISPER_ENABLED] = setup.whisperEnabled
            prefs[Keys.WHISPER_LEVEL] = setup.whisperLevel.name
            prefs[Keys.TELLING_TIMER] = setup.tellingTimer.name
            prefs[Keys.LAST_LIE] = setup.lastLieEnabled
        }
    }

    private fun Preferences.toSavedSetup(): SavedSetup {
        val defaults = SavedSetup()
        return SavedSetup(
            playerNames = this[Keys.PLAYER_NAMES]
                ?.let { runCatching { Json.decodeFromString<List<String>>(it) }.getOrNull() }
                ?: defaults.playerNames,
            tricksterCount = this[Keys.TRICKSTER_COUNT] ?: defaults.tricksterCount,
            excludedCategoryIds = this[Keys.EXCLUDED_CATEGORIES] ?: defaults.excludedCategoryIds,
            whisperEnabled = this[Keys.WHISPER_ENABLED] ?: defaults.whisperEnabled,
            whisperLevel = enumOrDefault(this[Keys.WHISPER_LEVEL], defaults.whisperLevel),
            tellingTimer = enumOrDefault(this[Keys.TELLING_TIMER], defaults.tellingTimer),
            lastLieEnabled = this[Keys.LAST_LIE] ?: defaults.lastLieEnabled,
        )
    }

    private inline fun <reified E : Enum<E>> enumOrDefault(name: String?, default: E): E =
        enumValues<E>().firstOrNull { it.name == name } ?: default

    private object Keys {
        val PLAYER_NAMES = stringPreferencesKey("setup_player_names")
        val TRICKSTER_COUNT = intPreferencesKey("setup_trickster_count")
        val EXCLUDED_CATEGORIES = stringSetPreferencesKey("setup_excluded_categories")
        val WHISPER_ENABLED = booleanPreferencesKey("setup_whisper_enabled")
        val WHISPER_LEVEL = stringPreferencesKey("setup_whisper_level")
        val TELLING_TIMER = stringPreferencesKey("setup_telling_timer")
        val LAST_LIE = booleanPreferencesKey("setup_last_lie")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val REVEAL_MODE = stringPreferencesKey("reveal_mode")
    }
}
