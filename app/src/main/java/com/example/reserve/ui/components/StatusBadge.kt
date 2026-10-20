package com.example.reserve.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = statusColors(status)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Text(
            text = status.replace("_", " "),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
private fun statusColors(status: String): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (status.uppercase()) {
        "AVAILABLE" -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        "PENDING" -> Color(0xFFFFF3CD) to Color(0xFF664D03)
        "APPROVED" -> scheme.primaryContainer to scheme.onPrimaryContainer
        "REQUESTED" -> scheme.secondaryContainer to scheme.onSecondaryContainer
        "COLLECTED" -> scheme.primaryContainer to scheme.onPrimaryContainer
        "COMPLETED" -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        "REJECTED" -> scheme.errorContainer to scheme.onErrorContainer
        "EXPIRED" -> scheme.surfaceVariant to scheme.onSurfaceVariant
        else -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
}
