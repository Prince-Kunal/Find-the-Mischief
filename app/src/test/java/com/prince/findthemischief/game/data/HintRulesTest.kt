package com.prince.findthemischief.game.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Proves the word-list checker actually catches bad hints (so a passing WordBankTest means something). */
class HintRulesTest {

    @Test
    fun `catches plurals and word stems`() {
        assertTrue(HintRules.revealsWord("Darts", "Dartboard"))
        assertTrue(HintRules.revealsWord("Wrestling", "Wrestler"))
        assertTrue(HintRules.revealsWord("Virus", "Antivirus"))
    }

    @Test
    fun `catches any part of a multi-word Truth`() {
        assertTrue(HintRules.revealsWord("Space station", "Outer space"))
        assertTrue(HintRules.revealsWord("Bus stop", "Bus"))
    }

    @Test
    fun `allows unrelated hints`() {
        assertFalse(HintRules.revealsWord("Pizza", "Pepperoni"))
        assertFalse(HintRules.revealsWord("Cat", "Whiskers"))
        assertFalse(HintRules.revealsWord("Thor", "Thunder god"))
    }
}
