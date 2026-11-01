package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.WorkoutPlaylist

@Entity(tableName = "workout_playlists")
data class WorkoutEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val description: String,
  val assignedDays: String, // comma-separated e.g. "MON,WED,FRI"
  val stepsJson: String,
  val colorHex: String,
  val isPreset: Boolean,
  val createdAt: Long
) {
  fun toDomain(): WorkoutPlaylist {
    return WorkoutPlaylist(
      id = id,
      title = title,
      description = description,
      assignedDays = if (assignedDays.isNotBlank()) assignedDays.split(",").map { it.trim() } else emptyList(),
      steps = WorkoutJsonConverter.stepsFromJson(stepsJson),
      colorHex = colorHex,
      isPreset = isPreset,
      createdAt = createdAt
    )
  }

  companion object {
    fun fromDomain(playlist: WorkoutPlaylist): WorkoutEntity {
      return WorkoutEntity(
        id = playlist.id,
        title = playlist.title,
        description = playlist.description,
        assignedDays = playlist.assignedDays.joinToString(","),
        stepsJson = WorkoutJsonConverter.stepsToJson(playlist.steps),
        colorHex = playlist.colorHex,
        isPreset = playlist.isPreset,
        createdAt = playlist.createdAt
      )
    }
  }
}
