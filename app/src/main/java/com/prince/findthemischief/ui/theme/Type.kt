package com.prince.findthemischief.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prince.findthemischief.R

// Cinzel and Inter are bundled as variable fonts; each Font entry pins one weight of the file.
// Variable-font settings are still marked experimental in Compose, hence the opt-in.
@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: FontWeight) = Font(
    resId = resId,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

/** Titles and big words (the Truth, headings). */
val Cinzel = FontFamily(
    variableFont(R.font.cinzel_variable, FontWeight.Normal),
    variableFont(R.font.cinzel_variable, FontWeight.SemiBold),
    variableFont(R.font.cinzel_variable, FontWeight.Bold),
)

/** Body text, labels, buttons' small print. */
val Inter = FontFamily(
    variableFont(R.font.inter_variable, FontWeight.Normal),
    variableFont(R.font.inter_variable, FontWeight.Medium),
    variableFont(R.font.inter_variable, FontWeight.SemiBold),
)

/** Elder Futhark runes for decorative borders and dividers. */
val NotoSansRunic = FontFamily(Font(R.font.noto_sans_runic))

private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Cinzel, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = Cinzel, fontWeight = FontWeight.Bold),
    displaySmall = base.displaySmall.copy(fontFamily = Cinzel, fontWeight = FontWeight.Bold),
    headlineLarge = base.headlineLarge.copy(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Cinzel, fontWeight = FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Inter),
    bodyMedium = base.bodyMedium.copy(fontFamily = Inter),
    bodySmall = base.bodySmall.copy(fontFamily = Inter),
    labelLarge = base.labelLarge.copy(fontFamily = Inter, fontWeight = FontWeight.SemiBold),
    labelMedium = base.labelMedium.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = Inter, fontWeight = FontWeight.Medium),
)

/** Rune text style for decorative rows. Purely ornamental, so hide it from screen readers at the call site. */
val RuneTextStyle = TextStyle(
    fontFamily = NotoSansRunic,
    fontSize = 16.sp,
    letterSpacing = 4.sp,
)
