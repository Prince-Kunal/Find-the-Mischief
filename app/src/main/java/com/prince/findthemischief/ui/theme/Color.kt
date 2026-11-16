package com.prince.findthemischief.ui.theme

import androidx.compose.ui.graphics.Color

// Core palette (see findTheMischief_PLAN.md §2.2). Use these names, not raw hex values, in UI code.
val NearBlack = Color(0xFF0B0F0D)       // background
val DeepEmerald = Color(0xFF0F3D2E)     // surface
val MischiefGreen = Color(0xFF2E8B57)   // primary
val OldGold = Color(0xFFC9A227)         // accent
val Parchment = Color(0xFFE8DFC8)       // text on dark
val BloodRed = Color(0xFF8B1E1E)        // error

// Supporting shades derived from the core palette.
val DeepEmeraldLight = Color(0xFF15503D)  // raised surfaces, pressed states
val ParchmentMuted = Color(0xFFB8AF98)    // secondary text on dark
val OldGoldDim = Color(0xFF7A6420)        // subtle gold lines and outlines
val GlowGreen = Color(0xFF3FBF78)         // smoke / glow effects (used at low alpha)
// Blood red is too dark to read as text on near-black, so error *text* and outlines use this lighter red.
val BloodRedLight = Color(0xFFE57373)
