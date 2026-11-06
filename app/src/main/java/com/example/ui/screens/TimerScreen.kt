package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExpandedInterval
import com.example.model.WorkoutPlaylist
import com.example.ui.components.CircularTimerDisplay
import com.example.ui.components.UpNextCard
import com.example.ui.theme.ChakraPetchFont
import com.example.ui.theme.NeonDoneGold
import com.example.ui.theme.NeonWorkGreen

@Composable
fun TimerScreen(
  playlist: WorkoutPlaylist,
  currentInterval: ExpandedInterval?,
  nextInterval: ExpandedInterval?,
  secondsLeft: Int,
  isTimerRunning: Boolean,
  isCompleted: Boolean,
  isScreenLocked: Boolean,
  soundEnabled: Boolean,
  soundProfile: com.example.audio.SoundProfile,
  elapsedTotalSeconds: Int,
  onTogglePlayPause: () -> Unit,
  onSkipNext: () -> Unit,
  onPrevious: () -> Unit,
  onAdjustTime: (Int) -> Unit,
  onToggleLock: () -> Unit,
  onToggleSound: () -> Unit,
  onToggleStealthMode: () -> Unit,
  onOpenSoundSettings: () -> Unit,
  onRestart: () -> Unit,
  onExit: () -> Unit
) {
  BackHandler {
    onExit()
  }

  val totalPlaylistSeconds = maxOf(1, playlist.totalDurationSeconds)
  val overallProgress = (elapsedTotalSeconds.toFloat() / totalPlaylistSeconds.toFloat()).coerceIn(0f, 1f)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    if (isCompleted) {
      // Victory / Finished Screen
      WorkoutCompletedView(
        playlist = playlist,
        elapsedSeconds = elapsedTotalSeconds,
        onRestart = onRestart,
        onExit = onExit
      )
    } else {
      // Active Workout Screen
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onExit,
            modifier = Modifier.testTag("exit_workout_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Exit Workout",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = playlist.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1
            )
            if (currentInterval != null) {
              Text(
                text = "INTERVAL ${currentInterval.index + 1} OF ${currentInterval.totalIntervals}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            val isStealth = soundProfile == com.example.audio.SoundProfile.STEALTH_VIBRATE

            // Stealth mode quick toggle (vibrate only)
            IconButton(
              onClick = onToggleStealthMode,
              modifier = Modifier.testTag("timer_stealth_toggle")
            ) {
              Icon(
                imageVector = if (isStealth) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = if (isStealth) "Exit Stealth Mode" else "Switch to Stealth Mode",
                tint = if (isStealth) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
              )
            }

            // Quick Audio & Ambience settings dialog
            IconButton(
              onClick = onOpenSoundSettings,
              modifier = Modifier.testTag("timer_sound_settings_button")
            ) {
              Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Sound Settings & Volume",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Lock screen touch guard
            IconButton(
              onClick = onToggleLock,
              modifier = Modifier.testTag("toggle_screen_lock_button")
            ) {
              Icon(
                imageVector = if (isScreenLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = "Lock Screen Guard",
                tint = if (isScreenLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Sound Profile Indicator Pill
        val isStealth = soundProfile == com.example.audio.SoundProfile.STEALTH_VIBRATE
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isStealth) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, if (isStealth) MaterialTheme.colorScheme.error.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(100.dp))
            .clickable(onClick = onOpenSoundSettings)
            .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (soundProfile) {
              com.example.audio.SoundProfile.STEALTH_VIBRATE -> Icons.Default.VolumeMute
              com.example.audio.SoundProfile.MELLOW_CHIMES -> Icons.Default.SpatialAudio
              com.example.audio.SoundProfile.VOICE_COACH -> Icons.Default.RecordVoiceOver
              com.example.audio.SoundProfile.MODERN_SYNTH -> Icons.Default.VolumeUp
            }
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = if (isStealth) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = soundProfile.displayName.uppercase(),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = if (isStealth) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Circular Timer Display
        CircularTimerDisplay(
          interval = currentInterval,
          secondsLeft = secondsLeft,
          sizeDp = 270.dp
        )

        // Exercise Title & Notes
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = currentInterval?.exerciseName ?: "Get Ready",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )

          if (!currentInterval?.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = currentInterval?.notes ?: "",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 16.dp)
            )
          }
        }

        // Up Next Preview Card
        UpNextCard(nextInterval = nextInterval)

        // Overall progress & total elapsed time
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            val elMins = elapsedTotalSeconds / 60
            val elSecs = elapsedTotalSeconds % 60
            Text(
              text = "Elapsed: ${String.format("%02d:%02d", elMins, elSecs)}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val remSecs = (totalPlaylistSeconds - elapsedTotalSeconds).coerceAtLeast(0)
            val remMins = remSecs / 60
            val remS = remSecs % 60
            Text(
              text = "Left: ${String.format("%02d:%02d", remMins, remS)}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { overallProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )
        }

        // Playback Controls Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Previous step / restart current step
          IconButton(
            onClick = onPrevious,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
              .testTag("timer_previous_button")
          ) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Interval", tint = MaterialTheme.colorScheme.onSurface)
          }

          // -15s
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
              .clickable { onAdjustTime(-15) }
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .testTag("timer_minus_15_button")
          ) {
            Text(
              text = "-15s",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          // Big Main Play/Pause Button
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                  )
                )
              )
              .clickable(onClick = onTogglePlayPause)
              .testTag("timer_play_pause_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isTimerRunning) "Pause" else "Play",
              tint = Color.Black,
              modifier = Modifier.size(40.dp)
            )
          }

          // +15s
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
              .clickable { onAdjustTime(+15) }
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .testTag("timer_plus_15_button")
          ) {
            Text(
              text = "+15s",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          // Skip to next step
          IconButton(
            onClick = onSkipNext,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
              .testTag("timer_skip_next_button")
          ) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next Interval", tint = MaterialTheme.colorScheme.onSurface)
          }
        }
      }

      // Lock Guard Overlay
      AnimatedVisibility(
        visible = isScreenLocked,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .clickable { /* consume clicks */ },
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "SCREEN LOCKED",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Touch controls disabled to prevent pocket taps during sets",
              style = MaterialTheme.typography.bodySmall,
              color = Color.Gray,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
              onClick = onToggleLock,
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("unlock_screen_button")
            ) {
              Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Tap to Unlock", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WorkoutCompletedView(
  playlist: WorkoutPlaylist,
  elapsedSeconds: Int,
  onRestart: () -> Unit,
  onExit: () -> Unit
) {
  val mins = elapsedSeconds / 60
  val secs = elapsedSeconds % 60

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(28.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(96.dp)
        .clip(CircleShape)
        .background(NeonDoneGold.copy(alpha = 0.18f))
        .border(2.dp, NeonDoneGold, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.EmojiEvents,
        contentDescription = "Trophy",
        tint = NeonDoneGold,
        modifier = Modifier.size(54.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "WORKOUT CRUSHED!",
      fontFamily = ChakraPetchFont,
      fontWeight = FontWeight.Bold,
      fontSize = 28.sp,
      letterSpacing = 1.5.sp,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Routine: ${playlist.title}",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Workout summary stats
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        .padding(18.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "TOTAL TIME",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = String.format("%02d:%02d", mins, secs),
          fontFamily = ChakraPetchFont,
          fontWeight = FontWeight.Bold,
          fontSize = 22.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "STEPS",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${playlist.steps.size}",
          fontFamily = ChakraPetchFont,
          fontWeight = FontWeight.Bold,
          fontSize = 22.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "TOTAL SETS",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${playlist.totalSetsCount}",
          fontFamily = ChakraPetchFont,
          fontWeight = FontWeight.Bold,
          fontSize = 22.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = onRestart,
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
      ) {
        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Restart")
      }

      Button(
        onClick = onExit,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("finish_workout_button")
      ) {
        Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }
  }
}
