package com.example.data

import com.example.model.PhaseType
import com.example.model.WorkoutPlaylist
import com.example.model.WorkoutStep

object DefaultWorkouts {

  fun getPresets(): List<WorkoutPlaylist> {
    return listOf(
      // Monday: Chest & Triceps Push
      WorkoutPlaylist(
        id = 1,
        title = "Monday: Chest & Triceps Power",
        description = "Heavy push compound movements with controlled rest intervals and tricep burnout.",
        assignedDays = listOf("MON"),
        colorHex = "#00E676", // Neon Green
        isPreset = true,
        steps = listOf(
          WorkoutStep(
            label = "Dynamic Chest & Shoulder Warmup",
            phaseType = PhaseType.WARMUP,
            durationSeconds = 120,
            targetSets = 1,
            targetReps = "Continuous",
            restBetweenSetsSeconds = 0,
            notes = "Arm circles, band pull-aparts, light pushups"
          ),
          WorkoutStep(
            label = "Barbell Bench Press",
            phaseType = PhaseType.WORK,
            durationSeconds = 45,
            targetSets = 4,
            targetReps = "8 reps",
            restBetweenSetsSeconds = 60,
            notes = "Focus on explosive drive and 2s eccentric lowering"
          ),
          WorkoutStep(
            label = "Incline Dumbbell Press",
            phaseType = PhaseType.WORK,
            durationSeconds = 40,
            targetSets = 3,
            targetReps = "10 reps",
            restBetweenSetsSeconds = 45,
            notes = "Squeeze upper pecs at peak contraction"
          ),
          WorkoutStep(
            label = "Mid-Workout Hydration Rest",
            phaseType = PhaseType.REST,
            durationSeconds = 60,
            targetSets = 1,
            targetReps = "-",
            restBetweenSetsSeconds = 0,
            notes = "Sip water and shake out shoulders"
          ),
          WorkoutStep(
            label = "Cable Chest Flyes",
            phaseType = PhaseType.WORK,
            durationSeconds = 35,
            targetSets = 3,
            targetReps = "12 reps",
            restBetweenSetsSeconds = 40,
            notes = "Slight bend in elbows, huge stretch across chest"
          ),
          WorkoutStep(
            label = "Tricep Rope Pushdowns",
            phaseType = PhaseType.WORK,
            durationSeconds = 30,
            targetSets = 3,
            targetReps = "15 reps",
            restBetweenSetsSeconds = 35,
            notes = "Spread ropes at bottom lockout"
          ),
          WorkoutStep(
            label = "Upper Body Cooldown & Pec Stretch",
            phaseType = PhaseType.COOLDOWN,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "Deep Breaths",
            restBetweenSetsSeconds = 0,
            notes = "Doorway pec stretch and overhead tricep stretch"
          )
        )
      ),

      // Tuesday: HIIT Cardio & Core Blast
      WorkoutPlaylist(
        id = 2,
        title = "Tuesday: HIIT Cardio & Core Blast",
        description = "High-octane interval circuits designed to torch calories and build abdominal endurance.",
        assignedDays = listOf("TUE"),
        colorHex = "#FF9100", // Neon Amber
        isPreset = true,
        steps = listOf(
          WorkoutStep(
            label = "Jump Rope / Light Jog Warmup",
            phaseType = PhaseType.WARMUP,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "Steady cadence",
            restBetweenSetsSeconds = 0,
            notes = "Elevate heart rate and loosen ankles"
          ),
          WorkoutStep(
            label = "Burpees to Tuck Jump",
            phaseType = PhaseType.WORK,
            durationSeconds = 40,
            targetSets = 3,
            targetReps = "Max reps (AMRAP)",
            restBetweenSetsSeconds = 25,
            notes = "Full chest to floor, explode upward"
          ),
          WorkoutStep(
            label = "High Knees Sprint",
            phaseType = PhaseType.WORK,
            durationSeconds = 30,
            targetSets = 3,
            targetReps = "Max speed",
            restBetweenSetsSeconds = 20,
            notes = "Drive knees up to hip height"
          ),
          WorkoutStep(
            label = "Active Recovery Walk",
            phaseType = PhaseType.REST,
            durationSeconds = 45,
            targetSets = 1,
            targetReps = "-",
            restBetweenSetsSeconds = 0,
            notes = "Deep nasal breathing, lower heart rate"
          ),
          WorkoutStep(
            label = "Mountain Climbers",
            phaseType = PhaseType.WORK,
            durationSeconds = 35,
            targetSets = 3,
            targetReps = "Fast pace",
            restBetweenSetsSeconds = 25,
            notes = "Keep hips level, core braced"
          ),
          WorkoutStep(
            label = "Hollow Body Rock to Plank Hold",
            phaseType = PhaseType.WORK,
            durationSeconds = 45,
            targetSets = 3,
            targetReps = "Hold",
            restBetweenSetsSeconds = 30,
            notes = "Lock ribs down, squeeze glutes"
          ),
          WorkoutStep(
            label = "Child's Pose & Deep Breathing",
            phaseType = PhaseType.COOLDOWN,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "Slow breathing",
            restBetweenSetsSeconds = 0,
            notes = "Lengthen spine and calm central nervous system"
          )
        )
      ),

      // Wednesday: Back & Biceps Hypertrophy
      WorkoutPlaylist(
        id = 3,
        title = "Wednesday: Back & Biceps Pull",
        description = "Lats, rhomboids, rear delts, and bicep peak building with targeted rest periods.",
        assignedDays = listOf("WED"),
        colorHex = "#00E5FF", // Neon Cyan
        isPreset = true,
        steps = listOf(
          WorkoutStep(
            label = "Scapular Pulls & Dynamic Warmup",
            phaseType = PhaseType.WARMUP,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "15 reps",
            restBetweenSetsSeconds = 0,
            notes = "Activate lats and scapular depressors"
          ),
          WorkoutStep(
            label = "Wide Grip Pull-Ups",
            phaseType = PhaseType.WORK,
            durationSeconds = 40,
            targetSets = 4,
            targetReps = "8-10 reps",
            restBetweenSetsSeconds = 60,
            notes = "Pull elbows down to hips, chin over bar"
          ),
          WorkoutStep(
            label = "Bent-Over Barbell Rows",
            phaseType = PhaseType.WORK,
            durationSeconds = 45,
            targetSets = 4,
            targetReps = "10 reps",
            restBetweenSetsSeconds = 50,
            notes = "Pull bar to navel, flat back"
          ),
          WorkoutStep(
            label = "Seated Cable Rows",
            phaseType = PhaseType.WORK,
            durationSeconds = 40,
            targetSets = 3,
            targetReps = "12 reps",
            restBetweenSetsSeconds = 40,
            notes = "Pause 1 sec at sternum"
          ),
          WorkoutStep(
            label = "Incline Dumbbell Bicep Curls",
            phaseType = PhaseType.WORK,
            durationSeconds = 35,
            targetSets = 3,
            targetReps = "12 reps",
            restBetweenSetsSeconds = 35,
            notes = "Full stretch on long head of biceps"
          ),
          WorkoutStep(
            label = "Hammer Curls Burnout",
            phaseType = PhaseType.WORK,
            durationSeconds = 30,
            targetSets = 3,
            targetReps = "15 reps",
            restBetweenSetsSeconds = 30,
            notes = "Brachialis and forearm builder"
          ),
          WorkoutStep(
            label = "Lat Hang & Arm Stretch",
            phaseType = PhaseType.COOLDOWN,
            durationSeconds = 60,
            targetSets = 1,
            targetReps = "Hold",
            restBetweenSetsSeconds = 0,
            notes = "Decompress spine on pullup bar"
          )
        )
      ),

      // Thursday: Active Recovery & Mobility
      WorkoutPlaylist(
        id = 4,
        title = "Thursday: Mobility & Active Recovery",
        description = "Joint mobility, hip openers, and restorative flow to prepare for heavy leg day.",
        assignedDays = listOf("THU"),
        colorHex = "#B388FF", // Neon Violet
        isPreset = true,
        steps = listOf(
          WorkoutStep(
            label = "World's Greatest Stretch (L & R)",
            phaseType = PhaseType.WARMUP,
            durationSeconds = 90,
            targetSets = 2,
            targetReps = "6 per side",
            restBetweenSetsSeconds = 20,
            notes = "Lunge, thoracic reach to sky, hamstring stretch"
          ),
          WorkoutStep(
            label = "90/90 Hip Mobility Flow",
            phaseType = PhaseType.WORK,
            durationSeconds = 60,
            targetSets = 3,
            targetReps = "Smooth transitions",
            restBetweenSetsSeconds = 20,
            notes = "Internal and external hip rotation"
          ),
          WorkoutStep(
            label = "Cat-Cow & Thoracic Extension",
            phaseType = PhaseType.WORK,
            durationSeconds = 45,
            targetSets = 2,
            targetReps = "10 slow reps",
            restBetweenSetsSeconds = 15,
            notes = "Synchronize with deep inhalation and exhalation"
          ),
          WorkoutStep(
            label = "Deep Asian Squat Hold",
            phaseType = PhaseType.WORK,
            durationSeconds = 60,
            targetSets = 2,
            targetReps = "Hold",
            restBetweenSetsSeconds = 30,
            notes = "Chest proud, push knees out with elbows"
          ),
          WorkoutStep(
            label = "Supine Spinal Twist & Relax",
            phaseType = PhaseType.COOLDOWN,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "Hold both sides",
            restBetweenSetsSeconds = 0,
            notes = "Full body decompression"
          )
        )
      ),

      // Friday: Legs & Abs Crusher
      WorkoutPlaylist(
        id = 5,
        title = "Friday: Legs & Core Crusher",
        description = "Quad, hamstring, and glute hypertrophy paired with intense anti-rotation core work.",
        assignedDays = listOf("FRI"),
        colorHex = "#FF5252", // Neon Coral
        isPreset = true,
        steps = listOf(
          WorkoutStep(
            label = "Leg Swings & Glute Bridges",
            phaseType = PhaseType.WARMUP,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "Dynamic",
            restBetweenSetsSeconds = 0,
            notes = "Warm up hip joints and fire glutes"
          ),
          WorkoutStep(
            label = "Barbell Back Squats",
            phaseType = PhaseType.WORK,
            durationSeconds = 50,
            targetSets = 4,
            targetReps = "8 reps",
            restBetweenSetsSeconds = 75,
            notes = "Parallel depth, drive heels into floor"
          ),
          WorkoutStep(
            label = "Romanian Deadlifts (RDL)",
            phaseType = PhaseType.WORK,
            durationSeconds = 45,
            targetSets = 3,
            targetReps = "10 reps",
            restBetweenSetsSeconds = 60,
            notes = "Hinge hips back, feel intense hamstring stretch"
          ),
          WorkoutStep(
            label = "Walking Dumbbell Lunges",
            phaseType = PhaseType.WORK,
            durationSeconds = 45,
            targetSets = 3,
            targetReps = "20 steps",
            restBetweenSetsSeconds = 45,
            notes = "90 degree knee angles, keep torso upright"
          ),
          WorkoutStep(
            label = "Standing Calf Raises",
            phaseType = PhaseType.WORK,
            durationSeconds = 35,
            targetSets = 3,
            targetReps = "15 reps",
            restBetweenSetsSeconds = 30,
            notes = "2s pause at top contraction"
          ),
          WorkoutStep(
            label = "Hanging Leg Raises",
            phaseType = PhaseType.WORK,
            durationSeconds = 35,
            targetSets = 3,
            targetReps = "12 reps",
            restBetweenSetsSeconds = 35,
            notes = "No swinging, curl pelvis toward chest"
          ),
          WorkoutStep(
            label = "Quad & Hamstring Stretch Cooldown",
            phaseType = PhaseType.COOLDOWN,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "Hold",
            restBetweenSetsSeconds = 0,
            notes = "Flush lactic acid and promote leg recovery"
          )
        )
      ),

      // Saturday: Full Body Tabata Gauntlet
      WorkoutPlaylist(
        id = 6,
        title = "Saturday: Tabata Protocol Circuit",
        description = "High-intensity 20s work / 10s rest classic Tabata protocol hitting the whole body.",
        assignedDays = listOf("SAT"),
        colorHex = "#FFD600", // Gold
        isPreset = true,
        steps = listOf(
          WorkoutStep(
            label = "Jumping Jacks Warmup",
            phaseType = PhaseType.WARMUP,
            durationSeconds = 60,
            targetSets = 1,
            targetReps = "Light rhythm",
            restBetweenSetsSeconds = 0,
            notes = "Shake out limbs"
          ),
          WorkoutStep(
            label = "Tabata Round: Jump Squats",
            phaseType = PhaseType.WORK,
            durationSeconds = 20,
            targetSets = 4,
            targetReps = "Max reps",
            restBetweenSetsSeconds = 10,
            notes = "20s all-out, 10s breathe"
          ),
          WorkoutStep(
            label = "Tabata Round: Explosive Pushups",
            phaseType = PhaseType.WORK,
            durationSeconds = 20,
            targetSets = 4,
            targetReps = "Max reps",
            restBetweenSetsSeconds = 10,
            notes = "Fast tempo, strong push"
          ),
          WorkoutStep(
            label = "Halfway Hydration Break",
            phaseType = PhaseType.REST,
            durationSeconds = 40,
            targetSets = 1,
            targetReps = "-",
            restBetweenSetsSeconds = 0,
            notes = "Catch breath, prepare for round 2"
          ),
          WorkoutStep(
            label = "Tabata Round: Kettlebell Swings",
            phaseType = PhaseType.WORK,
            durationSeconds = 20,
            targetSets = 4,
            targetReps = "Max reps",
            restBetweenSetsSeconds = 10,
            notes = "Hip snap power"
          ),
          WorkoutStep(
            label = "Tabata Round: Bicycle Crunches",
            phaseType = PhaseType.WORK,
            durationSeconds = 20,
            targetSets = 4,
            targetReps = "Max reps",
            restBetweenSetsSeconds = 10,
            notes = "Opposite elbow to opposite knee"
          ),
          WorkoutStep(
            label = "Cooldown Walk & Full Body Stretch",
            phaseType = PhaseType.COOLDOWN,
            durationSeconds = 90,
            targetSets = 1,
            targetReps = "Slow down",
            restBetweenSetsSeconds = 0,
            notes = "Heart rate down, rehydrate"
          )
        )
      ),

      // Sunday: Rest, Flow & Mindfulness
      WorkoutPlaylist(
        id = 7,
        title = "Sunday: Zen Stretch & Recharge",
        description = "Gentle restorative flow to reset muscles, tendons, and mental focus for the week ahead.",
        assignedDays = listOf("SUN"),
        colorHex = "#26C6DA", // Seafoam
        isPreset = true,
        steps = listOf(
          WorkoutStep(
            label = "Diaphragmatic Box Breathing",
            phaseType = PhaseType.WARMUP,
            durationSeconds = 120,
            targetSets = 1,
            targetReps = "4-4-4-4 rhythm",
            restBetweenSetsSeconds = 0,
            notes = "Inhale 4s, hold 4s, exhale 4s, hold 4s"
          ),
          WorkoutStep(
            label = "Cobra Pose to Downward Dog",
            phaseType = PhaseType.WORK,
            durationSeconds = 60,
            targetSets = 3,
            targetReps = "Slow flow",
            restBetweenSetsSeconds = 20,
            notes = "Pedal calves and open anterior chain"
          ),
          WorkoutStep(
            label = "Seated Forward Fold Hamstring Stretch",
            phaseType = PhaseType.WORK,
            durationSeconds = 60,
            targetSets = 2,
            targetReps = "Gentle hold",
            restBetweenSetsSeconds = 15,
            notes = "Relax head toward shins"
          ),
          WorkoutStep(
            label = "Pigeon Pose Hip Opener",
            phaseType = PhaseType.WORK,
            durationSeconds = 60,
            targetSets = 2,
            targetReps = "1 set per side",
            restBetweenSetsSeconds = 15,
            notes = "Deep glute and piriformis release"
          ),
          WorkoutStep(
            label = "Savasana / Total Relaxation",
            phaseType = PhaseType.COOLDOWN,
            durationSeconds = 120,
            targetSets = 1,
            targetReps = "Rest",
            restBetweenSetsSeconds = 0,
            notes = "Close eyes and let muscles recover completely"
          )
        )
      )
    )
  }
}
