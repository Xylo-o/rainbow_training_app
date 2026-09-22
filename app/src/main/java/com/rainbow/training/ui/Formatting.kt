package com.rainbow.training.ui

import java.util.Locale

/** "0.5 s", "2.4 s" - sekundy z jednym miejscem po kropce dziesietnej. */
fun formatSeconds(millis: Long): String =
    String.format(Locale.US, "%.1f s", millis / 1000.0)

/** "45 s", "2 min", "2 min 30 s" - zwiezly opis dlugosci sesji. */
fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return when {
        minutes == 0L -> "$seconds s"
        seconds == 0L -> "$minutes min"
        else -> "$minutes min $seconds s"
    }
}

/** "1:05" - zegar odliczajacy czas do konca sesji. */
fun formatClock(millis: Long): String {
    val totalSeconds = (millis.coerceAtLeast(0L) + 999) / 1000
    return String.format(Locale.ROOT, "%d:%02d", totalSeconds / 60, totalSeconds % 60)
}
