package com.example.bootcamp.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.ui.theme.SpaceViolet

/**
 * Secondary button with outline/glassmorphism style. Follows Style1 design with transparent
 * background and subtle border.
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param height Button height
 * @param cornerRadius Corner radius for the button shape
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 54.dp,
    cornerRadius: Dp = 14.dp
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(height),
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        border =
        BorderStroke(
            width = 1.dp,
            color =
            if (enabled) {
                SpaceViolet.copy(alpha = 0.5f)
            } else {
                Color.Gray.copy(alpha = 0.3f)
            }
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) SpaceViolet else Color.Gray,
        )
    }
}
