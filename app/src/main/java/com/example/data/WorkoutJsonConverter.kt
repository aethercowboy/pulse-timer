package com.example.data

import com.example.model.PhaseType
import com.example.model.WorkoutStep
import org.json.JSONArray
import org.json.JSONObject

object WorkoutJsonConverter {

  fun stepsToJson(steps: List<WorkoutStep>): String {
    val array = JSONArray()
    for (step in steps) {
      val obj = JSONObject().apply {
        put("id", step.id)
        put("label", step.label)
        put("phaseType", step.phaseType.name)
        put("durationSeconds", step.durationSeconds)
        put("targetSets", step.targetSets)
        put("targetReps", step.targetReps)
        put("restBetweenSetsSeconds", step.restBetweenSetsSeconds)
        put("notes", step.notes)
      }
      array.put(obj)
    }
    return array.toString()
  }

  fun stepsFromJson(json: String?): List<WorkoutStep> {
    if (json.isNullOrEmpty()) return emptyList()
    val list = mutableListOf<WorkoutStep>()
    try {
      val array = JSONArray(json)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val phaseName = obj.optString("phaseType", PhaseType.WORK.name)
        val phase = try {
          PhaseType.valueOf(phaseName)
        } catch (_: Exception) {
          PhaseType.WORK
        }

        list.add(
          WorkoutStep(
            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
            label = obj.optString("label", "Exercise"),
            phaseType = phase,
            durationSeconds = obj.optInt("durationSeconds", 45),
            targetSets = obj.optInt("targetSets", 1),
            targetReps = obj.optString("targetReps", "10 reps"),
            restBetweenSetsSeconds = obj.optInt("restBetweenSetsSeconds", 30),
            notes = obj.optString("notes", "")
          )
        )
      }
    } catch (_: Exception) {
      // Return empty if parsing error
    }
    return list
  }
}
