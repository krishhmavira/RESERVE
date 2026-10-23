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
    val displayText = statusDisplayText(status)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = backgroundColor
    ) {
        Text(
            text = displayText,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing
        )
    }
}

private fun statusDisplayText(status: String): String {
    return when (status.uppercase()) {
        "AVAILABLE" -> "Available"
        "PENDING" -> "Pending"
        "APPROVED" -> "Approved"
        "REQUESTED" -> "Requested"
        "COLLECTED" -> "Collected"
        "COMPLETED" -> "Completed"
        "REJECTED" -> "Declined"
        "EXPIRED" -> "Expired"
        else -> status.replace("_", " ")
    }
}

@Composable
private fun statusColors(status: String): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (status.uppercase()) {
        "AVAILABLE" -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        "PENDING" -> scheme.secondaryContainer to scheme.onSecondaryContainer
        "APPROVED" -> scheme.primaryContainer to scheme.onPrimaryContainer
        "REQUESTED" -> scheme.secondaryContainer to scheme.onSecondaryContainer
        "COLLECTED" -> scheme.primaryContainer to scheme.onPrimaryContainer
        "COMPLETED" -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        "REJECTED" -> scheme.errorContainer to scheme.onErrorContainer
        "EXPIRED" -> scheme.surfaceVariant to scheme.onSurfaceVariant
        else -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
}
