package com.prince.findthemischief.game.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/** Turns the text of words.json into a [WordBank]. */
object WordBankParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(text: String): WordBank = json.decodeFromString(text)
}

/**
 * Loads the word bank once, then serves it from memory.
 *
 * [readJson] supplies the raw file text. In the app it reads `assets/words.json`
 * (see AppContainer); in tests it can return any string. It runs on [ioDispatcher]
 * because reading a file shouldn't block the main (UI) thread.
 */
class WordRepository(
    private val readJson: () -> String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val mutex = Mutex()

    @Volatile
    private var cached: WordBank? = null

    suspend fun wordBank(): WordBank =
        cached ?: mutex.withLock {
            cached ?: withContext(ioDispatcher) { WordBankParser.parse(readJson()) }
                .also { cached = it }
        }

    suspend fun categories(): List<WordCategory> = wordBank().categories

    companion object {
        const val ASSET_FILE = "words.json"
    }
}
