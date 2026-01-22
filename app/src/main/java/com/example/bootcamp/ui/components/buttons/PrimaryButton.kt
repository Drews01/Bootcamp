package com.example.bootcamp.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.ui.theme.SpaceIndigo
import com.example.bootcamp.ui.theme.SpaceViolet

/**
 * Primary button with gradient background. Follows Style1 design with purple gradient and glow
 * effect.
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param height Button height
 * @param cornerRadius Corner radius for the button shape
 */
@Composable
fun PrimaryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        height: Dp = 54.dp,
        cornerRadius: Dp = 14.dp
) {
    Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().height(height),
            enabled = enabled,
            shape = RoundedCornerShape(cornerRadius),
            colors =
                    ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    ),
            contentPadding = PaddingValues(0.dp)
    ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(height)
                                .background(
                                        brush =
                                                if (enabled) {
                                                    Brush.horizontalGradient(
                                                            colors =
                                                                    listOf(SpaceIndigo, SpaceViolet)
                                                    )
                                                } else {
                                                    Brush.horizontalGradient(
                                                            colors = listOf(Color.Gray, Color.Gray)
                                                    )
                                                },
                                        shape = RoundedCornerShape(cornerRadius)
                                ),
                contentAlignment = Alignment.Center
        ) {
            Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
            )
        }
    }
}
