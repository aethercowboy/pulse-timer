package com.example.model

data class WorkoutPlaylist(
  val id: Long = 0,
  val title: String,
  val description: String = "",
  val assignedDays: List<String> = emptyList(), // e.g. ["MON", "WED", "FRI"]
  val steps: List<WorkoutStep> = emptyList(),
  val colorHex: String = "#00E676",
  val isPreset: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
) {
  val totalDurationSeconds: Int
    get() = steps.sumOf { it.totalStepDurationSeconds }

  val formattedTotalTime: String
    get() {
      val totalSecs = totalDurationSeconds
      val mins = totalSecs / 60
      val secs = totalSecs % 60
      return if (mins > 0) {
        if (secs > 0) "${mins} min ${secs} sec" else "${mins} min"
      } else {
        "${secs} sec"
      }
    }

  val totalSetsCount: Int
    get() = steps.sumOf { it.targetSets }
}

enum class WorkoutDay(
  val code: String,
  val fullName: String,
  val shortLabel: String
) {
  MON("MON", "Monday", "Mon"),
  TUE("TUE", "Tuesday", "Tue"),
  WED("WED", "Wednesday", "Wed"),
  THU("THU", "Thursday", "Thu"),
  FRI("FRI", "Friday", "Fri"),
  SAT("SAT", "Saturday", "Sat"),
  SUN("SUN", "Sunday", "Sun"),
  DAILY("DAILY", "Everyday", "Daily");

  companion object {
    fun fromCode(code: String): WorkoutDay? = entries.find { it.code.equals(code, ignoreCase = true) }
  }
}
