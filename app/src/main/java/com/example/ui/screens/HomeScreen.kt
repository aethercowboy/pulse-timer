package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WorkoutDay
import com.example.model.WorkoutPlaylist
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ChakraPetchFont
import com.example.ui.theme.NeonWorkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  playlists: List<WorkoutPlaylist>,
  todayPlaylist: WorkoutPlaylist?,
  selectedDayFilter: String,
  todayDayCode: String,
  themeMode: AppThemeMode,
  onSelectDayFilter: (String) -> Unit,
  onStartWorkout: (WorkoutPlaylist) -> Unit,
  onEditPlaylist: (WorkoutPlaylist) -> Unit,
  onDuplicatePlaylist: (WorkoutPlaylist) -> Unit,
  onDeletePlaylist: (WorkoutPlaylist) -> Unit,
  onCreatePlaylist: () -> Unit,
  onRestorePresets: () -> Unit,
  onToggleTheme: () -> Unit,
  onOpenSoundSettings: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NeonWorkGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = NeonWorkGreen,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "PULSETIMER",
              fontFamily = ChakraPetchFont,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              fontSize = 20.sp,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
        },
        actions = {
          // Gym Dark / Light toggle
          IconButton(
            onClick = onToggleTheme,
            modifier = Modifier.testTag("toggle_gym_theme_button")
          ) {
            Icon(
              imageVector = if (themeMode == AppThemeMode.GYM_DARK) Icons.Default.Brightness7 else Icons.Default.Bedtime,
              contentDescription = "Toggle Gym Theme",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          // Audio Tones & Settings
          IconButton(
            onClick = onOpenSoundSettings,
            modifier = Modifier.testTag("open_sound_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "Audio Tones & Ambience",
              tint = NeonWorkGreen
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onCreatePlaylist,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.Black,
        modifier = Modifier.testTag("create_playlist_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "New Workout Playlist")
      }
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 88.dp)
    ) {
      // Day of Week Filter Bar
      item {
        DayFilterBar(
          selectedFilter = selectedDayFilter,
          todayCode = todayDayCode,
          onSelectFilter = onSelectDayFilter
        )
      }

      // Today's Routine Hero Card
      if (todayPlaylist != null && (selectedDayFilter == "ALL" || selectedDayFilter == todayDayCode)) {
        item {
          TodayWorkoutHero(
            playlist = todayPlaylist,
            onStartWorkout = { onStartWorkout(todayPlaylist) },
            onEdit = { onEditPlaylist(todayPlaylist) }
          )
        }
      }

      // Section Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (selectedDayFilter == "ALL") "ALL WORKOUT PLAYLISTS" else "${WorkoutDay.fromCode(selectedDayFilter)?.fullName?.uppercase() ?: selectedDayFilter} PLAYLISTS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
          )

          Text(
            text = "${playlists.size} routines",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Empty State
      if (playlists.isEmpty()) {
        item {
          EmptyPlaylistState(
            dayCode = selectedDayFilter,
            onCreate = onCreatePlaylist,
            onRestore = onRestorePresets
          )
        }
      } else {
        items(playlists, key = { it.id }) { playlist ->
          PlaylistItemCard(
            playlist = playlist,
            todayCode = todayDayCode,
            onStart = { onStartWorkout(playlist) },
            onEdit = { onEditPlaylist(playlist) },
            onDuplicate = { onDuplicatePlaylist(playlist) },
            onDelete = { onDeletePlaylist(playlist) }
          )
        }
      }
    }
  }
}

@Composable
private fun DayFilterBar(
  selectedFilter: String,
  todayCode: String,
  onSelectFilter: (String) -> Unit
) {
  val dayOptions = listOf("ALL", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(dayOptions) { day ->
      val isSelected = selectedFilter == day
      val isToday = day == todayCode

      val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      }

      val textColor = when {
        isSelected -> Color.Black
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(bgColor)
          .border(
            width = if (isToday && !isSelected) 1.5.dp else 1.dp,
            color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
            shape = RoundedCornerShape(12.dp)
          )
          .clickable { onSelectFilter(day) }
          .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = day,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected || isToday) FontWeight.ExtraBold else FontWeight.Medium,
            color = textColor
          )
          if (isToday) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.Black else MaterialTheme.colorScheme.primary)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TodayWorkoutHero(
  playlist: WorkoutPlaylist,
  onStartWorkout: () -> Unit,
  onEdit: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(20.dp))
      .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              Color.Transparent
            )
          )
        )
        .padding(18.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.primary)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "TODAY'S SCHEDULED ROUTINE",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.ExtraBold,
              color = Color.Black,
              letterSpacing = 1.sp
            )
          }

          IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Routine",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = playlist.title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        if (playlist.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = playlist.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Badges
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatBadge(label = "TIME", value = playlist.formattedTotalTime)
          StatBadge(label = "STEPS", value = "${playlist.steps.size} intervals")
          StatBadge(label = "SETS", value = "${playlist.totalSetsCount} total sets")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Start Button
        Button(
          onClick = onStartWorkout,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("start_today_workout_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "START TODAY'S WORKOUT",
            fontFamily = ChakraPetchFont,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 1.sp
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlaylistItemCard(
  playlist: WorkoutPlaylist,
  todayCode: String,
  onStart: () -> Unit,
  onEdit: () -> Unit,
  onDuplicate: () -> Unit,
  onDelete: () -> Unit
) {
  var menuExpanded by remember { mutableStateOf(false) }
  val isTodayRoutine = playlist.assignedDays.contains(todayCode) || playlist.assignedDays.contains("DAILY")

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = 1.dp,
        color = if (isTodayRoutine) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        shape = RoundedCornerShape(16.dp)
      ),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Day Tag Badges
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.weight(1f)
        ) {
          playlist.assignedDays.forEach { dayCode ->
            val isCurrentDay = dayCode == todayCode
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isCurrentDay) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, if (isCurrentDay) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = dayCode,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isCurrentDay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }
        }

        // More options dropdown
        Box {
          IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Options",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text("Edit Routine") },
              leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
              onClick = {
                menuExpanded = false
                onEdit()
              }
            )
            DropdownMenuItem(
              text = { Text("Duplicate") },
              leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
              onClick = {
                menuExpanded = false
                onDuplicate()
              }
            )
            DropdownMenuItem(
              text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
              leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
              onClick = {
                menuExpanded = false
                onDelete()
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = playlist.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      if (playlist.description.isNotBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = playlist.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Quick summary stats
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${playlist.formattedTotalTime} • ${playlist.steps.size} steps • ${playlist.totalSetsCount} sets",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
          onClick = onStart,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.Black
          ),
          modifier = Modifier.height(38.dp)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Start", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun StatBadge(label: String, value: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun EmptyPlaylistState(
  dayCode: String,
  onCreate: () -> Unit,
  onRestore: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.FitnessCenter,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(32.dp)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "No Workouts Scheduled for $dayCode",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Create a custom interval routine or restore the default preset workouts.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = onCreate) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Create Routine")
      }

      Button(
        onClick = onRestore,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Restore Presets")
      }
    }
  }
}
