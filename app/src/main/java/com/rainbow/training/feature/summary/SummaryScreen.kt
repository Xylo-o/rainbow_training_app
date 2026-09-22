package com.rainbow.training.feature.summary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rainbow.training.R
import com.rainbow.training.monetization.LocalAdHost
import com.rainbow.training.ui.components.SectionCard
import com.rainbow.training.ui.formatDuration
import com.rainbow.training.ui.formatSeconds

/**
 * Ekran po zakonczonej sesji: krotkie podsumowanie i dwa wyjscia.
 *
 * Tresc jest przewijalna i ma minimalna wysokosc rowna ekranowi. Dzieki temu w pionie jest
 * wysrodkowana, a w poziomie - gdzie po prostu sie nie miesci - da sie do niej dojechac
 * zamiast tracic dolny przycisk poza ekranem.
 */
@Composable
fun SummaryScreen(
    durationMillis: Long,
    stimulusCount: Int,
    onRepeat: () -> Unit,
    onChangeSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Sesja sie skonczyla, wiec "wstecz" ma wrocic do ustawien, a nie wyjsc z aplikacji.
    BackHandler(onBack = onChangeSettings)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        val viewportHeight = maxHeight

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = viewportHeight)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.summary_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                SectionCard(
                    title = stringResource(R.string.summary_heading),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .widthIn(max = 440.dp),
                ) {
                    SummaryRow(
                        label = stringResource(R.string.summary_duration),
                        value = formatDuration(durationMillis),
                    )
                    SummaryRow(
                        label = stringResource(R.string.summary_count),
                        value = stimulusCount.toString(),
                    )
                    if (stimulusCount > 0) {
                        SummaryRow(
                            label = stringResource(R.string.summary_average),
                            value = formatSeconds(durationMillis / stimulusCount),
                        )
                    }
                }

                Button(
                    onClick = onRepeat,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .widthIn(max = 440.dp)
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                ) {
                    Text(
                        text = stringResource(R.string.summary_repeat),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                OutlinedButton(
                    onClick = onChangeSettings,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .widthIn(max = 440.dp)
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                ) {
                    Text(text = stringResource(R.string.summary_change_settings))
                }

                LocalAdHost.current.Banner(Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
