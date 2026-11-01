package com.example.model

import java.util.UUID

data class WorkoutStep(
  val id: String = UUID.randomUUID().toString(),
  val label: String,
  val phaseType: PhaseType = PhaseType.WORK,
  val durationSeconds: Int = 45,
  val targetSets: Int = 1,
  val targetReps: String = "10 reps",
  val restBetweenSetsSeconds: Int = 30,
  val notes: String = ""
) {
  val formattedDuration: String
    get() {
      val mins = durationSeconds / 60
      val secs = durationSeconds % 60
      return if (mins > 0) {
        if (secs > 0) "${mins}m ${secs}s" else "${mins}m"
      } else {
        "${secs}s"
      }
    }

  val totalStepDurationSeconds: Int
    get() {
      return if (targetSets > 1) {
        (durationSeconds * targetSets) + (restBetweenSetsSeconds * (targetSets - 1))
      } else {
        durationSeconds
      }
    }
}
