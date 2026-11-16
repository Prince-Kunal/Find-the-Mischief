package com.prince.findthemischief.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.prince.findthemischief.game.domain.TellingTimer
import com.prince.findthemischief.game.domain.WhisperLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/** Runs the real DataStore against a temporary file on the computer (no phone needed). */
class SettingsRepositoryTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun file() = File(tmp.root, "settings.preferences_pb")

    /** Opens the store, runs [block], then closes it, like one launch of the app. */
    private fun withRepository(block: suspend (SettingsRepository) -> Unit) = runBlocking {
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + job)) { file() }
        block(SettingsRepository(store))
        job.cancelAndJoin()
    }

    @Test
    fun `first launch gives the defaults`() = withRepository { repo ->
        assertEquals(SavedSetup(), repo.savedSetup.first())
    }

    @Test
    fun `setup survives a restart`() {
        val chosen = SavedSetup(
            playerNames = listOf("Asha", "Bilal", "", "Dev"),
            tricksterCount = 2,
            excludedCategoryIds = setOf("tech", "sports"),
            whisperEnabled = false,
            whisperLevel = WhisperLevel.Hard,
            tellingTimer = TellingTimer.Seconds90,
            lastLieEnabled = true,
        )
        withRepository { it.saveSetup(chosen) }
        withRepository { assertEquals(chosen, it.savedSetup.first()) }
    }

    @Test
    fun `names with commas, quotes and emoji round-trip`() {
        val names = listOf("Kumar, Jr.", "\"Ace\"", "Zoë 🐍")
        withRepository { it.saveSetup(SavedSetup(playerNames = names)) }
        withRepository { assertEquals(names, it.savedSetup.first().playerNames) }
    }
}
