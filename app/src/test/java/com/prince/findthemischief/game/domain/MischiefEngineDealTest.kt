package com.prince.findthemischief.game.domain

import com.prince.findthemischief.game.domain.EngineTestData.categories
import com.prince.findthemischief.game.domain.EngineTestData.category
import com.prince.findthemischief.game.domain.EngineTestData.setup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Dealing a game: Tricksters, the Truth, Whispers and word repeats. */
class MischiefEngineDealTest {

    @Test
    fun `deals exactly the requested number of Tricksters`() {
        for (seed in 0 until 50) {
            for ((players, tricksters) in listOf(3 to 1, 7 to 2, 10 to 3, 20 to 6)) {
                val game = MischiefEngine(Random(seed)).newGame(setup(players, tricksters), categories)
                assertEquals("seed=$seed players=$players", tricksters, game.tricksters.size)
                assertTrue(game.tricksters.all { it in 0 until players })
            }
        }
    }

    @Test
    fun `an illegal Trickster count is clamped`() {
        val game = MischiefEngine(Random(1)).newGame(setup(players = 4, tricksters = 3), categories)
        assertEquals(1, game.tricksters.size)
    }

    @Test
    fun `every player is sometimes the Trickster`() {
        val engine = MischiefEngine(Random(7))
        val seen = (1..200).flatMap { engine.newGame(setup(players = 5), categories).tricksters }.toSet()
        assertEquals((0 until 5).toSet(), seen)
    }

    @Test
    fun `the Whisper matches the chosen level`() {
        for (level in WhisperLevel.entries) {
            val game = MischiefEngine(Random(3)).newGame(setup(whisper = level), categories)
            val prefix = level.name.lowercase()
            assertEquals("$prefix-${game.truth}", game.whisper)
        }
    }

    @Test
    fun `Whispers off means no Whisper`() {
        val game = MischiefEngine(Random(3)).newGame(setup(whisper = null), categories)
        assertNull(game.whisper)
    }

    @Test
    fun `Tricksters get the Whisper and the Honest get the Truth`() {
        val game = MischiefEngine(Random(5)).newGame(setup(players = 7, tricksters = 2, whisper = WhisperLevel.Easy), categories)
        for (player in game.players.indices) {
            val expected = if (player in game.tricksters) {
                RoleCard.Trickster("easy-${game.truth}")
            } else {
                RoleCard.Honest(game.truth)
            }
            assertEquals(expected, game.cardFor(player))
        }
    }

    @Test
    fun `the Truth comes only from selected categories`() {
        val engine = MischiefEngine(Random(11))
        repeat(30) {
            val game = engine.newGame(setup(categoryIds = setOf("b")), categories)
            assertEquals("b", game.categoryId)
            assertTrue(game.truth.startsWith("b"))
        }
    }

    @Test
    fun `no word repeats until the pool runs out`() {
        val engine = MischiefEngine(Random(21))
        val firstEight = (1..8).map { engine.newGame(setup(), categories).truth }
        assertEquals("all 8 words used once", 8, firstEight.toSet().size)
    }

    @Test
    fun `no immediate repeat, even after the pool resets`() {
        for (seed in 0 until 30) {
            val engine = MischiefEngine(Random(seed))
            val truths = (1..40).map { engine.newGame(setup(), categories).truth }
            truths.zipWithNext().forEach { (a, b) -> assertNotEquals("seed=$seed", a, b) }
        }
    }

    @Test
    fun `a two-word pool alternates`() {
        val engine = MischiefEngine(Random(2))
        val tiny = listOf(category("t", 2))
        val truths = (1..10).map { engine.newGame(setup(categoryIds = setOf("t")), tiny).truth }
        truths.zipWithNext().forEach { (a, b) -> assertNotEquals(a, b) }
    }

    @Test
    fun `the same seed deals the same game`() {
        val a = MischiefEngine(Random(99)).newGame(setup(players = 9, tricksters = 2), categories)
        val b = MischiefEngine(Random(99)).newGame(setup(players = 9, tricksters = 2), categories)
        assertEquals(a, b)
    }

    @Test
    fun `a new game starts on player 1's Pass screen`() {
        val game = MischiefEngine(Random(1)).newGame(setup(), categories)
        assertEquals(GameState.Reveal(playerIndex = 0, showingCard = false), game.state)
    }
}
