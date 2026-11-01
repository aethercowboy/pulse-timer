package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

enum class WorkoutToneType {
  REST,
  NEXT_SET,
  NEXT_PHASE,
  DONE,
  COUNTDOWN_TICK
}

class ToneManager(private val context: Context) {

  private val coroutineScope = CoroutineScope(Dispatchers.Default)

  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  // Pre-computed audio buffers for zero-latency playback
  private val restBuffer: ShortArray by lazy { generateTwoToneWave(523.25, 392.0, 220, 350) }
  private val nextSetBuffer: ShortArray by lazy { generateDoubleBeepWave(880.0, 1046.5, 110, 60, 150) }
  private val nextPhaseBuffer: ShortArray by lazy { generateTripleAscendingWave(587.33, 739.99, 880.0, 90, 90, 220) }
  private val doneBuffer: ShortArray by lazy { generateFanfareWave(523.25, 659.25, 783.99, 1046.50, 120, 120, 140, 400) }
  private val tickBuffer: ShortArray by lazy { generateToneWave(659.25, 60, 0.4) }

  var soundEnabled: Boolean = true
  var vibrationEnabled: Boolean = true
  var countdownTicksEnabled: Boolean = true

  fun playTone(toneType: WorkoutToneType) {
    coroutineScope.launch {
      if (soundEnabled) {
        val buffer = when (toneType) {
          WorkoutToneType.REST -> restBuffer
          WorkoutToneType.NEXT_SET -> nextSetBuffer
          WorkoutToneType.NEXT_PHASE -> nextPhaseBuffer
          WorkoutToneType.DONE -> doneBuffer
          WorkoutToneType.COUNTDOWN_TICK -> {
            if (countdownTicksEnabled) tickBuffer else null
          }
        }
        buffer?.let { playPcm(it) }
      }

      if (vibrationEnabled) {
        triggerVibration(toneType)
      }
    }
  }

  private fun triggerVibration(toneType: WorkoutToneType) {
    try {
      vibrator?.let { vib ->
        if (!vib.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          when (toneType) {
            WorkoutToneType.COUNTDOWN_TICK -> {
              if (countdownTicksEnabled) {
                vib.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
              }
            }
            WorkoutToneType.REST -> {
              vib.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            WorkoutToneType.NEXT_SET -> {
              val timings = longArrayOf(0, 100, 60, 120)
              val amplitudes = intArrayOf(0, 200, 0, 255)
              vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
            WorkoutToneType.NEXT_PHASE -> {
              val timings = longArrayOf(0, 120, 60, 120, 60, 200)
              val amplitudes = intArrayOf(0, 180, 0, 220, 0, 255)
              vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
            WorkoutToneType.DONE -> {
              val timings = longArrayOf(0, 150, 80, 150, 80, 400)
              val amplitudes = intArrayOf(0, 200, 0, 220, 0, 255)
              vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
          }
        } else {
          @Suppress("DEPRECATION")
          when (toneType) {
            WorkoutToneType.COUNTDOWN_TICK -> if (countdownTicksEnabled) vib.vibrate(40)
            WorkoutToneType.REST -> vib.vibrate(300)
            WorkoutToneType.NEXT_SET -> vib.vibrate(longArrayOf(0, 100, 60, 120), -1)
            WorkoutToneType.NEXT_PHASE -> vib.vibrate(longArrayOf(0, 120, 60, 120, 60, 200), -1)
            WorkoutToneType.DONE -> vib.vibrate(longArrayOf(0, 150, 80, 150, 80, 400), -1)
          }
        }
      }
    } catch (_: Exception) {
      // Ignore vibration errors if restricted on device
    }
  }

  private fun playPcm(samples: ShortArray) {
    var audioTrack: AudioTrack? = null
    try {
      val sampleRate = 44100
      val bufferSize = samples.size * 2

      val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

      val format = AudioFormat.Builder()
        .setSampleRate(sampleRate)
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
        .build()

      audioTrack = AudioTrack.Builder()
        .setAudioAttributes(attributes)
        .setAudioFormat(format)
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(samples, 0, samples.size)
      audioTrack.play()

      val durationMs = (samples.size * 1000L) / sampleRate
      Thread.sleep(durationMs + 40)
    } catch (_: Exception) {
      // Ignore audio track interruption
    } finally {
      try {
        audioTrack?.stop()
        audioTrack?.release()
      } catch (_: Exception) {}
    }
  }

  // --- Waveform Generators ---

  private fun generateToneWave(freq: Double, durationMs: Int, volumeScale: Double = 0.8): ShortArray {
    val sampleRate = 44100
    val numSamples = (sampleRate * durationMs) / 1000
    val samples = ShortArray(numSamples)

    val attackSamples = (sampleRate * 0.01).toInt()
    val decaySamples = (sampleRate * 0.02).toInt()

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      var envelope = 1.0
      if (i < attackSamples) {
        envelope = i.toDouble() / attackSamples
      } else if (i > numSamples - decaySamples) {
        envelope = (numSamples - i).toDouble() / decaySamples
      }
      val sample = (sin(2.0 * PI * freq * t) * envelope * volumeScale * Short.MAX_VALUE).toInt()
      samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
    return samples
  }

  private fun generateTwoToneWave(
    freq1: Double,
    freq2: Double,
    duration1Ms: Int,
    duration2Ms: Int
  ): ShortArray {
    val part1 = generateToneWave(freq1, duration1Ms, 0.7)
    val part2 = generateToneWave(freq2, duration2Ms, 0.75)
    return part1 + part2
  }

  private fun generateDoubleBeepWave(
    freq1: Double,
    freq2: Double,
    dur1Ms: Int,
    pauseMs: Int,
    dur2Ms: Int
  ): ShortArray {
    val part1 = generateToneWave(freq1, dur1Ms, 0.85)
    val pause = ShortArray((44100 * pauseMs) / 1000)
    val part2 = generateToneWave(freq2, dur2Ms, 0.9)
    return part1 + pause + part2
  }

  private fun generateTripleAscendingWave(
    f1: Double,
    f2: Double,
    f3: Double,
    d1: Int,
    d2: Int,
    d3: Int
  ): ShortArray {
    val p1 = generateToneWave(f1, d1, 0.8)
    val p2 = generateToneWave(f2, d2, 0.85)
    val p3 = generateToneWave(f3, d3, 0.9)
    return p1 + p2 + p3
  }

  private fun generateFanfareWave(
    f1: Double,
    f2: Double,
    f3: Double,
    f4: Double,
    d1: Int,
    d2: Int,
    d3: Int,
    d4: Int
  ): ShortArray {
    val p1 = generateToneWave(f1, d1, 0.8)
    val p2 = generateToneWave(f2, d2, 0.85)
    val p3 = generateToneWave(f3, d3, 0.9)
    val p4 = generateToneWave(f4, d4, 0.95)
    return p1 + p2 + p3 + p4
  }
}
