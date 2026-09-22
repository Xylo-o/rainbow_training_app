@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.rainbow.training.feature.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rainbow.training.core.model.StimulusColor
import com.rainbow.training.core.model.StimulusMode
import com.rainbow.training.core.model.TrainingSettings
import com.rainbow.training.monetization.AdBannerSlot
import com.rainbow.training.ui.components.ColorToggle
import com.rainbow.training.ui.components.SectionCard
import com.rainbow.training.ui.formatDuration
import com.rainbow.training.ui.formatSeconds
import kotlin.math.roundToLong

/** Ekran konfiguracji sesji - wszystko, co trzeba ustawic przed wcisnieciem START. */
@Composable
fun SetupScreen(
    settings: TrainingSettings,
    onSettingsChange: (TrainingSettings) -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val error = settings.validationError

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (error != null) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    Button(
                        onClick = onStart,
                        enabled = error == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) {
                        Text(text = "START", style = MaterialTheme.typography.titleMedium)
                    }
                    AdBannerSlot()
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Trening obserwacji",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Aplikacja pokazuje bodźce w losowych odstępach. " +
                        "Reaguj na nie po swojemu — ekranu nie trzeba dotykać.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            ModeSection(
                mode = settings.mode,
                onModeChange = { onSettingsChange(settings.copy(mode = it)) },
            )

            ColorsSection(
                enabled = settings.enabledColors,
                onToggle = { color ->
                    val updated = settings.enabledColors.toMutableSet().apply {
                        if (!add(color)) remove(color)
                    }
                    onSettingsChange(settings.copy(enabledColors = updated))
                },
            )

            if (settings.mode.usesDigits) {
                DigitsSection(
                    enabled = settings.enabledDigits,
                    onToggle = { digit ->
                        val updated = settings.enabledDigits.toMutableSet().apply {
                            if (!add(digit)) remove(digit)
                        }
                        onSettingsChange(settings.copy(enabledDigits = updated))
                    },
                )
            }

            IntervalSection(
                minMillis = settings.minIntervalMillis,
                maxMillis = settings.maxIntervalMillis,
                onChange = { min, max ->
                    onSettingsChange(
                        settings.copy(minIntervalMillis = min, maxIntervalMillis = max),
                    )
                },
            )

            DurationSection(
                durationMillis = settings.sessionDurationMillis,
                onChange = { onSettingsChange(settings.copy(sessionDurationMillis = it)) },
            )
        }
    }
}

@Composable
private fun ModeSection(mode: StimulusMode, onModeChange: (StimulusMode) -> Unit) {
    SectionCard(title = "Tryb") {
        val modes = StimulusMode.entries
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            modes.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = item == mode,
                    onClick = { onModeChange(item) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                    label = { Text(item.displayName) },
                )
            }
        }
        Text(
            text = mode.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ColorsSection(
    enabled: Set<StimulusColor>,
    onToggle: (StimulusColor) -> Unit,
) {
    SectionCard(
        title = "Kolory",
        subtitle = "Bodźce losowane są tylko z zaznaczonych barw (minimum " +
            "${TrainingSettings.MIN_ENABLED_COLORS}).",
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StimulusColor.ALL.forEach { color ->
                ColorToggle(
                    color = color,
                    selected = color in enabled,
                    onToggle = { onToggle(color) },
                )
            }
        }
    }
}

@Composable
private fun DigitsSection(enabled: Set<Int>, onToggle: (Int) -> Unit) {
    SectionCard(
        title = "Cyfry",
        subtitle = "Cyfra pojawia się na kolorowym tle, w maksymalnie kontrastowym kolorze.",
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            (0..9).forEach { digit ->
                FilterChip(
                    selected = digit in enabled,
                    onClick = { onToggle(digit) },
                    label = { Text(digit.toString()) },
                )
            }
        }
    }
}

@Composable
private fun IntervalSection(
    minMillis: Long,
    maxMillis: Long,
    onChange: (Long, Long) -> Unit,
) {
    val lowerBound = TrainingSettings.MIN_INTERVAL_MILLIS / 1000f
    val upperBound = TrainingSettings.MAX_INTERVAL_MILLIS / 1000f
    // Suwak w krokach co 0,1 s; `steps` liczy punkty POMIEDZY krancami zakresu.
    val steps = ((upperBound - lowerBound) * 10f).roundToLong().toInt() - 1

    SectionCard(
        title = "Odstęp między bodźcami",
        subtitle = "Każda zmiana czeka losowo długo — gdzieś w tym zakresie.",
    ) {
        Text(
            text = "od ${formatSeconds(minMillis)} do ${formatSeconds(maxMillis)}",
            style = MaterialTheme.typography.titleLarge,
        )
        RangeSlider(
            value = (minMillis / 1000f)..(maxMillis / 1000f),
            onValueChange = { range ->
                onChange(
                    (range.start * 1000f).roundToLong(),
                    (range.endInclusive * 1000f).roundToLong(),
                )
            },
            valueRange = lowerBound..upperBound,
            steps = steps,
        )
    }
}

@Composable
private fun DurationSection(durationMillis: Long, onChange: (Long) -> Unit) {
    SectionCard(
        title = "Długość sesji",
        subtitle = "Wybierz gotową długość albo wpisz własną.",
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TrainingSettings.SESSION_PRESETS_MILLIS.forEach { preset ->
                FilterChip(
                    selected = preset == durationMillis,
                    onClick = { onChange(preset) },
                    label = { Text(formatDuration(preset)) },
                )
            }
        }
        CustomDurationFields(durationMillis = durationMillis, onChange = onChange)
    }
}

/**
 * Dwa pola: minuty i sekundy. Tekst jest stanem lokalnym, zeby dalo sie kasowac cyfry bez
 * "przeskakiwania" wartosci; z modelem synchronizuje sie dopiero po zmianie z zewnatrz
 * (np. po klikniecu gotowej dlugosci).
 */
@Composable
private fun CustomDurationFields(durationMillis: Long, onChange: (Long) -> Unit) {
    var minutesText by rememberSaveable { mutableStateOf((durationMillis / 60_000).toString()) }
    var secondsText by rememberSaveable {
        mutableStateOf(((durationMillis % 60_000) / 1000).toString())
    }
    var lastEmitted by rememberSaveable { mutableLongStateOf(durationMillis) }

    LaunchedEffect(durationMillis) {
        if (durationMillis != lastEmitted) {
            minutesText = (durationMillis / 60_000).toString()
            secondsText = ((durationMillis % 60_000) / 1000).toString()
            lastEmitted = durationMillis
        }
    }

    fun publish(minutes: String, seconds: String) {
        val total = (minutes.toLongOrNull() ?: 0L) * 60_000L + (seconds.toLongOrNull() ?: 0L) * 1000L
        lastEmitted = total
        onChange(total)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = minutesText,
            onValueChange = { input ->
                minutesText = input.filter(Char::isDigit).take(3)
                publish(minutesText, secondsText)
            },
            label = { Text("min") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(110.dp),
        )
        OutlinedTextField(
            value = secondsText,
            onValueChange = { input ->
                secondsText = input.filter(Char::isDigit).take(2)
                publish(minutesText, secondsText)
            },
            label = { Text("s") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(110.dp),
        )
        Text(
            text = "= ${formatDuration(durationMillis)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
