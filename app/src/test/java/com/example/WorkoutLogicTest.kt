package com.example

import com.example.data.WorkoutJsonConverter
import com.example.model.ExpandedInterval
import com.example.model.PhaseType
import com.example.model.WorkoutPlaylist
import com.example.model.WorkoutStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WorkoutLogicTest {

  @Test
  fun testExpandedIntervalQueueWithSetsAndRest() {
    val steps = listOf(
      WorkoutStep(
        label = "Warmup",
        phaseType = PhaseType.WARMUP,
        durationSeconds = 60,
        targetSets = 1,
        targetReps = "-"
      ),
      WorkoutStep(
        label = "Bench Press",
        phaseType = PhaseType.WORK,
        durationSeconds = 45,
        targetSets = 3,
        targetReps = "8 reps",
        restBetweenSetsSeconds = 30
      ),
      WorkoutStep(
        label = "Cooldown",
        phaseType = PhaseType.COOLDOWN,
        durationSeconds = 60,
        targetSets = 1,
        targetReps = "-"
      )
    )

    val playlist = WorkoutPlaylist(
      id = 1,
      title = "Test Routine",
      steps = steps
    )

    val queue = ExpandedInterval.buildIntervalQueue(playlist)

    // Expected intervals:
    // 0: Warmup (60s)
    // 1: Bench Press Set 1 (45s)
    // 2: Rest after Set 1 (30s)
    // 3: Bench Press Set 2 (45s)
    // 4: Rest after Set 2 (30s)
    // 5: Bench Press Set 3 (45s)
    // 6: Cooldown (60s)
    assertEquals(7, queue.size)

    assertEquals(PhaseType.WARMUP, queue[0].phaseType)
    assertEquals("Warmup", queue[0].exerciseName)

    assertEquals(PhaseType.WORK, queue[1].phaseType)
    assertEquals(1, queue[1].currentSet)
    assertEquals(3, queue[1].totalSets)
    assertEquals("8 reps", queue[1].repsTarget)

    assertEquals(PhaseType.REST, queue[2].phaseType)
    assertTrue(queue[2].isRestBetweenSets)
    assertEquals(30, queue[2].durationSeconds)

    assertEquals(PhaseType.WORK, queue[3].phaseType)
    assertEquals(2, queue[3].currentSet)

    assertEquals(PhaseType.WORK, queue[5].phaseType)
    assertEquals(3, queue[5].currentSet)

    assertEquals(PhaseType.COOLDOWN, queue[6].phaseType)
  }

  @Test
  fun testWeightExerciseWithInterSetRestAndStandaloneRest() {
    // 3 sets of 10 reps weights (30s each, 30s rest between sets) + 60s standalone hydration rest
    val steps = listOf(
      WorkoutStep(
        label = "Dumbbell Shoulder Press",
        phaseType = PhaseType.WORK,
        durationSeconds = 30,
        targetSets = 3,
        targetReps = "10 reps",
        restBetweenSetsSeconds = 30
      ),
      WorkoutStep(
        label = "Water & Recovery Break",
        phaseType = PhaseType.REST,
        durationSeconds = 60,
        targetSets = 1,
        targetReps = "-"
      )
    )

    val playlist = WorkoutPlaylist(id = 2, title = "Shoulder & Hydrate", steps = steps)
    val queue = ExpandedInterval.buildIntervalQueue(playlist)

    // Expected sequence:
    // 0: Shoulder Press Set 1 (30s Work)
    // 1: Inter-Set Rest (30s Rest)
    // 2: Shoulder Press Set 2 (30s Work)
    // 3: Inter-Set Rest (30s Rest)
    // 4: Shoulder Press Set 3 (30s Work)
    // 5: Standalone Rest (60s Rest)
    assertEquals(6, queue.size)

    assertEquals("Dumbbell Shoulder Press", queue[0].exerciseName)
    assertEquals(PhaseType.WORK, queue[0].phaseType)
    assertEquals(30, queue[0].durationSeconds)
    assertEquals(1, queue[0].currentSet)
    assertEquals("10 reps", queue[0].repsTarget)

    assertTrue(queue[1].isRestBetweenSets)
    assertEquals(PhaseType.REST, queue[1].phaseType)
    assertEquals(30, queue[1].durationSeconds)

    assertEquals(PhaseType.WORK, queue[2].phaseType)
    assertEquals(2, queue[2].currentSet)

    assertTrue(queue[3].isRestBetweenSets)
    assertEquals(PhaseType.REST, queue[3].phaseType)
    assertEquals(30, queue[3].durationSeconds)

    assertEquals(PhaseType.WORK, queue[4].phaseType)
    assertEquals(3, queue[4].currentSet)

    // Final standalone rest
    assertEquals(PhaseType.REST, queue[5].phaseType)
    assertEquals("Water & Recovery Break", queue[5].exerciseName)
    assertEquals(60, queue[5].durationSeconds)
    assertEquals(false, queue[5].isRestBetweenSets)
  }

  @Test
  fun testWorkoutJsonConverterRoundTrip() {
    val originalSteps = listOf(
      WorkoutStep(
        label = "Squats",
        phaseType = PhaseType.WORK,
        durationSeconds = 50,
        targetSets = 4,
        targetReps = "10 reps",
        restBetweenSetsSeconds = 60,
        notes = "Parallel depth"
      ),
      WorkoutStep(
        label = "Rest",
        phaseType = PhaseType.REST,
        durationSeconds = 90,
        targetSets = 1,
        targetReps = "-",
        restBetweenSetsSeconds = 0,
        notes = "Hydrate"
      )
    )

    val json = WorkoutJsonConverter.stepsToJson(originalSteps)
    assertNotNull(json)
    assertTrue(json.contains("Squats"))

    val parsedSteps = WorkoutJsonConverter.stepsFromJson(json)
    assertEquals(2, parsedSteps.size)
    assertEquals("Squats", parsedSteps[0].label)
    assertEquals(PhaseType.WORK, parsedSteps[0].phaseType)
    assertEquals(4, parsedSteps[0].targetSets)
    assertEquals("10 reps", parsedSteps[0].targetReps)
    assertEquals(60, parsedSteps[0].restBetweenSetsSeconds)
    assertEquals("Parallel depth", parsedSteps[0].notes)
  }
}
