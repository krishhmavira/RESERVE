package com.example.reserve.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            color = textColor
        )
    }
}

private fun statusColors(status: String): Pair<Color, Color> {
    return when (status.uppercase()) {
        "AVAILABLE" -> Color(0xFFD8F3DC) to Color(0xFF1B4332)
        "PENDING" -> Color(0xFFFFF3CD) to Color(0xFF664D03)
        "APPROVED" -> Color(0xFFD1ECF1) to Color(0xFF0C5460)
        "REQUESTED" -> Color(0xFFE2E3F1) to Color(0xFF383D6E)
        "COLLECTED" -> Color(0xFFB7E4C7) to Color(0xFF002114)
        "COMPLETED" -> Color(0xFFD4EDDA) to Color(0xFF155724)
        "REJECTED" -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        "EXPIRED" -> Color(0xFFE2E3DD) to Color(0xFF434840)
        else -> Color(0xFFE2E3DD) to Color(0xFF434840)
    }
}
