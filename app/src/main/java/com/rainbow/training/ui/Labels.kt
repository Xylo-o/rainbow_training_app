package com.rainbow.training.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rainbow.training.R
import com.rainbow.training.core.model.SettingsError
import com.rainbow.training.core.model.StimulusColor
import com.rainbow.training.core.model.StimulusMode
import com.rainbow.training.core.model.TrainingSettings

/**
 * Tlumaczenie pojec z warstwy `core/` na teksty widoczne dla uzytkownika.
 *
 * Model celowo nie zna zasobow Androida ani jezyka interfejsu - przechowuje identyfikatory
 * i przyczyny bledow, a caly tekst mieszka w `res/values/strings.xml`. Dodanie kolejnego
 * jezyka to zalozenie katalogu `values-xx/` i nic wiecej.
 */

@Composable
fun StimulusColor.label(): String = stringResource(
    when (this) {
        StimulusColor.RED -> R.string.color_red
        StimulusColor.ORANGE -> R.string.color_orange
        StimulusColor.YELLOW -> R.string.color_yellow
        StimulusColor.GREEN -> R.string.color_green
        StimulusColor.BLUE -> R.string.color_blue
        StimulusColor.PURPLE -> R.string.color_purple
        StimulusColor.WHITE -> R.string.color_white
        StimulusColor.BLACK -> R.string.color_black
    },
)

@Composable
fun StimulusMode.label(): String = stringResource(
    when (this) {
        StimulusMode.COLORS -> R.string.mode_colors
        StimulusMode.DIGITS -> R.string.mode_digits
        StimulusMode.MIXED -> R.string.mode_mixed
    },
)

@Composable
fun StimulusMode.description(): String = stringResource(
    when (this) {
        StimulusMode.COLORS -> R.string.mode_colors_description
        StimulusMode.DIGITS -> R.string.mode_digits_description
        StimulusMode.MIXED -> R.string.mode_mixed_description
    },
)

@Composable
fun SettingsError.message(): String = when (this) {
    SettingsError.TOO_FEW_COLORS ->
        stringResource(R.string.error_too_few_colors, TrainingSettings.MIN_ENABLED_COLORS)

    SettingsError.TOO_FEW_DIGITS ->
        stringResource(R.string.error_too_few_digits, TrainingSettings.MIN_ENABLED_DIGITS)

    SettingsError.INTERVAL_TOO_SHORT ->
        stringResource(
            R.string.error_interval_too_short,
            formatSeconds(TrainingSettings.MIN_INTERVAL_MILLIS),
        )

    SettingsError.INTERVAL_TOO_LONG ->
        stringResource(
            R.string.error_interval_too_long,
            formatSeconds(TrainingSettings.MAX_INTERVAL_MILLIS),
        )

    SettingsError.INTERVAL_REVERSED -> stringResource(R.string.error_interval_reversed)

    SettingsError.SESSION_TOO_SHORT ->
        stringResource(
            R.string.error_session_too_short,
            formatDuration(TrainingSettings.MIN_SESSION_DURATION_MILLIS),
        )

    SettingsError.SESSION_TOO_LONG ->
        stringResource(
            R.string.error_session_too_long,
            formatDuration(TrainingSettings.MAX_SESSION_DURATION_MILLIS),
        )
}
