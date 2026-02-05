package com.example.bootcamp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bootcamp.domain.model.LoanMilestone
import com.example.bootcamp.domain.model.MilestoneStatus
import com.example.bootcamp.ui.theme.Emerald500
import com.example.bootcamp.ui.theme.Gray500
import com.example.bootcamp.ui.theme.Gray700
import com.example.bootcamp.ui.theme.SpaceIndigo

/**
 * Horizontal timeline component for displaying loan milestones.
 * Each milestone column contains both the dot and the label, ensuring proper centering.
 */
@Composable
fun MilestoneTimeline(milestones: List<LoanMilestone>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        milestones.forEachIndexed { index, milestone ->
            val isLast = index == milestones.lastIndex
            val nextMilestone = milestones.getOrNull(index + 1)

            // Each milestone takes equal weight
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dot with connecting line drawn behind it
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .drawBehind {
                            // Draw line to the right if not the last milestone
                            if (!isLast) {
                                val lineColor = if (milestone.status == MilestoneStatus.COMPLETED) {
                                    Emerald500
                                } else {
                                    Gray700
                                }
                                drawLine(
                                    color = lineColor,
                                    start = Offset(size.width / 2, size.height / 2),
                                    end = Offset(size.width, size.height / 2),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                            // Draw line from the left if not the first milestone
                            if (index > 0) {
                                val prevMilestone = milestones[index - 1]
                                val lineColor = if (prevMilestone.status == MilestoneStatus.COMPLETED) {
                                    Emerald500
                                } else {
                                    Gray700
                                }
                                drawLine(
                                    color = lineColor,
                                    start = Offset(0f, size.height / 2),
                                    end = Offset(size.width / 2, size.height / 2),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    MilestoneDot(status = milestone.status)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Label
                MilestoneLabel(
                    name = milestone.name,
                    timestamp = milestone.timestamp,
                    status = milestone.status
                )
            }
        }
    }
}

@Composable
private fun MilestoneDot(status: MilestoneStatus) {
    val color = when (status) {
        MilestoneStatus.COMPLETED -> Emerald500
        MilestoneStatus.CURRENT -> SpaceIndigo
        MilestoneStatus.PENDING -> Gray700
    }
    val outerColor = when (status) {
        MilestoneStatus.CURRENT -> SpaceIndigo.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    Box(contentAlignment = Alignment.Center) {
        // Outer glow for CURRENT status
        if (status == MilestoneStatus.CURRENT) {
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(color = outerColor)
            }
        }
        // Inner dot
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
    }
}

@Composable
private fun MilestoneLabel(name: String, timestamp: String?, status: MilestoneStatus) {
    val textColor = when (status) {
        MilestoneStatus.COMPLETED -> Color.White
        MilestoneStatus.CURRENT -> SpaceIndigo
        MilestoneStatus.PENDING -> Gray500
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = name,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = if (status == MilestoneStatus.CURRENT) FontWeight.Bold else FontWeight.Normal,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
        if (timestamp != null) {
            Text(
                text = timestamp.split("T").getOrNull(0) ?: "",
                color = Gray500,
                fontSize = 8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
