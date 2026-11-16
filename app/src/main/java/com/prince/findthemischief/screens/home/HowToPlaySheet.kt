package com.prince.findthemischief.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.R
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.theme.NearBlack
import com.prince.findthemischief.ui.theme.NotoSansRunic
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.OldGoldDim
import com.prince.findthemischief.ui.theme.Parchment
import com.prince.findthemischief.ui.theme.ParchmentMuted
import kotlinx.coroutines.launch

/** One rule: a rune bullet, a title and a short explanation. */
private data class Rule(val rune: String, @StringRes val title: Int, @StringRes val body: Int)

private val Rules = listOf(
    Rule("ᚠ", R.string.rules_pass_title, R.string.rules_pass_body),
    Rule("ᚢ", R.string.rules_truth_title, R.string.rules_truth_body),
    Rule("ᚦ", R.string.rules_trickster_title, R.string.rules_trickster_body),
    Rule("ᚨ", R.string.rules_telling_title, R.string.rules_telling_body),
    Rule("ᚱ", R.string.rules_judgement_title, R.string.rules_judgement_body),
    Rule("ᚲ", R.string.rules_victory_title, R.string.rules_victory_body),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlaySheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NearBlack,
        contentColor = Parchment,
        dragHandle = { BottomSheetDefaults.DragHandle(color = OldGoldDim) },
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
        ) {
            Text(
                text = stringResource(R.string.home_how_to_play),
                style = MaterialTheme.typography.headlineMedium,
                color = OldGold,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(12.dp))
            RuneDivider()
            Spacer(Modifier.height(8.dp))
            Rules.forEach { RuleItem(it) }
            Spacer(Modifier.height(16.dp))
            MischiefButton(
                text = stringResource(R.string.rules_close),
                onClick = {
                    // Animate the sheet down first, then remove it.
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RuleItem(rule: Rule) {
    Row(Modifier.padding(vertical = 10.dp)) {
        Text(
            text = rule.rune,
            fontFamily = NotoSansRunic,
            style = MaterialTheme.typography.titleLarge,
            color = OldGold,
            modifier = Modifier
                .width(32.dp)
                .clearAndSetSemantics { },
        )
        Column {
            Text(
                text = stringResource(rule.title),
                style = MaterialTheme.typography.titleMedium,
                color = OldGold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(rule.body),
                style = MaterialTheme.typography.bodyMedium,
                color = ParchmentMuted,
            )
        }
    }
}
