package com.prince.findthemischief.screens.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prince.findthemischief.R
import com.prince.findthemischief.game.domain.GameSetup
import com.prince.findthemischief.game.domain.SetupProblem
import com.prince.findthemischief.game.domain.SetupRules
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.components.ScreenHeader
import com.prince.findthemischief.ui.components.SectionTitle

/** Connects the Setup UI to its ViewModel. */
@Composable
fun SetupScreen(
    onBack: () -> Unit,
    onBegin: (GameSetup) -> Unit,
    viewModel: SetupViewModel = viewModel(factory = SetupViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SetupContent(
        state = state,
        actions = viewModel,
        onBack = onBack,
        onBegin = { viewModel.tryBegin()?.let(onBegin) },
    )
}

@Composable
fun SetupContent(
    state: SetupUiState,
    actions: SetupActions,
    onBack: () -> Unit,
    onBegin: () -> Unit,
) {
    MischiefBackground {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            ScreenHeader(
                title = stringResource(R.string.setup_title),
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            )
            if (state.isLoading) return@Column

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 8.dp, bottom = 24.dp),
            ) {
                item {
                    SectionTitle(
                        title = stringResource(R.string.setup_players_title, state.players.size),
                        subtitle = stringResource(R.string.setup_players_subtitle),
                    )
                }
                val invalid = state.invalidPlayerIndices
                itemsIndexed(state.players, key = { _, row -> row.id }) { index, row ->
                    PlayerRowItem(
                        row = row,
                        index = index,
                        count = state.players.size,
                        isError = index in invalid,
                        canRemove = state.canRemovePlayer,
                        actions = actions,
                        modifier = Modifier.animateItem(),
                    )
                }
                item { AddPlayerButton(state.canAddPlayer, actions::onAddPlayer, Modifier.padding(end = 8.dp)) }
                item {
                    TricksterSection(
                        count = state.tricksterCount,
                        max = state.maxTricksters,
                        playerCount = state.players.size,
                        onChange = actions::onTricksterCountChange,
                    )
                }
                item { Column(Modifier.padding(end = 8.dp)) { CategorySection(state.categories, actions) } }
                item {
                    Column(Modifier.padding(end = 8.dp)) {
                        WhisperSection(state.whisperEnabled, state.whisperLevel, actions)
                    }
                }
                item {
                    Column(Modifier.padding(end = 8.dp)) {
                        TimerSection(state.tellingTimer, actions::onTellingTimerChange)
                    }
                }
                item { LastLieSection(state.lastLieEnabled, actions::onLastLieChange) }
            }

            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                RuneDivider()
                Spacer(Modifier.height(12.dp))
                ProblemList(problemMessages(state.visibleProblems))
                MischiefButton(
                    text = stringResource(R.string.setup_begin),
                    onClick = onBegin,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun problemMessages(problems: List<SetupProblem>): List<String> = problems.map {
    when (it) {
        SetupProblem.TooFewPlayers -> stringResource(R.string.setup_problem_too_few, SetupRules.MIN_PLAYERS)
        SetupProblem.TooManyPlayers -> stringResource(R.string.setup_problem_too_many, SetupRules.MAX_PLAYERS)
        is SetupProblem.BlankNames -> pluralStringResource(R.plurals.setup_problem_blank, it.indices.size, it.indices.size)
        is SetupProblem.DuplicateName -> stringResource(R.string.setup_problem_duplicate, it.name)
        SetupProblem.NoCategories -> stringResource(R.string.setup_problem_no_categories)
    }
}
