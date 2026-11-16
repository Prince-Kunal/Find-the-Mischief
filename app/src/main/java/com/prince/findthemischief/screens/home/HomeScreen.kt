package com.prince.findthemischief.screens.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.R
import com.prince.findthemischief.ui.components.GoldFrameCard
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.MischiefButtonStyle
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.components.SerpentEmblem
import com.prince.findthemischief.ui.theme.FindTheMischiefTheme
import com.prince.findthemischief.ui.theme.OldGold

/** Phase 0 placeholder: shows off the theme and components. Replaced by the real Home in Phase 1. */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val comingSoon = stringResource(R.string.placeholder_next_phase)
    val showComingSoon = { Toast.makeText(context, comingSoon, Toast.LENGTH_SHORT).show() }

    MischiefBackground(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SerpentEmblem(size = 168.dp)
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                color = OldGold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            RuneDivider()
            Spacer(Modifier.height(24.dp))
            GoldFrameCard(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.placeholder_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.placeholder_card_body),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(32.dp))
            MischiefButton(
                text = stringResource(R.string.home_play),
                onClick = showComingSoon,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            MischiefButton(
                text = stringResource(R.string.home_how_to_play),
                onClick = showComingSoon,
                modifier = Modifier.fillMaxWidth(),
                style = MischiefButtonStyle.Secondary,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    FindTheMischiefTheme { HomeScreen() }
}
