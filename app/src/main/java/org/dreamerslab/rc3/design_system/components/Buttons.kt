package org.dreamerslab.rc3.design_system.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.dreamerslab.rc3.design_system.theme.spacing

@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    border: BorderStroke? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(color)
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColor,
        )
    ) {
        ButtonContent(
            label = label,
            icon = icon,
            trailingIcon = trailingIcon
        )
    }
}

@Composable
fun SecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )
    ) {
        ButtonContent(
            label = label,
            icon = icon,
            trailingIcon = trailingIcon
        )
    }
}

@Composable
fun PlainButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled
    ) {
        ButtonContent(
            label = label,
            icon = icon,
            trailingIcon = trailingIcon
        )
    }
}

@Composable
private fun ButtonContent(
    label: String,
    icon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
) {
    if (icon == null && trailingIcon == null) {
        Text(text = label)
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.extraSmall,
                Alignment.CenterHorizontally
            ),
        ) {
            icon?.let { it() }
            Text(
                text = label,
                textAlign = TextAlign.Center
            )
            trailingIcon?.let { it() }
        }
    }
}