package com.rainbow.training.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.rainbow.training.R

/**
 * Space Grotesk - grotesk z charakterem (katowe sciecia, nietypowe cyfry). Niesie caly
 * interfejs: naglowki, etykiety, przyciski.
 */
val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

/**
 * Outfit ExtraBold tylko na cyfry-bodzce.
 *
 * Cyfra na pelnym ekranie jest elementem graficznym, nie tekstem, wiec moze pochodzic z innego
 * kroju niz reszta. Outfit ma niemal idealnie kolowe ksztalty i wage 800, ktorej Space Grotesk
 * w ogole nie oferuje - a przy tym rozmiarze grubosc jest tym, co daje czytelnosc.
 */
val DigitFamily = FontFamily(
    Font(R.font.outfit_extra_bold, FontWeight.ExtraBold),
)

private val default = Typography()

val RainbowTypography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = SpaceGroteskFamily),
    displayMedium = default.displayMedium.copy(fontFamily = SpaceGroteskFamily),
    displaySmall = default.displaySmall.copy(fontFamily = SpaceGroteskFamily),
    headlineLarge = default.headlineLarge.copy(fontFamily = SpaceGroteskFamily),
    headlineMedium = default.headlineMedium.copy(fontFamily = SpaceGroteskFamily),
    headlineSmall = default.headlineSmall.copy(fontFamily = SpaceGroteskFamily),
    titleLarge = default.titleLarge.copy(fontFamily = SpaceGroteskFamily),
    titleMedium = default.titleMedium.copy(fontFamily = SpaceGroteskFamily),
    titleSmall = default.titleSmall.copy(fontFamily = SpaceGroteskFamily),
    bodyLarge = default.bodyLarge.copy(fontFamily = SpaceGroteskFamily),
    bodyMedium = default.bodyMedium.copy(fontFamily = SpaceGroteskFamily),
    bodySmall = default.bodySmall.copy(fontFamily = SpaceGroteskFamily),
    labelLarge = default.labelLarge.copy(fontFamily = SpaceGroteskFamily),
    labelMedium = default.labelMedium.copy(fontFamily = SpaceGroteskFamily),
    labelSmall = default.labelSmall.copy(fontFamily = SpaceGroteskFamily),
)
