package com.example.bootcamp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 */
@Composable
fun MilestoneTimeline(
    milestones: List<LoanMilestone>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(vertical = 12.dp)
    ) {
        // Dots and lines row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            milestones.forEachIndexed { index, milestone ->
                val isLast = index == milestones.lastIndex

                // Dot
                MilestoneDot(status = milestone.status)

                // Connecting line (not after the last dot)
                if (!isLast) {
                    ConnectingLine(
                        isCompleted = milestone.status == MilestoneStatus.COMPLETED,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            milestones.forEach { milestone ->
                MilestoneLabel(
                    name = milestone.name,
                    timestamp = milestone.timestamp,
                    status = milestone.status,
                    modifier = Modifier.weight(1f)
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
        Canvas(modifier = Modifier.size(24.dp)) {
            drawCircle(color = outerColor)
        }
        // Inner dot
        Canvas(modifier = Modifier.size(14.dp)) {
            drawCircle(color = color)
        }
    }
}

@Composable
private fun ConnectingLine(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isCompleted) Emerald500 else Gray700

    Canvas(
        modifier = modifier
            .height(4.dp)
            .padding(horizontal = 2.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun MilestoneLabel(
    name: String,
    timestamp: String?,
    status: MilestoneStatus,
    modifier: Modifier = Modifier
) {
    val textColor = when (status) {
        MilestoneStatus.COMPLETED -> Color.White
        MilestoneStatus.CURRENT -> SpaceIndigo
        MilestoneStatus.PENDING -> Gray500
    }

    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = if (status == MilestoneStatus.CURRENT) FontWeight.Bold else FontWeight.Normal,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
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
