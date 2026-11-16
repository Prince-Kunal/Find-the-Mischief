package com.prince.findthemischief.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.prince.findthemischief.MischiefApp
import com.prince.findthemischief.core.data.SavedSetup
import com.prince.findthemischief.core.data.SettingsRepository
import com.prince.findthemischief.game.data.WordRepository
import com.prince.findthemischief.game.domain.GameSetup
import com.prince.findthemischief.game.domain.SetupRules
import com.prince.findthemischief.game.domain.TellingTimer
import com.prince.findthemischief.game.domain.WhisperLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupViewModel(
    private val settings: SettingsRepository,
    private val words: WordRepository,
) : ViewModel(), SetupActions {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    private var nextPlayerId = 0L

    init {
        viewModelScope.launch {
            val saved = settings.savedSetup.first()
            val categories = runCatching { words.categories() }.getOrDefault(emptyList())
            val names = saved.playerNames.take(SetupRules.MAX_PLAYERS)
                .let { it + List((SetupRules.MIN_PLAYERS - it.size).coerceAtLeast(0)) { "" } }
            _uiState.value = SetupUiState(
                isLoading = false,
                players = names.map { newRow(it) },
                tricksterCount = SetupRules.clampTricksters(saved.tricksterCount, names.size),
                categories = categories.map {
                    CategoryOption(it.id, it.name, it.emoji, selected = it.id !in saved.excludedCategoryIds)
                },
                whisperEnabled = saved.whisperEnabled,
                whisperLevel = saved.whisperLevel,
                tellingTimer = saved.tellingTimer,
                lastLieEnabled = saved.lastLieEnabled,
            )
        }
    }

    /** Validates the setup. Returns it if it's ready to play; otherwise reveals the problems and returns null. */
    fun tryBegin(): GameSetup? {
        val state = _uiState.value
        if (state.isLoading) return null
        val setup = SetupRules.validate(
            playerNames = state.players.map { it.name },
            tricksterCount = state.tricksterCount,
            categoryIds = state.selectedCategoryIds,
            whisperLevel = state.whisperLevel.takeIf { state.whisperEnabled },
            tellingTimer = state.tellingTimer,
            lastLieEnabled = state.lastLieEnabled,
        )
        if (setup == null) _uiState.update { it.copy(showProblems = true) }
        return setup
    }

    override fun onNameChange(id: Long, name: String) {
        val clean = name.replace('\n', ' ').take(SetupRules.MAX_NAME_LENGTH)
        edit { s -> s.copy(players = s.players.map { if (it.id == id) it.copy(name = clean) else it }) }
    }

    override fun onAddPlayer() = edit { s ->
        if (s.canAddPlayer) s.copy(players = s.players + newRow("")) else s
    }

    override fun onRemovePlayer(id: Long) = edit { s ->
        if (s.canRemovePlayer) s.copy(players = s.players.filterNot { it.id == id }) else s
    }

    override fun onMovePlayer(id: Long, offset: Int) = edit { s ->
        val from = s.players.indexOfFirst { it.id == id }
        val to = from + offset
        if (from < 0 || to !in s.players.indices) return@edit s
        val reordered = s.players.toMutableList().apply { add(to, removeAt(from)) }
        s.copy(players = reordered)
    }

    override fun onTricksterCountChange(count: Int) = edit { it.copy(tricksterCount = count) }

    override fun onCategoryToggle(id: String) = edit { s ->
        s.copy(categories = s.categories.map { if (it.id == id) it.copy(selected = !it.selected) else it })
    }

    override fun onSelectAllCategories(selected: Boolean) = edit { s ->
        s.copy(categories = s.categories.map { it.copy(selected = selected) })
    }

    override fun onWhisperEnabledChange(enabled: Boolean) = edit { it.copy(whisperEnabled = enabled) }

    override fun onWhisperLevelChange(level: WhisperLevel) = edit { it.copy(whisperLevel = level) }

    override fun onTellingTimerChange(timer: TellingTimer) = edit { it.copy(tellingTimer = timer) }

    override fun onLastLieChange(enabled: Boolean) = edit { it.copy(lastLieEnabled = enabled) }

    /** Applies a change, keeps the Trickster count legal, and saves the result straight away. */
    private fun edit(transform: (SetupUiState) -> SetupUiState) {
        if (_uiState.value.isLoading) return
        _uiState.update { old ->
            val new = transform(old)
            new.copy(tricksterCount = SetupRules.clampTricksters(new.tricksterCount, new.players.size))
        }
        val snapshot = _uiState.value.toSavedSetup()
        viewModelScope.launch { settings.saveSetup(snapshot) }
    }

    private fun newRow(name: String) = PlayerRow(id = nextPlayerId++, name = name)

    private fun SetupUiState.toSavedSetup() = SavedSetup(
        playerNames = players.map { it.name },
        tricksterCount = tricksterCount,
        excludedCategoryIds = categories.filterNot { it.selected }.map { it.id }.toSet(),
        whisperEnabled = whisperEnabled,
        whisperLevel = whisperLevel,
        tellingTimer = tellingTimer,
        lastLieEnabled = lastLieEnabled,
    )

    companion object {
        /** Tells Compose's viewModel() how to build this ViewModel with its dependencies from AppContainer. */
        val Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as MischiefApp).container
                SetupViewModel(container.settingsRepository, container.wordRepository)
            }
        }
    }
}
