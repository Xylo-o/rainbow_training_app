package com.rainbow.training.ui

import androidx.compose.ui.graphics.Color
import com.rainbow.training.core.model.Stimulus
import com.rainbow.training.core.model.StimulusColor

/**
 * Mostek miedzy modelem (kolory jako ARGB w [Long]) a Compose.
 * Model nie zalezy od UI, wiec konwersja mieszka tutaj.
 */
val StimulusColor.composeColor: Color get() = Color(argb)

val Stimulus.backgroundColor: Color get() = Color(backgroundArgb)

val Stimulus.contentColor: Color get() = Color(contentArgb)
