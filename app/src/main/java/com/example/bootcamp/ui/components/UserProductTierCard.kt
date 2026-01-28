package com.example.bootcamp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.data.remote.dto.UserTierLimitDTO
import java.text.NumberFormat
import java.util.Locale

@Composable
fun UserProductTierCard(tierDTO: UserTierLimitDTO, modifier: Modifier = Modifier) {
    // Determine colors based on tier code
    val tierCode = tierDTO.tierCode?.uppercase() ?: ""
    val (startColor, endColor) = when {
        tierCode.contains("BRONZE") -> Color(0xFFCD7F32) to Color(0xFFA05A2C)
        tierCode.contains("SILVER") -> Color(0xFF94A3B8) to Color(0xFF475569)
        tierCode.contains("GOLD") -> Color(0xFFF59E0B) to Color(0xFFB45309)
        else -> Color(0xFF6366F1) to Color(0xFF4338CA) // Fallback Indigo
    }

    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val creditLimit = tierDTO.creditLimit ?: 0.0
    val currentUsed = tierDTO.currentUsedAmount ?: 0.0
    val available = tierDTO.availableCredit ?: (creditLimit - currentUsed)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Use Box for gradient
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(startColor, endColor)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tierDTO.tierName ?: "Premium Tier",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tierDTO.status ?: "ACTIVE",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Balance Info
                Column {
                    Text(
                        text = "Available Limit",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = formatter.format(available),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Limit",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatter.format(creditLimit),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Used",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatter.format(currentUsed),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Upgrade Progress (if applicable)
                if (tierDTO.upgradeThreshold != null && tierDTO.upgradeThreshold > 0) {
                    val progress = (tierDTO.totalPaidAmount ?: 0.0) / tierDTO.upgradeThreshold
                    val animatedProgress by animateFloatAsState(targetValue = progress.toFloat().coerceIn(0f, 1f))
                    val remaining = tierDTO.remainingToUpgrade ?: 0.0

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Next Tier Progress",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (remaining > 0) {
                                Text(
                                    text = "Pay ${formatter.format(remaining)} more",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )
                            } else {
                                Text(
                                    text = "Eligible for Upgrade!",
                                    color = Color(0xFF4ADE80), // Green
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color.White,
                            trackColor = Color.Black.copy(alpha = 0.2f),
                            // strokeCap = androidx.compose.ui.graphics.StrokeCap.Round // Requires API > 23? Compose usually handles this.
                        )
                    }
                }
            }
        }
    }
}
