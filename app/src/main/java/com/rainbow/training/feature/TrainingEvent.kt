package com.rainbow.training.feature

/** Jednorazowe zdarzenia, na ktore reaguje warstwa Androida (np. wibracja). */
sealed interface TrainingEvent {
    data object SessionFinished : TrainingEvent
}
