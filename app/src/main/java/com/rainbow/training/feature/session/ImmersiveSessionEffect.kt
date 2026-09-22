package com.rainbow.training.feature.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.rainbow.training.ui.findActivity

/**
 * Na czas sesji: ekran nie gasnie, paski systemowe znikaja, a nic poza bodzcem nie rozprasza.
 * Wszystko wraca do normy, gdy ekran sesji opuszcza kompozycje.
 */
@Composable
fun ImmersiveSessionEffect() {
    val view = LocalView.current

    DisposableEffect(view) {
        view.keepScreenOn = true

        val window = view.context.findActivity()?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            view.keepScreenOn = false
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
