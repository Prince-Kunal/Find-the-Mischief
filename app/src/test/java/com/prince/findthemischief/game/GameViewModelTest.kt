package com.prince.findthemischief.game

import com.prince.findthemischief.game.data.WordBank
import com.prince.findthemischief.game.data.WordRepository
import com.prince.findthemischief.game.domain.EngineTestData
import com.prince.findthemischief.game.domain.GameSetup
import com.prince.findthemischief.game.domain.MischiefEngine
import com.prince.findthemischief.game.domain.RoleCard
import com.prince.findthemischief.game.domain.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    // ViewModels launch work on the "main" thread; in a plain JVM test we swap in a test dispatcher.
    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Records feedback calls instead of making sounds. */
    private class RecordingFeedback : GameFeedback {
        var passes = 0
        var reveals = 0
        override fun phonePassed() { passes++ }
        override fun cardRevealed() { reveals++ }
    }

    private val feedback = RecordingFeedback()

    private fun viewModel(setup: GameSetup = EngineTestData.setup(players = 3)): GameViewModel {
        val json = Json.encodeToString(WordBank(1, EngineTestData.categories))
        val words = WordRepository(readJson = { json }, ioDispatcher = Dispatchers.Unconfined)
        return GameViewModel(setup, MischiefEngine(Random(8)), words, feedback)
    }

    private val GameViewModel.state get() = uiState.value

    @Test
    fun `walks every player through Pass then Card, then seals`() {
        val vm = viewModel()
        for (i in 0 until 3) {
            val pass = vm.state as GameUiState.Pass
            assertEquals("P${i + 1}", pass.player.name)
            assertEquals(i + 1, pass.position)
            vm.onNextPlayer() // ignored: must reveal first
            assertTrue(vm.state is GameUiState.Pass)
            vm.onRevealCard()
            val card = vm.state as GameUiState.Card
            assertEquals(i == 2, card.isLastPlayer)
            vm.onNextPlayer()
        }
        assertEquals(GameUiState.AllSealed, vm.state)
    }

    @Test
    fun `a full game ends in a result with the Truth`() {
        val vm = viewModel()
        var trickster = -1
        repeat(3) {
            vm.onRevealCard()
            val card = vm.state as GameUiState.Card
            if (card.card is RoleCard.Trickster) trickster = card.player.index
            vm.onNextPlayer()
        }
        vm.onBeginTelling()
        val telling = vm.state as GameUiState.Telling
        assertTrue(telling.order.first().index != trickster)
        vm.onBeginJudgement()
        assertEquals(3, (vm.state as GameUiState.Judgement).candidates.size)
        vm.onBanish(trickster)
        val result = vm.state as GameUiState.Result
        assertEquals(Team.Honest, result.winner)
        assertEquals(listOf(trickster), result.tricksters.map { it.index })
    }

    @Test
    fun `play again deals a fresh game from player 1`() {
        val vm = viewModel()
        vm.onRevealCard()
        vm.onPlayAgain()
        assertEquals(1, (vm.state as GameUiState.Pass).position)
    }

    @Test
    fun `every real Next player press chimes and buzzes exactly once`() {
        val vm = viewModel()
        vm.onNextPlayer() // on a Pass screen: ignored, so no chime
        assertEquals(0, feedback.passes)
        repeat(3) {
            vm.onRevealCard()
            vm.onNextPlayer()
            vm.onNextPlayer() // double tap: second press lands on a Pass screen and is ignored
        }
        assertEquals(3, feedback.passes)
        assertEquals(GameUiState.AllSealed, vm.state)
    }

    @Test
    fun `a card glowing open asks for the reveal sound`() {
        val vm = viewModel()
        vm.onRevealCard()
        vm.onCardRevealed()
        assertEquals(1, feedback.reveals)
    }
}
