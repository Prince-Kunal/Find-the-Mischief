package com.prince.findthemischief.ui.components

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/**
 * While [enabled], the window is marked secure: screenshots and screen recordings come out
 * black, and the app's preview in the Recents list is hidden. Cleared again when [enabled]
 * turns false or this leaves the screen.
 */
@Composable
fun SecureWindow(enabled: Boolean) {
    val window = LocalActivity.current?.window
    DisposableEffect(window, enabled) {
        if (enabled) window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            if (enabled) window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

/** Stops the screen from dimming or locking while this is on screen (a game is running). */
@Composable
fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}
