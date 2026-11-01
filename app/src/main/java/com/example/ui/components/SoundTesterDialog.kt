package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.audio.WorkoutToneType
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.NeonDoneGold
import com.example.ui.theme.NeonNextSetCoral
import com.example.ui.theme.NeonRestCyan
import com.example.ui.theme.NeonWorkGreen

@Composable
fun SoundTesterDialog(
  soundEnabled: Boolean,
  vibrationEnabled: Boolean,
  countdownTicksEnabled: Boolean,
  themeMode: AppThemeMode,
  onSoundToggle: (Boolean) -> Unit,
  onVibrationToggle: (Boolean) -> Unit,
  onCountdownTicksToggle: (Boolean) -> Unit,
  onThemeChange: (AppThemeMode) -> Unit,
  onPlayTestTone: (WorkoutToneType) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Audio & Gym Ambience",
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Preview distinct transition tones",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_sound_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close settings",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tone Audition Section
        Text(
          text = "TAP TO TEST TRANSITION TONES",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        TonePreviewButton(
          title = "Rest Tone",
          subtitle = "Calming two-tone chime when rest period begins",
          accentColor = NeonRestCyan,
          icon = Icons.Default.Bedtime,
          onClick = { onPlayTestTone(WorkoutToneType.REST) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TonePreviewButton(
          title = "Next Set Tone",
          subtitle = "Punchy double-beep when rest ends and next set starts",
          accentColor = NeonNextSetCoral,
          icon = Icons.Default.FitnessCenter,
          onClick = { onPlayTestTone(WorkoutToneType.NEXT_SET) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TonePreviewButton(
          title = "Next Phase Tone",
          subtitle = "Ascending triple-alert when starting a new exercise",
          accentColor = NeonWorkGreen,
          icon = Icons.Default.Schedule,
          onClick = { onPlayTestTone(WorkoutToneType.NEXT_PHASE) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TonePreviewButton(
          title = "Workout Complete",
          subtitle = "Triumphant victory fanfare when the routine ends",
          accentColor = NeonDoneGold,
          icon = Icons.Default.SportsScore,
          onClick = { onPlayTestTone(WorkoutToneType.DONE) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(14.dp))

        // Audio & Haptic Toggles
        SettingSwitchRow(
          title = "Sound Effects",
          description = "Synthesized transition tones during workout",
          checked = soundEnabled,
          onCheckedChange = onSoundToggle
        )

        SettingSwitchRow(
          title = "Vibration Feedback",
          description = "Haptic pulses for gym earbuds or pockets",
          checked = vibrationEnabled,
          onCheckedChange = onVibrationToggle
        )

        SettingSwitchRow(
          title = "3-2-1 Countdown Ticks",
          description = "Short tick beeps before each transition",
          checked = countdownTicksEnabled,
          onCheckedChange = onCountdownTicksToggle
        )

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(14.dp))

        // Gym Environment Theme Selector
        Text(
          text = "GYM DISPLAY THEME",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ThemeSelectChip(
            title = "Gym Dark",
            subtitle = "OLED Pure Black",
            selected = themeMode == AppThemeMode.GYM_DARK,
            icon = Icons.Default.Bedtime,
            modifier = Modifier.weight(1f),
            onClick = { onThemeChange(AppThemeMode.GYM_DARK) }
          )

          ThemeSelectChip(
            title = "Athletic Light",
            subtitle = "Clean Daylight",
            selected = themeMode == AppThemeMode.LIGHT,
            icon = Icons.Default.Brightness7,
            modifier = Modifier.weight(1f),
            onClick = { onThemeChange(AppThemeMode.LIGHT) }
          )
        }
      }
    }
  }
}

@Composable
private fun TonePreviewButton(
  title: String,
  subtitle: String,
  accentColor: Color,
  icon: ImageVector,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(accentColor.copy(alpha = 0.18f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(accentColor.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Test tone",
        tint = accentColor,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun SettingSwitchRow(
  title: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.primary,
        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
      )
    )
  }
}

@Composable
private fun ThemeSelectChip(
  title: String,
  subtitle: String,
  selected: Boolean,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
  val bgColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(20.dp)
    )

    Spacer(modifier = Modifier.width(8.dp))

    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
