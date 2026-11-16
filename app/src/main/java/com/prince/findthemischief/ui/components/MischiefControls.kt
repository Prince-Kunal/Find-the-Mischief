package com.prince.findthemischief.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.ui.theme.BloodRedLight
import com.prince.findthemischief.ui.theme.DeepEmerald
import com.prince.findthemischief.ui.theme.MischiefGreen
import com.prince.findthemischief.ui.theme.NearBlack
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.Parchment
import com.prince.findthemischief.ui.theme.ParchmentMuted

// Mischief-themed colours for standard Material controls, so every screen's switches,
// chips and text fields look the same.

/** A gold Cinzel section heading with an optional muted line underneath. */
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier.padding(top = 24.dp, bottom = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = OldGold,
            modifier = Modifier.semantics { heading() },
        )
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = ParchmentMuted)
        }
    }
}

@Composable
fun mischiefSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = Parchment,
    checkedTrackColor = MischiefGreen,
    checkedBorderColor = OldGold,
    uncheckedThumbColor = ParchmentMuted,
    uncheckedTrackColor = NearBlack,
    uncheckedBorderColor = OldGoldDim,
)

@Composable
fun mischiefSegmentedColors() = SegmentedButtonDefaults.colors(
    activeContainerColor = MischiefGreen,
    activeContentColor = Parchment,
    activeBorderColor = OldGold,
    inactiveContainerColor = Color.Transparent,
    inactiveContentColor = ParchmentMuted,
    inactiveBorderColor = OldGoldDim,
    disabledActiveContainerColor = DeepEmerald,
    disabledActiveContentColor = ParchmentMuted.copy(alpha = 0.6f),
    disabledActiveBorderColor = OldGoldDim,
    disabledInactiveContainerColor = Color.Transparent,
    disabledInactiveContentColor = ParchmentMuted.copy(alpha = 0.4f),
    disabledInactiveBorderColor = OldGoldDim.copy(alpha = 0.5f),
)

@Composable
fun mischiefChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color.Transparent,
    labelColor = ParchmentMuted,
    selectedContainerColor = MischiefGreen,
    selectedLabelColor = Parchment,
    selectedLeadingIconColor = Parchment,
)

@Composable
fun mischiefChipBorder(selected: Boolean) = FilterChipDefaults.filterChipBorder(
    enabled = true,
    selected = selected,
    borderColor = OldGoldDim,
    selectedBorderColor = OldGold,
    borderWidth = 1.dp,
    selectedBorderWidth = 1.dp,
)

@Composable
fun mischiefTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Parchment,
    unfocusedTextColor = Parchment,
    focusedBorderColor = OldGold,
    unfocusedBorderColor = OldGoldDim,
    cursorColor = OldGold,
    focusedContainerColor = DeepEmerald.copy(alpha = 0.5f),
    unfocusedContainerColor = Color.Transparent,
    focusedPlaceholderColor = ParchmentMuted.copy(alpha = 0.6f),
    unfocusedPlaceholderColor = ParchmentMuted.copy(alpha = 0.6f),
    errorBorderColor = BloodRedLight,
    errorCursorColor = BloodRedLight,
    errorTextColor = Parchment,
    errorContainerColor = Color.Transparent,
)
