package com.rainbow.training.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.rainbow.training.R

/**
 * Inter - krój o wyraznie zarysowanych, jednoznacznych znakach. Wazne akurat tutaj, bo cyfra
 * musi byc rozpoznana w ulamku sekundy i nie moze sie mylic z zadna inna.
 *
 * Fonty sa wbudowane w aplikacje (res/font), a nie pobierane przez Google Fonts Provider -
 * dzieki temu aplikacja dziala w pelni offline i nie wymaga uprawnienia do sieci.
 */
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_semi_bold, FontWeight.SemiBold),
    Font(R.font.inter_extra_bold, FontWeight.ExtraBold),
)

private val default = Typography()

val RainbowTypography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = InterFontFamily),
    displayMedium = default.displayMedium.copy(fontFamily = InterFontFamily),
    displaySmall = default.displaySmall.copy(fontFamily = InterFontFamily),
    headlineLarge = default.headlineLarge.copy(fontFamily = InterFontFamily),
    headlineMedium = default.headlineMedium.copy(fontFamily = InterFontFamily),
    headlineSmall = default.headlineSmall.copy(fontFamily = InterFontFamily),
    titleLarge = default.titleLarge.copy(fontFamily = InterFontFamily),
    titleMedium = default.titleMedium.copy(fontFamily = InterFontFamily),
    titleSmall = default.titleSmall.copy(fontFamily = InterFontFamily),
    bodyLarge = default.bodyLarge.copy(fontFamily = InterFontFamily),
    bodyMedium = default.bodyMedium.copy(fontFamily = InterFontFamily),
    bodySmall = default.bodySmall.copy(fontFamily = InterFontFamily),
    labelLarge = default.labelLarge.copy(fontFamily = InterFontFamily),
    labelMedium = default.labelMedium.copy(fontFamily = InterFontFamily),
    labelSmall = default.labelSmall.copy(fontFamily = InterFontFamily),
)
