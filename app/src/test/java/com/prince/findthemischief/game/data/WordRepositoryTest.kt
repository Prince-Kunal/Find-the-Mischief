package com.prince.findthemischief.game.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class WordRepositoryTest {

    private val tinyBank = """
        {
          "version": 1,
          "categories": [
            { "id": "food", "name": "Food", "emoji": "🍕",
              "words": [ { "word": "Pizza", "hints": { "easy": "Pepperoni", "medium": "Italian food", "hard": "Round" } } ] }
          ]
        }
    """.trimIndent()

    @Test
    fun `parses categories and hints`() = runBlocking {
        val repo = WordRepository(readJson = { tinyBank }, ioDispatcher = Dispatchers.Unconfined)
        val pizza = repo.categories().single().words.single()
        assertEquals("Pizza", pizza.word)
        assertEquals(Hints("Pepperoni", "Italian food", "Round"), pizza.hints)
    }

    @Test
    fun `reads the file only once and caches the result`() = runBlocking {
        var reads = 0
        val repo = WordRepository(readJson = { reads++; tinyBank }, ioDispatcher = Dispatchers.Unconfined)
        val first = repo.wordBank()
        val second = repo.wordBank()
        assertEquals(1, reads)
        assertSame(first, second)
    }
}
