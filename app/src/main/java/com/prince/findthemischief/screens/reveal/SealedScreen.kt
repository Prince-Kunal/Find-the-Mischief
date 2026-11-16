package com.prince.findthemischief.screens.reveal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.R
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.MischiefButton
import com.prince.findthemischief.ui.components.RuneDivider
import com.prince.findthemischief.ui.components.SerpentEmblem
import com.prince.findthemischief.ui.theme.OldGold
import com.prince.findthemischief.ui.theme.ParchmentMuted

/** "All fates are sealed." Everyone has seen their card; the Telling is next. */
@Composable
fun SealedScreen(onBeginTelling: () -> Unit) {
    MischiefBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(Modifier.height(1.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SerpentEmblem(size = 160.dp)
                Spacer(Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.sealed_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = OldGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(16.dp))
                RuneDivider()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.sealed_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = ParchmentMuted,
                    textAlign = TextAlign.Center,
                )
            }
            MischiefButton(
                text = stringResource(R.string.sealed_begin_telling),
                onClick = onBeginTelling,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
