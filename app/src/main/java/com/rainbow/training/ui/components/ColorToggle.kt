package com.rainbow.training.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.rainbow.training.R
import com.rainbow.training.core.model.StimulusColor
import com.rainbow.training.ui.composeColor
import com.rainbow.training.ui.label

/**
 * Kafelek jednego koloru z palety.
 *
 * Wypelnienie jest zawsze pelna barwa - inaczej biel i czern nie bylyby rozpoznawalne. Stan
 * niosa przezroczystosc calego kafelka, grubosc obramowania i znacznik.
 *
 * Znacznik celowo NIE uzywa barwy dopelniajacej (tej od cyfr): to cienka kreska na malym
 * kafelku, wiec liczy sie tu czysty kontrast, a nie barwa.
 */
@Composable
fun ColorToggle(
    color: StimulusColor,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    val name = color.label()
    val state = stringResource(
        if (selected) R.string.color_state_on else R.string.color_state_off,
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .alpha(if (selected) 1f else 0.4f)
            .clip(shape)
            .background(color.composeColor)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = shape,
            )
            .clickable(onClick = onToggle)
            .semantics {
                contentDescription = name
                stateDescription = state
            },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(color.outlineArgb),
            )
        }
    }
}
