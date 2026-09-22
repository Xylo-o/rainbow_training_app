package com.rainbow.training.feature.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rainbow.training.monetization.AdBannerSlot
import com.rainbow.training.ui.components.SectionCard
import com.rainbow.training.ui.formatDuration
import com.rainbow.training.ui.formatSeconds

/** Ekran po zakonczonej sesji: krotkie podsumowanie i dwa wyjscia. */
@Composable
fun SummaryScreen(
    durationMillis: Long,
    stimulusCount: Int,
    onRepeat: () -> Unit,
    onChangeSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Sesja zakończona",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        SectionCard(
            title = "Podsumowanie",
            modifier = Modifier.padding(top = 24.dp),
        ) {
            SummaryRow(label = "Czas trwania", value = formatDuration(durationMillis))
            SummaryRow(label = "Pokazanych bodźców", value = stimulusCount.toString())
            if (stimulusCount > 0) {
                SummaryRow(
                    label = "Średni odstęp",
                    value = formatSeconds(durationMillis / stimulusCount),
                )
            }
        }

        Button(
            onClick = onRepeat,
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(text = "Powtórz", style = MaterialTheme.typography.titleMedium)
        }

        OutlinedButton(
            onClick = onChangeSettings,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(text = "Zmień ustawienia")
        }

        AdBannerSlot(modifier = Modifier.padding(top = 16.dp))
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
            fontWeight = FontWeight.SemiBold,
        )
    }
}
