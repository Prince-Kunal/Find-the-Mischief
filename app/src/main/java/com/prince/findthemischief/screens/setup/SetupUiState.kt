package com.prince.findthemischief.screens.setup

import com.prince.findthemischief.game.domain.SetupProblem
import com.prince.findthemischief.game.domain.SetupRules
import com.prince.findthemischief.game.domain.TellingTimer
import com.prince.findthemischief.game.domain.WhisperLevel

/** A player row on screen. [id] stays fixed while the row is renamed or moved, so the list animates correctly. */
data class PlayerRow(val id: Long, val name: String)

data class CategoryOption(val id: String, val name: String, val emoji: String, val selected: Boolean)

/** Everything the Setup screen draws. Derived values are computed here, not in the UI. */
data class SetupUiState(
    val isLoading: Boolean = true,
    val players: List<PlayerRow> = emptyList(),
    val tricksterCount: Int = 1,
    val categories: List<CategoryOption> = emptyList(),
    val whisperEnabled: Boolean = true,
    val whisperLevel: WhisperLevel = WhisperLevel.Medium,
    val tellingTimer: TellingTimer = TellingTimer.Off,
    val lastLieEnabled: Boolean = false,
    /** Problems stay hidden until the group first tries to begin, so the screen doesn't nag. */
    val showProblems: Boolean = false,
) {
    val maxTricksters: Int get() = SetupRules.maxTricksters(players.size)
    val canAddPlayer: Boolean get() = players.size < SetupRules.MAX_PLAYERS
    val canRemovePlayer: Boolean get() = players.size > SetupRules.MIN_PLAYERS
    val selectedCategoryIds: Set<String> get() = categories.filter { it.selected }.map { it.id }.toSet()

    val visibleProblems: List<SetupProblem>
        get() = if (showProblems) SetupRules.problems(players.map { it.name }, selectedCategoryIds) else emptyList()

    /** Row positions to outline in red (blank or duplicate names). */
    val invalidPlayerIndices: Set<Int>
        get() = visibleProblems.flatMap {
            when (it) {
                is SetupProblem.BlankNames -> it.indices
                is SetupProblem.DuplicateName -> it.indices
                else -> emptyList()
            }
        }.toSet()
}

/** Every event the Setup screen can send. Implemented by [SetupViewModel]. */
interface SetupActions {
    fun onNameChange(id: Long, name: String)
    fun onAddPlayer()
    fun onRemovePlayer(id: Long)
    fun onMovePlayer(id: Long, offset: Int)
    fun onTricksterCountChange(count: Int)
    fun onCategoryToggle(id: String)
    fun onSelectAllCategories(selected: Boolean)
    fun onWhisperEnabledChange(enabled: Boolean)
    fun onWhisperLevelChange(level: WhisperLevel)
    fun onTellingTimerChange(timer: TellingTimer)
    fun onLastLieChange(enabled: Boolean)
}
