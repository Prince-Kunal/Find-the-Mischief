package com.prince.findthemischief.game.domain

import com.prince.findthemischief.game.domain.EngineTestData.categories
import com.prince.findthemischief.game.domain.EngineTestData.setup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Moving through a game: reveal, Telling, Judgement, last lie, result. */
class MischiefEngineFlowTest {

    private fun engine(seed: Int = 1) = MischiefEngine(Random(seed))

    /** Deals and walks every player through Pass → Card → next, ending on "All fates are sealed." */
    private fun MischiefEngine.sealed(setup: GameSetup): Game {
        var game = newGame(setup, categories)
        repeat(setup.playerNames.size) { game = nextPlayer(revealCard(game)) }
        return game
    }

    private fun MischiefEngine.judging(setup: GameSetup) = beginJudgement(beginTelling(sealed(setup)))

    @Test
    fun `reveal goes Pass, Card, next player's Pass, never card to card`() {
        val e = engine()
        var game = e.newGame(setup(players = 3), categories)
        for (player in 0 until 3) {
            assertEquals(GameState.Reveal(player, showingCard = false), game.state)
            assertSame("can't skip ahead from a Pass screen", game, e.nextPlayer(game))
            game = e.revealCard(game)
            assertEquals(GameState.Reveal(player, showingCard = true), game.state)
            assertSame("revealing twice changes nothing", game, e.revealCard(game))
            game = e.nextPlayer(game)
        }
        assertEquals(GameState.AllSealed, game.state)
    }

    @Test
    fun `a Trickster never starts the Telling`() {
        for (seed in 0 until 200) {
            val e = engine(seed)
            var game = e.beginTelling(e.sealed(setup(players = 10, tricksters = 3)))
            repeat(5) {
                val order = (game.state as GameState.Telling).order
                assertFalse("seed=$seed starter=${order.first()}", game.isTrickster(order.first()))
                game = e.anotherTellingRound(game)
            }
        }
    }

    @Test
    fun `Telling order is everyone once, in seat order, rotated to the starter`() {
        val e = engine(4)
        val game = e.beginTelling(e.sealed(setup(players = 6)))
        val order = (game.state as GameState.Telling).order
        assertEquals((0 until 6).toSet(), order.toSet())
        order.zipWithNext().forEach { (a, b) -> assertEquals((a + 1) % 6, b) }
    }

    @Test
    fun `one Trickster - banishing them finds the mischief`() {
        val e = engine()
        val game = e.judging(setup(players = 5))
        val result = e.banish(game, game.tricksters.single())
        assertEquals(GameState.Result(Team.Honest, ResultReason.AllTrickstersBanished), result.state)
    }

    @Test
    fun `one Trickster - banishing an Honest player and the Trickster prevails`() {
        val e = engine()
        val game = e.judging(setup(players = 5))
        val honest = game.players.indices.first { !game.isTrickster(it) }
        assertEquals(GameState.Result(Team.Tricksters, ResultReason.HonestBanished), e.banish(game, honest).state)
    }

    @Test
    fun `last lie - a right guess wins, ignoring case and spaces`() {
        val e = engine()
        val game = e.judging(setup(lastLie = true))
        val lastLie = e.banish(game, game.tricksters.single())
        assertEquals(GameState.LastLie(game.tricksters.single()), lastLie.state)
        val result = e.guessTruth(lastLie, "  ${game.truth.uppercase()} ")
        assertEquals(GameState.Result(Team.Tricksters, ResultReason.LastLieGuessed, game.truth.uppercase()), result.state)
    }

    @Test
    fun `last lie - a wrong guess and the Honest win`() {
        val e = engine()
        val game = e.judging(setup(lastLie = true))
        val result = e.guessTruth(e.banish(game, game.tricksters.single()), "nonsense")
        assertEquals(Team.Honest, (result.state as GameState.Result).winner)
        assertEquals(ResultReason.LastLieMissed, (result.state as GameState.Result).reason)
    }

    @Test
    fun `several Tricksters - after a banishment the game returns to the Telling without them`() {
        val e = engine()
        val game = e.judging(setup(players = 10, tricksters = 3))
        val honest = game.players.indices.first { !game.isTrickster(it) }
        val next = e.banish(game, honest)
        val telling = next.state as GameState.Telling
        assertEquals(2, telling.round)
        assertFalse(honest in telling.order)
        assertEquals(9, telling.order.size)
        assertFalse(next.isTrickster(telling.order.first()))
    }

    @Test
    fun `several Tricksters - only the final Trickster gets the last lie`() {
        val e = engine()
        var game = e.judging(setup(players = 7, tricksters = 2, lastLie = true))
        val (first, second) = game.tricksters.toList()
        game = e.banish(game, first)
        assertTrue("first Trickster is simply out", game.state is GameState.Telling)
        game = e.banish(e.beginJudgement(game), second)
        assertEquals(GameState.LastLie(second), game.state)
    }

    @Test
    fun `several Tricksters - all banished finds the mischief`() {
        val e = engine()
        var game = e.judging(setup(players = 7, tricksters = 2))
        val (first, second) = game.tricksters.toList()
        game = e.banish(e.beginJudgement(e.banish(game, first)), second)
        assertEquals(GameState.Result(Team.Honest, ResultReason.AllTrickstersBanished), game.state)
    }

    @Test
    fun `several Tricksters - they prevail once they equal the Honest`() {
        val e = engine()
        // 7 players, 2 Tricksters, 5 Honest. Banish 3 Honest -> 2 v 2.
        var game = e.judging(setup(players = 7, tricksters = 2))
        val honest = game.players.indices.filterNot { game.isTrickster(it) }
        game = e.banish(game, honest[0])
        game = e.banish(e.beginJudgement(game), honest[1])
        assertTrue(game.state is GameState.Telling)
        game = e.banish(e.beginJudgement(game), honest[2])
        assertEquals(GameState.Result(Team.Tricksters, ResultReason.TrickstersEqualHonest), game.state)
        assertEquals(honest.take(3), game.banished)
    }

    @Test
    fun `moves that don't fit the phase change nothing`() {
        val e = engine()
        val sealed = e.sealed(setup())
        assertSame(sealed, e.beginJudgement(sealed))
        assertSame(sealed, e.banish(sealed, 0))
        assertSame(sealed, e.guessTruth(sealed, "x"))
        val judging = e.beginJudgement(e.beginTelling(sealed))
        assertSame("unknown player", judging, e.banish(judging, 99))
        val banished = e.banish(judging, judging.players.indices.first { !judging.isTrickster(it) })
        assertSame("already decided", banished, e.banish(banished, 0))
    }
}
