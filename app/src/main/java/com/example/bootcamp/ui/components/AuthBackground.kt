package com.example.bootcamp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.bootcamp.ui.theme.DeepSpace1
import com.example.bootcamp.ui.theme.DeepSpace2
import com.example.bootcamp.ui.theme.DeepSpace3
import com.example.bootcamp.ui.theme.SpaceIndigo
import com.example.bootcamp.ui.theme.SpacePink
import com.example.bootcamp.ui.theme.SpacePurple
import com.example.bootcamp.ui.theme.SpaceViolet
import kotlin.random.Random

@Composable
fun AuthBackground(
    content: @Composable (glowAlpha: Float) -> Unit
) {
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    // Floating animation for shapes
    val floatOffset1 by
    infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatOffset2 by
    infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val floatOffset3 by
    infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float3"
    )

    // Rotation animation for shapes
    val rotation1 by
    infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation1"
    )

    // Glow animation for title (passed to content)
    val glowAlpha by
    infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Twinkle animation for stars
    val starAlpha by
    infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec =
        infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Box(
        modifier =
        Modifier.fillMaxSize()
            .background(
                brush =
                Brush.linearGradient(
                    colors =
                    listOf(
                        DeepSpace1,
                        DeepSpace2,
                        DeepSpace3,
                    ),
                    start = Offset(0f, 0f),
                    end =
                    Offset(
                        Float.POSITIVE_INFINITY,
                        Float.POSITIVE_INFINITY
                    )
                )
            )
    ) {
        // Star field background
        Box(
            modifier =
            Modifier.fillMaxSize().drawBehind {
                val random =
                    Random(
                        42
                    ) // Fixed seed for consistent star positions
                for (i in 0..100) {
                    val x = random.nextFloat() * size.width
                    val y = random.nextFloat() * size.height
                    val starSize = random.nextFloat() * 2f + 1f
                    drawCircle(
                        color =
                        Color.White.copy(
                            alpha =
                            starAlpha *
                                    random.nextFloat()
                        ),
                        radius = starSize,
                        center = Offset(x, y)
                    )
                }
            }
        )

        // Floating Shape 1 - Purple glow (top-left)
        Box(
            modifier =
            Modifier.size(200.dp)
                .offset(x = (-50).dp, y = 100.dp + floatOffset1.dp)
                .graphicsLayer { rotationZ = rotation1 }
                .blur(40.dp)
                .clip(CircleShape)
                .background(
                    brush =
                    Brush.radialGradient(
                        colors =
                        listOf(
                            SpaceViolet.copy(
                                alpha = 0.4f
                            ),
                            SpaceIndigo.copy(
                                alpha = 0.2f
                            ),
                            Color.Transparent
                        )
                    )
                )
        )

        // Floating Shape 2 - Indigo glow (right)
        Box(
            modifier =
            Modifier.size(300.dp)
                .offset(x = 200.dp, y = 300.dp + floatOffset2.dp)
                .blur(40.dp)
                .clip(CircleShape)
                .background(
                    brush =
                    Brush.radialGradient(
                        colors =
                        listOf(
                            SpaceIndigo.copy(
                                alpha = 0.3f
                            ),
                            SpacePurple.copy(
                                alpha =
                                0.15f
                            ),
                            Color.Transparent
                        )
                    )
                )
        )

        // Floating Shape 3 - Pink glow (bottom)
        Box(
            modifier =
            Modifier.size(400.dp)
                .offset(x = (-100).dp, y = 600.dp + floatOffset3.dp)
                .blur(40.dp)
                .clip(CircleShape)
                .background(
                    brush =
                    Brush.radialGradient(
                        colors =
                        listOf(
                            SpacePink.copy(
                                alpha = 0.2f
                            ),
                            SpaceViolet.copy(
                                alpha = 0.1f
                            ),
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Main Content
        content(glowAlpha)
    }
}
