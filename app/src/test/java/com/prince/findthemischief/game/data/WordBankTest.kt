package com.prince.findthemischief.game.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

/** Checks the real assets/words.json so a bad edit to the word list fails the build's tests. */
class WordBankTest {

    companion object {
        private lateinit var bank: WordBank

        // Unit tests run with the app module folder as the working directory.
        private val WORDS_FILE = File("src/main/assets/${WordRepository.ASSET_FILE}")

        private val EXPECTED_CATEGORIES = setOf(
            "food", "animals", "places", "movies_shows", "sports", "objects",
            "jobs", "bollywood_cricket", "festivals", "tech", "norse_myth",
        )

        @BeforeClass
        @JvmStatic
        fun loadBank() {
            bank = WordBankParser.parse(WORDS_FILE.readText())
        }
    }

    private val allEntries get() = bank.categories.flatMap { c -> c.words.map { c to it } }

    @Test
    fun `file parses with all starter categories`() {
        assertEquals(1, bank.version)
        assertEquals(EXPECTED_CATEGORIES, bank.categories.map { it.id }.toSet())
        assertEquals("category ids must be unique", bank.categories.size, EXPECTED_CATEGORIES.size)
    }

    @Test
    fun `every category has a name, an emoji and at least 30 words`() {
        for (category in bank.categories) {
            assertTrue("${category.id}: blank name", category.name.isNotBlank())
            assertTrue("${category.id}: blank emoji", category.emoji.isNotBlank())
            assertTrue(
                "${category.id}: only ${category.words.size} words",
                category.words.size >= 30,
            )
        }
    }

    @Test
    fun `every word has three non-blank hints`() {
        val problems = allEntries.filter { (_, e) ->
            e.word.isBlank() || listOf(e.hints.easy, e.hints.medium, e.hints.hard).any { it.isBlank() }
        }.map { (c, e) -> "${c.id}/${e.word}" }
        assertTrue("Blank word or hint: $problems", problems.isEmpty())
    }

    @Test
    fun `no hint contains its word, case-insensitive`() {
        val problems = allEntries.flatMap { (c, e) ->
            e.hints.all().filter { it.contains(e.word, ignoreCase = true) }
                .map { "${c.id}/${e.word} -> \"$it\"" }
        }
        assertTrue("Hints that give the Truth away: $problems", problems.isEmpty())
    }

    @Test
    fun `no hint contains a form of its word`() {
        val problems = allEntries.flatMap { (c, e) ->
            e.hints.all().filter { HintRules.revealsWord(e.word, it) }
                .map { "${c.id}/${e.word} -> \"$it\"" }
        }
        assertTrue("Hints that contain a form of the Truth: $problems", problems.isEmpty())
    }

    @Test
    fun `a word's three hints are all different`() {
        val problems = allEntries.filter { (_, e) ->
            e.hints.all().map { it.lowercase() }.toSet().size != 3
        }.map { (c, e) -> "${c.id}/${e.word}" }
        assertTrue("Repeated hints: $problems", problems.isEmpty())
    }

    @Test
    fun `no duplicate words within a category`() {
        for (category in bank.categories) {
            val duplicates = category.words.groupBy { it.word.trim().lowercase() }
                .filterValues { it.size > 1 }.keys
            assertTrue("${category.id} has duplicates: $duplicates", duplicates.isEmpty())
        }
    }

    private fun Hints.all() = listOf(easy, medium, hard)
}
