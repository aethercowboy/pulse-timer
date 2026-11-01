package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WorkoutDay
import com.example.model.WorkoutPlaylist
import com.example.model.WorkoutStep
import com.example.ui.components.AddEditStepDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlaylistEditScreen(
  playlist: WorkoutPlaylist?,
  steps: List<WorkoutStep>,
  onAddStep: (WorkoutStep) -> Unit,
  onUpdateStep: (Int, WorkoutStep) -> Unit,
  onDeleteStep: (Int) -> Unit,
  onMoveStepUp: (Int) -> Unit,
  onMoveStepDown: (Int) -> Unit,
  onSave: (title: String, desc: String, days: List<String>, colorHex: String) -> Unit,
  onCancel: () -> Unit
) {
  BackHandler { onCancel() }

  var title by remember { mutableStateOf(playlist?.title ?: "") }
  var description by remember { mutableStateOf(playlist?.description ?: "") }
  var assignedDays by remember { mutableStateOf(playlist?.assignedDays ?: listOf("MON")) }
  var selectedColorHex by remember { mutableStateOf(playlist?.colorHex ?: "#00E676") }

  var dialogStepToEdit by remember { mutableStateOf<Pair<Int, WorkoutStep>?>(null) }
  var isAddingStep by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (playlist == null || playlist.id == 0L) "New Workout Playlist" else "Edit Workout Playlist",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onCancel) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = {
              onSave(title, description, assignedDays, selectedColorHex)
            },
            modifier = Modifier.testTag("save_playlist_top_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = "Save Playlist", tint = MaterialTheme.colorScheme.primary)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Basic Details Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
              value = title,
              onValueChange = { title = it },
              label = { Text("Playlist Title") },
              placeholder = { Text("e.g. Wednesday Leg & Core Blitz") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("playlist_title_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Description & Focus") },
              placeholder = { Text("e.g. Heavy compound sets with strict 45s rest") },
              maxLines = 2,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Assigned Days Selector
            Text(
              text = "SCHEDULE ON DAYS",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              WorkoutDay.entries.forEach { day ->
                val isSelected = assignedDays.contains(day.code)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                    .clickable {
                      assignedDays = if (isSelected) {
                        assignedDays - day.code
                      } else {
                        assignedDays + day.code
                      }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = day.shortLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }

      // Steps Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "WORKOUT INTERVAL STEPS",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              letterSpacing = 1.sp
            )
            val totalSecs = steps.sumOf { it.totalStepDurationSeconds }
            val mins = totalSecs / 60
            val secs = totalSecs % 60
            Text(
              text = "${steps.size} steps • Total: ${mins}m ${secs}s",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Button(
            onClick = { isAddingStep = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("add_step_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Step", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }

      // Steps List
      if (steps.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "No Steps in this Playlist Yet",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap 'Add Step' to add exercises, warmups, or rest intervals.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        itemsIndexed(steps, key = { index, step -> "${step.id}_$index" }) { index, step ->
          val phaseColor = step.phaseType.getAccentColor()

          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Order Number
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${index + 1}",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              // Step Details
              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = step.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(phaseColor.copy(alpha = 0.2f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = step.phaseType.shortLabel,
                      style = MaterialTheme.typography.labelSmall,
                      color = phaseColor,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Sets & Reps & Duration summary
                val setsInfo = when {
                  step.phaseType == com.example.model.PhaseType.REST -> {
                    "Standalone Rest Period • ${step.formattedDuration}"
                  }
                  step.targetSets > 1 && step.restBetweenSetsSeconds > 0 -> {
                    "${step.targetSets} Sets × ${step.targetReps} • ${step.durationSeconds}s Work / ${step.restBetweenSetsSeconds}s Rest"
                  }
                  step.targetSets > 1 -> {
                    "${step.targetSets} Sets × ${step.targetReps} • ${step.durationSeconds}s Work (Continuous)"
                  }
                  else -> {
                    "${step.formattedDuration} Work • ${step.targetReps}"
                  }
                }

                Text(
                  text = setsInfo,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Reorder & Action buttons
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { onMoveStepUp(index) },
                  enabled = index > 0,
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    Icons.Default.ArrowUpward,
                    contentDescription = "Move Up",
                    tint = if (index > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.size(16.dp)
                  )
                }

                IconButton(
                  onClick = { onMoveStepDown(index) },
                  enabled = index < steps.size - 1,
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = "Move Down",
                    tint = if (index < steps.size - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    modifier = Modifier.size(16.dp)
                  )
                }

                IconButton(
                  onClick = { dialogStepToEdit = Pair(index, step) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Step",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                }

                IconButton(
                  onClick = { onDeleteStep(index) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Step",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Bottom Save & Cancel Buttons
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
          ) {
            Text("Cancel")
          }

          Button(
            onClick = { onSave(title, description, assignedDays, selectedColorHex) },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = Color.Black
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("save_playlist_bottom_button")
          ) {
            Text("Save Routine", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }

  // Add Step Dialog
  if (isAddingStep) {
    AddEditStepDialog(
      initialStep = null,
      onSave = { newStep ->
        onAddStep(newStep)
        isAddingStep = false
      },
      onDismiss = { isAddingStep = false }
    )
  }

  // Edit Step Dialog
  dialogStepToEdit?.let { (idx, step) ->
    AddEditStepDialog(
      initialStep = step,
      onSave = { updatedStep ->
        onUpdateStep(idx, updatedStep)
        dialogStepToEdit = null
      },
      onDismiss = { dialogStepToEdit = null }
    )
  }
}
