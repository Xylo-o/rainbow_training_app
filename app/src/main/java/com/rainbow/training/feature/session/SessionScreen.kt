package com.rainbow.training.feature.session

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rainbow.training.core.model.Stimulus
import com.rainbow.training.feature.SessionPhase
import com.rainbow.training.feature.monotonicNowMillis
import com.rainbow.training.ui.backgroundColor
import com.rainbow.training.ui.contentColor

/**
 * Pelnoekranowy ekran treningu.
 *
 * Celowo nie reaguje na dotyk - reakcja na bodziec dzieje sie poza aplikacja. Jedyne wyjscie
 * to przycisk / gest "wstecz", ktory przerywa sesje.
 */
@Composable
fun SessionScreen(
    phase: SessionPhase,
    onAbort: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ImmersiveSessionEffect()
    BackHandler(onBack = onAbort)

    when (phase) {
        is SessionPhase.Countdown -> CountdownContent(phase.secondsLeft, modifier)
        is SessionPhase.Running -> RunningContent(phase, modifier)
        else -> Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
    }
}

@Composable
private fun CountdownContent(secondsLeft: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GiantGlyph(
                text = secondsLeft.toString(),
                color = MaterialTheme.colorScheme.onBackground,
                heightFraction = 0.35f,
            )
            Text(
                text = "Przygotuj się",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RunningContent(phase: SessionPhase.Running, modifier: Modifier = Modifier) {
    val stimulus = phase.stimulus
    val background = stimulus.backgroundColor
    val content = stimulus.contentColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        if (stimulus is Stimulus.Digit) {
            GiantGlyph(
                text = stimulus.digit.toString(),
                color = content,
                heightFraction = 0.55f,
            )
        }

        SessionProgressBar(
            startedAtMillis = phase.startedAtMillis,
            totalMillis = phase.totalMillis,
            color = content,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

/** Jak najwiekszy znak, ktory zmiesci sie na ekranie niezaleznie od rozmiaru urzadzenia. */
@Composable
private fun GiantGlyph(
    text: String,
    color: Color,
    heightFraction: Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val shorterSide = minOf(maxWidth, maxHeight)
        val fontSize = with(LocalDensity.current) { (shorterSide * heightFraction).toSp() }
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Cienka kreska u gory pokazujaca, ile sesji zostalo. Odswiezana co klatke, zeby plynnie
 * rosla bez zasypywania modelu zdarzeniami.
 */
@Composable
private fun SessionProgressBar(
    startedAtMillis: Long,
    totalMillis: Long,
    color: Color,
    modifier: Modifier = Modifier,
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(startedAtMillis, totalMillis) {
        while (true) {
            withFrameMillis {
                val elapsed = (monotonicNowMillis() - startedAtMillis).toFloat()
                progress = if (totalMillis <= 0L) 1f else (elapsed / totalMillis).coerceIn(0f, 1f)
            }
        }
    }

    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .fillMaxWidth()
            .height(3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(color.copy(alpha = 0.45f)),
        )
    }
}
