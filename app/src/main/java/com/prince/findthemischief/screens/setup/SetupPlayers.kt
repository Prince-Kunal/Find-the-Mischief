package com.prince.findthemischief.screens.setup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.prince.findthemischief.R
import com.prince.findthemischief.ui.components.mischiefTextFieldColors
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.ParchmentMuted

/** One player: number, editable name, move up / move down, remove. */
@Composable
fun PlayerRowItem(
    row: PlayerRow,
    index: Int,
    count: Int,
    isError: Boolean,
    canRemove: Boolean,
    actions: SetupActions,
    modifier: Modifier = Modifier,
) {
    val fallbackName = stringResource(R.string.setup_player_placeholder, index + 1)
    val spokenName = row.name.ifBlank { fallbackName }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = row.name,
            onValueChange = { actions.onNameChange(row.id, it) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            isError = isError,
            placeholder = { Text(fallbackName) },
            leadingIcon = {
                Text("${index + 1}", style = MaterialTheme.typography.titleSmall, color = OldGold)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = if (index < count - 1) ImeAction.Next else ImeAction.Done,
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = MaterialTheme.shapes.small,
            colors = mischiefTextFieldColors(),
        )
        RowIcon(
            icon = Icons.Filled.KeyboardArrowUp,
            description = stringResource(R.string.setup_move_up, spokenName),
            enabled = index > 0,
            onClick = { actions.onMovePlayer(row.id, -1) },
        )
        RowIcon(
            icon = Icons.Filled.KeyboardArrowDown,
            description = stringResource(R.string.setup_move_down, spokenName),
            enabled = index < count - 1,
            onClick = { actions.onMovePlayer(row.id, +1) },
        )
        RowIcon(
            icon = Icons.Filled.Close,
            description = stringResource(R.string.setup_remove_player, spokenName),
            enabled = canRemove,
            onClick = { actions.onRemovePlayer(row.id) },
        )
    }
}

@Composable
private fun RowIcon(icon: ImageVector, description: String, enabled: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (enabled) OldGold else ParchmentMuted.copy(alpha = 0.3f),
        )
    }
}

@Composable
fun AddPlayerButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .heightIn(min = 48.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, OldGoldDim),
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = OldGold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(if (enabled) R.string.setup_add_player else R.string.setup_players_full),
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) OldGold else ParchmentMuted,
        )
    }
}
