package com.example.model

data class ExpandedInterval(
  val index: Int,
  val totalIntervals: Int,
  val stepIndex: Int,
  val totalSteps: Int,
  val stepId: String,
  val exerciseName: String,
  val intervalLabel: String,
  val phaseType: PhaseType,
  val durationSeconds: Int,
  val currentSet: Int,
  val totalSets: Int,
  val repsTarget: String,
  val notes: String,
  val isRestBetweenSets: Boolean
) {
  companion object {
    fun buildIntervalQueue(playlist: WorkoutPlaylist): List<ExpandedInterval> {
      val rawSteps = playlist.steps
      val result = mutableListOf<ExpandedInterval>()

      var globalIndex = 0

      rawSteps.forEachIndexed { sIdx, step ->
        val setsCount = maxOf(1, step.targetSets)

        for (setNum in 1..setsCount) {
          // Add Work/Exercise interval for this set
          result.add(
            ExpandedInterval(
              index = globalIndex++,
              totalIntervals = 0, // patched later
              stepIndex = sIdx + 1,
              totalSteps = rawSteps.size,
              stepId = step.id,
              exerciseName = step.label,
              intervalLabel = if (step.phaseType == PhaseType.WORK) {
                if (setsCount > 1) "${step.label} • Set $setNum" else step.label
              } else {
                step.label
              },
              phaseType = step.phaseType,
              durationSeconds = maxOf(1, step.durationSeconds),
              currentSet = setNum,
              totalSets = setsCount,
              repsTarget = step.targetReps,
              notes = step.notes,
              isRestBetweenSets = false
            )
          )

          // If there is another set and rest between sets is configured, insert Rest interval
          if (setNum < setsCount && step.restBetweenSetsSeconds > 0) {
            result.add(
              ExpandedInterval(
                index = globalIndex++,
                totalIntervals = 0,
                stepIndex = sIdx + 1,
                totalSteps = rawSteps.size,
                stepId = step.id,
                exerciseName = "${step.label} • Rest",
                intervalLabel = "Rest after Set $setNum (${step.restBetweenSetsSeconds}s)",
                phaseType = PhaseType.REST,
                durationSeconds = step.restBetweenSetsSeconds,
                currentSet = setNum,
                totalSets = setsCount,
                repsTarget = "Next: Set ${setNum + 1} (${step.targetReps})",
                notes = if (step.notes.isNotBlank()) "${step.notes} • Prepare for Set ${setNum + 1}" else "Catch breath before Set ${setNum + 1} of $setsCount",
                isRestBetweenSets = true
              )
            )
          }
        }
      }

      val total = result.size
      return result.mapIndexed { i, interval ->
        interval.copy(index = i, totalIntervals = total)
      }
    }
  }
}
