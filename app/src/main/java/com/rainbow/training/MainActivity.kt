package com.rainbow.training

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rainbow.training.core.platform.Haptics
import com.rainbow.training.feature.SessionPhase
import com.rainbow.training.feature.TrainingEvent
import com.rainbow.training.feature.TrainingViewModel
import com.rainbow.training.feature.session.SessionScreen
import com.rainbow.training.feature.setup.SetupScreen
import com.rainbow.training.feature.summary.SummaryScreen
import com.rainbow.training.ui.theme.RainbowTrainingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Motyw aplikacji jest zawsze ciemny, wiec ikony paskow systemowych tez musza byc
        // jasne - domyslne `auto` wybiera je wedlug motywu SYSTEMU i na telefonie ustawionym
        // na jasny znikaja na ciemnym tle.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        val container = (application as RainbowApp).container

        setContent {
            RainbowTrainingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    RainbowTrainingApp(
                        viewModel = viewModel(factory = TrainingViewModel.factory(container)),
                    )
                }
            }
        }
    }
}

/** Korzen aplikacji: wybiera ekran na podstawie fazy sesji i obsluguje zdarzenia jednorazowe. */
@Composable
fun RainbowTrainingApp(viewModel: TrainingViewModel) {
    val phase by viewModel.phase.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                TrainingEvent.SessionFinished -> Haptics.sessionFinished(context)
            }
        }
    }

    when (val current = phase) {
        is SessionPhase.Idle -> {
            val loaded = settings
            if (loaded == null) {
                // Krotka chwila na wczytanie ustawien z dysku - bez migotania spinnera.
                Box(Modifier.fillMaxSize())
            } else {
                SetupScreen(
                    settings = loaded,
                    onSettingsChange = { updated -> viewModel.updateSettings { updated } },
                    onStart = viewModel::startSession,
                )
            }
        }

        is SessionPhase.Countdown,
        is SessionPhase.Running,
        -> SessionScreen(phase = current, onAbort = viewModel::abortSession)

        is SessionPhase.Finished -> SummaryScreen(
            durationMillis = current.durationMillis,
            stimulusCount = current.stimulusCount,
            onRepeat = viewModel::repeatSession,
            onChangeSettings = viewModel::backToSetup,
        )
    }
}
