package com.prince.findthemischief.screens.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import com.prince.findthemischief.R
import com.prince.findthemischief.game.domain.TellingTimer
import com.prince.findthemischief.game.domain.WhisperLevel
import com.prince.findthemischief.ui.components.SectionTitle
import com.prince.findthemischief.ui.components.mischiefChipBorder
import com.prince.findthemischief.ui.components.mischiefChipColors
import com.prince.findthemischief.ui.components.mischiefSegmentedColors
import com.prince.findthemischief.ui.components.mischiefSwitchColors
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.Parchment
import com.prince.findthemischief.ui.theme.ParchmentMuted

@Composable
fun TricksterSection(count: Int, max: Int, playerCount: Int, onChange: (Int) -> Unit) {
    val countLabel = stringResource(R.string.setup_tricksters_count, count)
    Row(verticalAlignment = Alignment.CenterVertically) {
        SectionTitle(
            title = stringResource(R.string.setup_tricksters_title),
            subtitle = stringResource(R.string.setup_tricksters_limit, max, playerCount),
            modifier = Modifier.weight(1f),
        )
        StepButton("−", stringResource(R.string.setup_tricksters_fewer), enabled = count > 1) { onChange(count - 1) }
        Text(
            text = "$count",
            style = MaterialTheme.typography.headlineMedium,
            color = Parchment,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(min = 48.dp)
                .semantics { contentDescription = countLabel },
        )
        StepButton("+", stringResource(R.string.setup_tricksters_more), enabled = count < max) { onChange(count + 1) }
    }
}

@Composable
private fun StepButton(symbol: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    OutlinedIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(48.dp)
            .semantics { contentDescription = description },
        border = BorderStroke(1.dp, if (enabled) OldGold else OldGoldDim),
    ) {
        Text(symbol, style = MaterialTheme.typography.titleLarge, color = if (enabled) OldGold else ParchmentMuted)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySection(categories: List<CategoryOption>, actions: SetupActions) {
    val allSelected = categories.all { it.selected }
    Row(verticalAlignment = Alignment.CenterVertically) {
        SectionTitle(stringResource(R.string.setup_categories_title), Modifier.weight(1f))
        TextButton(onClick = { actions.onSelectAllCategories(!allSelected) }) {
            Text(
                stringResource(if (allSelected) R.string.setup_categories_none else R.string.setup_categories_all),
                color = OldGold,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { category ->
            FilterChip(
                selected = category.selected,
                onClick = { actions.onCategoryToggle(category.id) },
                label = { Text("${category.emoji}  ${category.name}", style = MaterialTheme.typography.labelLarge) },
                leadingIcon = if (category.selected) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else {
                    null
                },
                colors = mischiefChipColors(),
                border = mischiefChipBorder(category.selected),
            )
        }
    }
}

@Composable
fun WhisperSection(enabled: Boolean, level: WhisperLevel, actions: SetupActions) {
    SwitchRow(
        title = stringResource(R.string.setup_whisper_title),
        subtitle = stringResource(R.string.setup_whisper_subtitle),
        checked = enabled,
        onCheckedChange = actions::onWhisperEnabledChange,
    )
    val labels = listOf(R.string.setup_whisper_easy, R.string.setup_whisper_medium, R.string.setup_whisper_hard)
    ChoiceRow(
        options = WhisperLevel.entries,
        selected = level,
        label = { stringResource(labels[it.ordinal]) },
        enabled = enabled,
        onSelect = actions::onWhisperLevelChange,
    )
    val description = when (level) {
        WhisperLevel.Easy -> R.string.setup_whisper_easy_desc
        WhisperLevel.Medium -> R.string.setup_whisper_medium_desc
        WhisperLevel.Hard -> R.string.setup_whisper_hard_desc
    }
    Text(
        text = stringResource(if (enabled) description else R.string.setup_whisper_off_desc),
        style = MaterialTheme.typography.bodyMedium,
        color = ParchmentMuted,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
fun TimerSection(timer: TellingTimer, onSelect: (TellingTimer) -> Unit) {
    SectionTitle(stringResource(R.string.setup_timer_title), subtitle = stringResource(R.string.setup_timer_subtitle))
    ChoiceRow(
        options = TellingTimer.entries,
        selected = timer,
        label = {
            if (it == TellingTimer.Off) stringResource(R.string.setup_timer_off)
            else stringResource(R.string.setup_timer_seconds, it.seconds)
        },
        onSelect = onSelect,
    )
}

@Composable
fun LastLieSection(enabled: Boolean, onChange: (Boolean) -> Unit) {
    SwitchRow(
        title = stringResource(R.string.setup_last_lie_title),
        subtitle = stringResource(R.string.setup_last_lie_subtitle),
        checked = enabled,
        onCheckedChange = onChange,
    )
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val state = stringResource(if (checked) R.string.state_on else R.string.state_off)
    Row(verticalAlignment = Alignment.CenterVertically) {
        SectionTitle(title, Modifier.weight(1f), subtitle = subtitle)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = mischiefSwitchColors(),
            modifier = Modifier
                .padding(start = 12.dp)
                .semantics {
                    contentDescription = title
                    stateDescription = state
                },
        )
    }
}

/** A row of joined buttons where exactly one is chosen (Material "segmented buttons"). */
@Composable
private fun <T> ChoiceRow(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    enabled: Boolean = true,
) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                enabled = enabled,
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                colors = mischiefSegmentedColors(),
                icon = {},
                label = { Text(label(option), style = MaterialTheme.typography.labelLarge) },
            )
        }
    }
}

/** Plain-language messages for whatever stops the game from starting. */
@Composable
fun ProblemList(messages: List<String>, modifier: Modifier = Modifier) {
    Column(modifier) {
        messages.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
    }
}
