package com.example.bootcamp.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glassmorphism-styled card component. Follows Style1 design with semi-transparent background and
 * blur effect simulation.
 *
 * @param modifier Modifier for the card
 * @param cornerRadius Corner radius for the card shape
 * @param contentPadding Padding for the card content
 * @param content Card content
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 28.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
    ) {
        Box(
            modifier =
            Modifier.fillMaxWidth()
                .background(
                    brush =
                    Brush.verticalGradient(
                        colors =
                        listOf(
                            Color.White.copy(
                                alpha = 0.15f
                            ),
                            Color.White.copy(
                                alpha = 0.05f
                            )
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}
