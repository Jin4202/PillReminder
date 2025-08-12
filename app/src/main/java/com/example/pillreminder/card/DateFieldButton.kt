package com.example.pillreminder.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DateFieldButton(
    label: String,
    valueText: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "MM/DD/YYYY",
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    trailingIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val borderColor =
        when {
            isError  -> colors.error
            pressed  -> colors.primary
            !enabled -> colors.outlineVariant.copy(alpha = 0.6f)
            else     -> colors.outlineVariant
        }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surface,
            contentColor = colors.onSurface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable(
                    enabled = enabled,
                    interactionSource = interaction,
                    indication = rememberRipple(bounded = true),
                    onClick = onClick
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueText?.takeIf { it.isNotBlank() } ?: placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (valueText.isNullOrBlank())
                        colors.onSurfaceVariant.copy(alpha = 0.7f)
                    else
                        colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                trailingIcon()
            }
        }

        if (supportingText != null || isError) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = supportingText ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = if (isError) colors.error else colors.onSurfaceVariant
            )
        }
    }
}
