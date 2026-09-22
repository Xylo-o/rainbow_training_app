package com.rainbow.training.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.rainbow.training.core.model.StimulusColor
import com.rainbow.training.ui.composeColor

/**
 * Kafelek jednego koloru z palety. Zaznaczony swieci pelna barwa i nosi znacznik w kolorze
 * maksymalnie kontrastowym - tym samym, ktorym beda rysowane cyfry na tym tle.
 */
@Composable
fun ColorToggle(
    color: StimulusColor,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val target = color.composeColor
    val fill by animateColorAsState(
        targetValue = if (selected) target else target.copy(alpha = 0.22f),
        label = "fill",
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(fill)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onToggle)
            .semantics {
                contentDescription = color.displayName
                stateDescription = if (selected) "włączony" else "wyłączony"
            },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(color.contentArgb),
            )
        }
    }
}
