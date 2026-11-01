package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExpandedInterval
import com.example.model.PhaseType
import com.example.ui.theme.ChakraPetchFont
import com.example.ui.theme.NeonWorkGreen

@Composable
fun CircularTimerDisplay(
  interval: ExpandedInterval?,
  secondsLeft: Int,
  sizeDp: Dp = 280.dp,
  modifier: Modifier = Modifier
) {
  val totalDuration = maxOf(1, interval?.durationSeconds ?: 1)
  val progress = (secondsLeft.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)

  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
    label = "timer_progress"
  )

  val phase = interval?.phaseType ?: PhaseType.WORK
  val accentColor = phase.getAccentColor()

  val animatedColor by animateColorAsState(
    targetValue = accentColor,
    animationSpec = tween(400),
    label = "phase_color"
  )

  val minutes = secondsLeft / 60
  val seconds = secondsLeft % 60
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)

  Box(
    modifier = modifier
      .size(sizeDp)
      .testTag("circular_timer_display"),
    contentAlignment = Alignment.Center
  ) {
    // Outer glow & track canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val strokeWidth = 14.dp.toPx()
      val diameter = size.minDimension - strokeWidth
      val topLeft = androidx.compose.ui.geometry.Offset(
        (size.width - diameter) / 2f,
        (size.height - diameter) / 2f
      )
      val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

      // Background Track
      drawArc(
        color = Color(0xFF1E293B),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // Active Neon Progress Arc
      val sweep = 360f * animatedProgress
      if (sweep > 0f) {
        drawArc(
          brush = Brush.sweepGradient(
            listOf(
              animatedColor.copy(alpha = 0.6f),
              animatedColor,
              animatedColor
            )
          ),
          startAngle = -90f,
          sweepAngle = sweep,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }
    }

    // Centered Timer Content
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      // Phase Badge
      val badgeLabel = when {
        interval?.isRestBetweenSets == true -> "REST BETWEEN SETS"
        phase == PhaseType.REST -> "REST PERIOD"
        phase == PhaseType.WORK -> "WORK SET"
        else -> phase.shortLabel
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(100.dp))
          .background(animatedColor.copy(alpha = 0.18f))
          .border(1.5.dp, animatedColor, RoundedCornerShape(100.dp))
          .padding(horizontal = 14.dp, vertical = 4.dp)
      ) {
        Text(
          text = badgeLabel,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.ExtraBold,
          color = animatedColor,
          letterSpacing = 1.2.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Giant Time Digits
      Text(
        text = timeFormatted,
        fontFamily = ChakraPetchFont,
        fontWeight = FontWeight.Bold,
        fontSize = 58.sp,
        lineHeight = 60.sp,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("timer_countdown_digits")
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Sets & Reps Pill
      if (interval != null) {
        val subtitleText = when {
          interval.isRestBetweenSets -> {
            "RESTING • UP NEXT: SET ${interval.currentSet + 1} OF ${interval.totalSets}"
          }
          interval.phaseType == PhaseType.REST -> {
            "RECOVERY BREAK"
          }
          interval.totalSets > 1 -> {
            val targetText = if (interval.repsTarget.isNotBlank() && interval.repsTarget != "-") {
              " • ${interval.repsTarget.uppercase()}"
            } else ""
            "SET ${interval.currentSet} OF ${interval.totalSets}$targetText"
          }
          else -> {
            val targetText = if (interval.repsTarget.isNotBlank() && interval.repsTarget != "-") {
              " • ${interval.repsTarget.uppercase()}"
            } else ""
            "SET 1$targetText"
          }
        }

        Text(
          text = subtitleText,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 1.sp
        )
      }
    }
  }
}
