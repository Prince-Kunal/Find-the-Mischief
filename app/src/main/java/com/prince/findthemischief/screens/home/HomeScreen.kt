package com.prince.findthemischief.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.R
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.MischiefButtonStyle
import com.prince.findthemischief.ui.components.RuneBorder
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.components.SerpentEmblem
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.ParchmentMuted

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Whether the "How to play" sheet is open. Saveable so it survives screen rotation.
    var showRules by rememberSaveable { mutableStateOf(false) }

    MischiefBackground(modifier) {
        RuneBorder(Modifier.fillMaxSize().safeDrawingPadding()) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                // Scrolls only on very short screens; otherwise the two groups spread out evenly.
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    HomeTitle()
                    HomeActions(
                        onPlay = onPlay,
                        onHowToPlay = { showRules = true },
                        onSettings = onSettings,
                    )
                }
            }
        }
    }

    if (showRules) {
        HowToPlaySheet(onDismiss = { showRules = false })
    }
}

@Composable
private fun HomeTitle() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SerpentEmblem(size = 176.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = OldGold,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.home_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = ParchmentMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        RuneDivider(Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun HomeActions(
    onPlay: () -> Unit,
    onHowToPlay: () -> Unit,
    onSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MischiefButton(
            text = stringResource(R.string.home_play),
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(),
        )
        MischiefButton(
            text = stringResource(R.string.home_how_to_play),
            onClick = onHowToPlay,
            modifier = Modifier.fillMaxWidth(),
            style = MischiefButtonStyle.Secondary,
        )
        MischiefButton(
            text = stringResource(R.string.home_settings),
            onClick = onSettings,
            modifier = Modifier.fillMaxWidth(),
            style = MischiefButtonStyle.Secondary,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    FindTheMischiefTheme { HomeScreen(onPlay = {}, onSettings = {}) }
}
