package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.model.PhaseType
import com.example.model.WorkoutStep
import com.example.ui.theme.NeonRestCyan
import com.example.ui.theme.NeonWorkGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditStepDialog(
  initialStep: WorkoutStep?,
  onSave: (WorkoutStep) -> Unit,
  onDismiss: () -> Unit
) {
  var label by remember { mutableStateOf(initialStep?.label ?: "") }
  var phaseType by remember { mutableStateOf(initialStep?.phaseType ?: PhaseType.WORK) }
  var durationSeconds by remember { mutableIntStateOf(initialStep?.durationSeconds ?: 30) }
  var targetSets by remember { mutableIntStateOf(initialStep?.targetSets ?: 1) }
  var targetReps by remember { mutableStateOf(initialStep?.targetReps ?: "10 reps") }
  var restBetweenSetsSeconds by remember { mutableIntStateOf(initialStep?.restBetweenSetsSeconds ?: 30) }
  var isRestBetweenSetsEnabled by remember {
    mutableStateOf(initialStep?.let { it.targetSets > 1 && it.restBetweenSetsSeconds > 0 } ?: true)
  }
  var notes by remember { mutableStateOf(initialStep?.notes ?: "") }

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
          Text(
            text = if (initialStep == null) "Add Workout Step" else "Edit Step",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phase Type Selector
        Text(
          text = "STEP TYPE",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          PhaseType.entries.forEach { phase ->
            val isSelected = phase == phaseType
            val accent = phase.getAccentColor()
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) accent.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) accent else Color.Transparent, RoundedCornerShape(10.dp))
                .clickable {
                  phaseType = phase
                  if (phase == PhaseType.REST && label.isBlank()) {
                    label = "Rest & Hydrate"
                  }
                }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = phase.shortLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step Label
        OutlinedTextField(
          value = label,
          onValueChange = { label = it },
          label = {
            Text(if (phaseType == PhaseType.REST) "Rest Period Label" else "Exercise / Step Label")
          },
          placeholder = {
            Text(if (phaseType == PhaseType.REST) "e.g. Water Break, Station Rest" else "e.g. Dumbbell Bench Press, Squats, Plank")
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("step_label_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Case 1: Standalone Rest Step
        if (phaseType == PhaseType.REST) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NeonRestCyan.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bedtime, contentDescription = null, tint = NeonRestCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "STANDALONE REST DURATION",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = NeonRestCyan,
                  letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                  text = "${durationSeconds}s",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = NeonRestCyan
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                listOf(15, 30, 45, 60, 90, 120, 180).forEach { sec ->
                  val isSel = durationSeconds == sec
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSel) NeonRestCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                      .border(1.dp, if (isSel) NeonRestCyan else Color.Transparent, RoundedCornerShape(8.dp))
                      .clickable { durationSeconds = sec }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = if (sec >= 60) "${sec / 60}m" else "${sec}s",
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = if (isSel) NeonRestCyan else MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }

              // Stepper
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
              ) {
                IconButton(
                  onClick = { durationSeconds = (durationSeconds - 5).coerceAtLeast(5) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Remove, contentDescription = "Decrease 5s")
                }
                Text(
                  text = "± 5s",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                  onClick = { durationSeconds += 5 },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Increase 5s")
                }
              }
            }
          }
        } else {
          // Case 2: Work/Exercise Step (with Sets, Reps, and Optional Inter-Set Rest)

          // 1. Work Duration per Set
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "WORK DURATION (PER SET)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
              )
              Text(
                text = "Time spent performing each set",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            Text(
              text = if (mins > 0) "${mins}m ${secs}s" else "${secs}s",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(15, 20, 30, 45, 60, 90, 120).forEach { sec ->
              val isSel = durationSeconds == sec
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                  .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                  .clickable { durationSeconds = sec }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "${sec}s",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { durationSeconds = (durationSeconds - 5).coerceAtLeast(5) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease 5s")
            }
            Text(
              text = "± 5s",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(
              onClick = { durationSeconds += 5 },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase 5s")
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 2. Target Sets Counter & Quick Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "TARGET SETS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
              )
              Text(
                text = "Rounds to repeat this exercise",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              IconButton(
                onClick = { targetSets = (targetSets - 1).coerceAtLeast(1) },
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant)
              ) {
                Icon(Icons.Default.Remove, contentDescription = "Minus set", modifier = Modifier.size(18.dp))
              }

              Text(
                text = "$targetSets",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(28.dp),
                fontSize = 20.sp
              )

              IconButton(
                onClick = {
                  targetSets = (targetSets + 1).coerceAtMost(20)
                  if (targetSets > 1) isRestBetweenSetsEnabled = true
                },
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant)
              ) {
                Icon(Icons.Default.Add, contentDescription = "Plus set", modifier = Modifier.size(18.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Quick Sets Preset Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(1, 2, 3, 4, 5).forEach { s ->
              val isSel = targetSets == s
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                  .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                  .clickable {
                    targetSets = s
                    if (s > 1) isRestBetweenSetsEnabled = true
                  }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (s == 1) "1 Set" else "$s Sets",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3. Target Reps
          OutlinedTextField(
            value = targetReps,
            onValueChange = { targetReps = it },
            label = { Text("Target Reps (per set)") },
            placeholder = { Text("e.g. 10 reps, 12 reps, AMRAP, Failure") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("step_reps_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("8 reps", "10 reps", "12 reps", "15 reps", "AMRAP", "To Failure", "Hold").forEach { preset ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                  .clickable { targetReps = preset }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = preset,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 4. Dedicated "REST BETWEEN SETS" Configuration Box
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
              containerColor = if (isRestBetweenSetsEnabled && targetSets > 1) {
                NeonRestCyan.copy(alpha = 0.08f)
              } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
              }
            ),
            shape = RoundedCornerShape(14.dp),
            border = if (isRestBetweenSetsEnabled && targetSets > 1) {
              androidx.compose.foundation.BorderStroke(1.dp, NeonRestCyan.copy(alpha = 0.35f))
            } else null
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Bedtime,
                      contentDescription = null,
                      tint = if (isRestBetweenSetsEnabled && targetSets > 1) NeonRestCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "REST BETWEEN SETS",
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = if (isRestBetweenSetsEnabled && targetSets > 1) NeonRestCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                      letterSpacing = 1.sp
                    )
                  }
                  Text(
                    text = if (targetSets > 1) "Optional rest interval between each set" else "Add multiple sets above to enable inter-set rest",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                if (targetSets > 1) {
                  Switch(
                    checked = isRestBetweenSetsEnabled,
                    onCheckedChange = { isRestBetweenSetsEnabled = it },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = NeonRestCyan,
                      checkedTrackColor = NeonRestCyan.copy(alpha = 0.35f)
                    )
                  )
                }
              }

              if (targetSets > 1 && isRestBetweenSetsEnabled) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Rest Duration:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Text(
                    text = "${restBetweenSetsSeconds}s rest",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonRestCyan
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf(15, 20, 30, 45, 60, 90, 120).forEach { sec ->
                    val isSel = restBetweenSetsSeconds == sec
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) NeonRestCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, if (isSel) NeonRestCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { restBetweenSetsSeconds = sec }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                      Text(
                        text = "${sec}s",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) NeonRestCyan else MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                }

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  IconButton(
                    onClick = { restBetweenSetsSeconds = (restBetweenSetsSeconds - 5).coerceAtLeast(5) },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(Icons.Default.Remove, contentDescription = "Minus 5s rest")
                  }
                  Text(
                    text = "± 5s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                  )
                  IconButton(
                    onClick = { restBetweenSetsSeconds += 5 },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = "Plus 5s rest")
                  }
                }
              }
            }
          }

          // Live Schedule Breakdown Preview
          if (targetSets > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            val effectiveRest = if (isRestBetweenSetsEnabled) restBetweenSetsSeconds else 0
            val totalSecs = (durationSeconds * targetSets) + (effectiveRest * (targetSets - 1))
            val tMins = totalSecs / 60
            val tSecs = totalSecs % 60

            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "SCHEDULE BREAKDOWN",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = if (effectiveRest > 0) {
                    "$targetSets sets × ${durationSeconds}s work + ${effectiveRest}s rest between sets"
                  } else {
                    "$targetSets sets × ${durationSeconds}s work (continuous sets)"
                  },
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Total step duration: ${if (tMins > 0) "${tMins}m ${tSecs}s" else "${tSecs}s"}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Notes & Coaching Cues
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes & Coaching Cues (Optional)") },
          placeholder = { Text("e.g. 10 reps of weights, slow descent") },
          singleLine = false,
          maxLines = 2,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
          ) {
            Text("Cancel")
          }

          Button(
            onClick = {
              val validLabel = label.ifBlank {
                when (phaseType) {
                  PhaseType.REST -> "Rest Period"
                  PhaseType.WARMUP -> "Warmup"
                  PhaseType.COOLDOWN -> "Cooldown"
                  PhaseType.WORK -> "Exercise"
                }
              }
              val finalRestSeconds = if (phaseType == PhaseType.WORK && targetSets > 1 && isRestBetweenSetsEnabled) {
                restBetweenSetsSeconds
              } else {
                0
              }

              val step = WorkoutStep(
                id = initialStep?.id ?: java.util.UUID.randomUUID().toString(),
                label = validLabel,
                phaseType = phaseType,
                durationSeconds = durationSeconds,
                targetSets = if (phaseType == PhaseType.REST) 1 else targetSets,
                targetReps = if (phaseType == PhaseType.REST) "-" else targetReps,
                restBetweenSetsSeconds = finalRestSeconds,
                notes = notes
              )
              onSave(step)
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = Color.Black
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("save_step_button")
          ) {
            Text("Save Step", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
