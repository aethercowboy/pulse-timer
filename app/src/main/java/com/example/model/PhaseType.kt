package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonCooldownViolet
import com.example.ui.theme.NeonRestCyan
import com.example.ui.theme.NeonWarmupAmber
import com.example.ui.theme.NeonWorkGreen

enum class PhaseType(
  val displayName: String,
  val shortLabel: String
) {
  WARMUP("Warmup", "WARMUP"),
  WORK("Work / Exercise", "WORK"),
  REST("Rest Period", "REST"),
  COOLDOWN("Cooldown", "COOL");

  fun getAccentColor(): Color {
    return when (this) {
      WARMUP -> NeonWarmupAmber
      WORK -> NeonWorkGreen
      REST -> NeonRestCyan
      COOLDOWN -> NeonCooldownViolet
    }
  }
}
