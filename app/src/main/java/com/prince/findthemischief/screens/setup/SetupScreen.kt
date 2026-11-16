package com.prince.findthemischief.screens.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prince.findthemischief.R
import com.prince.findthemischief.ui.components.MischiefBackground
import com.prince.findthemischief.ui.components.ScreenHeader
import com.prince.findthemischief.ui.theme.ParchmentMuted

/** Bare shell so navigation works. Filled in by Phase 3. */
@Composable
fun SetupScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MischiefBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
        ) {
            ScreenHeader(title = stringResource(R.string.setup_title), onBack = onBack)
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.setup_not_built),
                style = MaterialTheme.typography.bodyLarge,
                color = ParchmentMuted,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}
