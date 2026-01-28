package com.example.bootcamp.ui.components.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bootcamp.ui.theme.SpaceIndigo

/**
 * Full-screen loading overlay. Displays a semi-transparent overlay with a loading indicator.
 *
 * @param isLoading Whether to show the loading overlay
 * @param modifier Modifier for the overlay
 */
@Composable
fun LoadingOverlay(isLoading: Boolean, modifier: Modifier = Modifier) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = SpaceIndigo,
                strokeWidth = 4.dp
            )
        }
    }
}

/**
 * Inline loading indicator for buttons or small spaces.
 *
 * @param modifier Modifier for the indicator
 * @param size Size of the indicator
 * @param color Color of the indicator
 */
@Composable
fun InlineLoader(modifier: Modifier = Modifier, size: Int = 24, color: Color = Color.White) {
    CircularProgressIndicator(modifier = modifier.size(size.dp), color = color, strokeWidth = 2.dp)
}
