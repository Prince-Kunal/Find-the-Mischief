package com.prince.findthemischief.game.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SetupRulesTest {

    private val cats = setOf("food")

    @Test
    fun `max Tricksters follows max(1, (players - 1) div 3)`() {
        val expected = mapOf(3 to 1, 4 to 1, 6 to 1, 7 to 2, 9 to 2, 10 to 3, 13 to 4, 20 to 6)
        expected.forEach { (players, max) -> assertEquals("players=$players", max, SetupRules.maxTricksters(players)) }
    }

    @Test
    fun `Trickster count is clamped to the legal range`() {
        assertEquals(1, SetupRules.clampTricksters(0, 10))
        assertEquals(3, SetupRules.clampTricksters(9, 10))
        assertEquals(2, SetupRules.clampTricksters(2, 7))
    }

    @Test
    fun `a good setup has no problems`() {
        assertTrue(SetupRules.problems(listOf("Asha", "Bilal", "Chen"), cats).isEmpty())
    }

    @Test
    fun `fewer than three players is a problem`() {
        assertEquals(listOf(SetupProblem.TooFewPlayers), SetupRules.problems(listOf("Asha", "Bilal"), cats))
    }

    @Test
    fun `more than twenty players is a problem`() {
        val names = (1..21).map { "P$it" }
        assertEquals(listOf(SetupProblem.TooManyPlayers), SetupRules.problems(names, cats))
    }

    @Test
    fun `blank and whitespace-only names are reported with their positions`() {
        val problems = SetupRules.problems(listOf("Asha", "", "  "), cats)
        assertEquals(listOf(SetupProblem.BlankNames(listOf(1, 2))), problems)
    }

    @Test
    fun `duplicate names are caught ignoring case and spaces`() {
        val problems = SetupRules.problems(listOf("Asha", " asha ", "Chen"), cats)
        assertEquals(listOf(SetupProblem.DuplicateName("Asha", listOf(0, 1))), problems)
    }

    @Test
    fun `no categories is a problem`() {
        assertEquals(listOf(SetupProblem.NoCategories), SetupRules.problems(listOf("A", "B", "C"), emptySet()))
    }

    @Test
    fun `validate trims names and clamps Tricksters`() {
        val setup = SetupRules.validate(
            playerNames = listOf(" Asha ", "Bilal", "Chen "),
            tricksterCount = 5,
            categoryIds = cats,
            whisperLevel = WhisperLevel.Hard,
            tellingTimer = TellingTimer.Seconds60,
            lastLieEnabled = true,
        )
        assertNotNull(setup)
        assertEquals(listOf("Asha", "Bilal", "Chen"), setup!!.playerNames)
        assertEquals(1, setup.tricksterCount)
    }

    @Test
    fun `validate refuses a setup with problems`() {
        assertNull(SetupRules.validate(listOf("A", "a", "B"), 1, cats, null, TellingTimer.Off, false))
    }
}
