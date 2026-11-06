package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class WorkoutToneType {
  REST,
  NEXT_SET,
  NEXT_PHASE,
  DONE,
  COUNTDOWN_TICK
}

enum class SoundProfile(
  val displayName: String,
  val description: String
) {
  MELLOW_CHIMES(
    displayName = "Mellow Chimes",
    description = "Soft, warm acoustic bells & marimba — kid & family friendly"
  ),
  VOICE_COACH(
    displayName = "Spoken Voice Coach",
    description = "Speaks sets, rests, and exercises aloud in a calm voice"
  ),
  MODERN_SYNTH(
    displayName = "Modern Synth",
    description = "Punchy electronic athletic beeps"
  ),
  STEALTH_VIBRATE(
    displayName = "Stealth (Vibration Only)",
    description = "Completely silent speaker, distinct tactile haptic pulses"
  )
}

class ToneManager(private val context: Context) : TextToSpeech.OnInitListener {

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

  // Text to Speech Engine for Voice Coach
  private var tts: TextToSpeech? = null
  private var isTtsReady: Boolean = false

  init {
    try {
      tts = TextToSpeech(context.applicationContext, this)
    } catch (_: Exception) {
      isTtsReady = false
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      tts?.let { engine ->
        val result = engine.setLanguage(Locale.US)
        if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
          engine.setSpeechRate(1.05f)
          engine.setPitch(1.0f)
          isTtsReady = true
        }
      }
    }
  }

  // Configuration options
  var soundEnabled: Boolean = true
  var soundProfile: SoundProfile = SoundProfile.MELLOW_CHIMES
  var volumeLevel: Float = 0.5f // 0.05f to 1.0f (default comfortable 50%)
  var vibrationEnabled: Boolean = true
  var countdownTicksEnabled: Boolean = true
  var visualFlashEnabled: Boolean = true

  // Pre-computed audio buffers for Synth profile
  private val synthRestBuffer: ShortArray by lazy { generateTwoToneWave(523.25, 392.0, 220, 350) }
  private val synthNextSetBuffer: ShortArray by lazy { generateDoubleBeepWave(880.0, 1046.5, 110, 60, 150) }
  private val synthNextPhaseBuffer: ShortArray by lazy { generateTripleAscendingWave(587.33, 739.99, 880.0, 90, 90, 220) }
  private val synthDoneBuffer: ShortArray by lazy { generateFanfareWave(523.25, 659.25, 783.99, 1046.50, 120, 120, 140, 400) }
  private val synthTickBuffer: ShortArray by lazy { generateToneWave(659.25, 60, 0.4) }

  // Pre-computed audio buffers for Mellow Chimes (Warm acoustic harmonic chords with soft decay)
  private val mellowRestBuffer: ShortArray by lazy { generateWarmChime(392.0, 523.25, 450) } // G4 + C5 calm harmony
  private val mellowNextSetBuffer: ShortArray by lazy { generateTwoNoteMarimba(329.63, 440.0, 180, 260) } // E4 -> A4 marimba
  private val mellowNextPhaseBuffer: ShortArray by lazy { generateAscendingChimes(listOf(261.63, 329.63, 392.0), 140) } // C4-E4-G4 triad
  private val mellowDoneBuffer: ShortArray by lazy { generateAscendingChimes(listOf(261.63, 329.63, 392.0, 523.25), 200, isFinalLong = true) }
  private val mellowTickBuffer: ShortArray by lazy { generateWoodBlockTick(329.63, 50) } // Low warm wooden tap

  fun playTone(toneType: WorkoutToneType, spokenPhrase: String? = null) {
    coroutineScope.launch {
      // 1. Audio playback
      if (soundEnabled && soundProfile != SoundProfile.STEALTH_VIBRATE) {
        when (soundProfile) {
          SoundProfile.VOICE_COACH -> {
            playVoiceCoachCue(toneType, spokenPhrase)
          }
          SoundProfile.MELLOW_CHIMES -> {
            val buffer = when (toneType) {
              WorkoutToneType.REST -> mellowRestBuffer
              WorkoutToneType.NEXT_SET -> mellowNextSetBuffer
              WorkoutToneType.NEXT_PHASE -> mellowNextPhaseBuffer
              WorkoutToneType.DONE -> mellowDoneBuffer
              WorkoutToneType.COUNTDOWN_TICK -> if (countdownTicksEnabled) mellowTickBuffer else null
            }
            buffer?.let { playPcm(it, volumeLevel) }
          }
          SoundProfile.MODERN_SYNTH -> {
            val buffer = when (toneType) {
              WorkoutToneType.REST -> synthRestBuffer
              WorkoutToneType.NEXT_SET -> synthNextSetBuffer
              WorkoutToneType.NEXT_PHASE -> synthNextPhaseBuffer
              WorkoutToneType.DONE -> synthDoneBuffer
              WorkoutToneType.COUNTDOWN_TICK -> if (countdownTicksEnabled) synthTickBuffer else null
            }
            buffer?.let { playPcm(it, volumeLevel) }
          }
          SoundProfile.STEALTH_VIBRATE -> {
            // No audio in stealth mode
          }
        }
      }

      // 2. Haptic vibration
      if (vibrationEnabled || soundProfile == SoundProfile.STEALTH_VIBRATE) {
        triggerVibration(toneType)
      }
    }
  }

  private fun playVoiceCoachCue(toneType: WorkoutToneType, spokenPhrase: String?) {
    // Play a gentle soft warm cue first, then speak
    val textToSpeak = when (toneType) {
      WorkoutToneType.REST -> spokenPhrase ?: "Rest period. Take a breath."
      WorkoutToneType.NEXT_SET -> spokenPhrase ?: "Next set. Let's go!"
      WorkoutToneType.NEXT_PHASE -> spokenPhrase ?: "Next exercise."
      WorkoutToneType.DONE -> "Workout complete! Fantastic job!"
      WorkoutToneType.COUNTDOWN_TICK -> {
        // For 3-2-1, use gentle soft woodblock tick instead of talking over itself
        if (countdownTicksEnabled) {
          playPcm(mellowTickBuffer, volumeLevel * 0.7f)
        }
        null
      }
    }

    if (textToSpeak != null && isTtsReady && tts != null) {
      try {
        val params = Bundle().apply {
          putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volumeLevel.coerceIn(0.1f, 1.0f))
        }
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "WORKOUT_VOICE_ID")
      } catch (_: Exception) {
        // Fallback to mellow chime if TTS fails
        val buffer = when (toneType) {
          WorkoutToneType.REST -> mellowRestBuffer
          WorkoutToneType.NEXT_SET -> mellowNextSetBuffer
          WorkoutToneType.NEXT_PHASE -> mellowNextPhaseBuffer
          WorkoutToneType.DONE -> mellowDoneBuffer
          WorkoutToneType.COUNTDOWN_TICK -> mellowTickBuffer
        }
        playPcm(buffer, volumeLevel)
      }
    } else if (textToSpeak != null) {
      // Fallback if TTS is still initializing
      val buffer = when (toneType) {
        WorkoutToneType.REST -> mellowRestBuffer
        WorkoutToneType.NEXT_SET -> mellowNextSetBuffer
        WorkoutToneType.NEXT_PHASE -> mellowNextPhaseBuffer
        WorkoutToneType.DONE -> mellowDoneBuffer
        WorkoutToneType.COUNTDOWN_TICK -> mellowTickBuffer
      }
      playPcm(buffer, volumeLevel)
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
                vib.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
              }
            }
            WorkoutToneType.REST -> {
              vib.vibrate(VibrationEffect.createOneShot(280, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            WorkoutToneType.NEXT_SET -> {
              val timings = longArrayOf(0, 90, 70, 110)
              val amplitudes = intArrayOf(0, 190, 0, 240)
              vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
            WorkoutToneType.NEXT_PHASE -> {
              val timings = longArrayOf(0, 100, 60, 100, 60, 180)
              val amplitudes = intArrayOf(0, 160, 0, 200, 0, 255)
              vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
            WorkoutToneType.DONE -> {
              val timings = longArrayOf(0, 140, 70, 140, 70, 350)
              val amplitudes = intArrayOf(0, 180, 0, 220, 0, 255)
              vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
          }
        } else {
          @Suppress("DEPRECATION")
          when (toneType) {
            WorkoutToneType.COUNTDOWN_TICK -> if (countdownTicksEnabled) vib.vibrate(35)
            WorkoutToneType.REST -> vib.vibrate(280)
            WorkoutToneType.NEXT_SET -> vib.vibrate(longArrayOf(0, 90, 70, 110), -1)
            WorkoutToneType.NEXT_PHASE -> vib.vibrate(longArrayOf(0, 100, 60, 100, 60, 180), -1)
            WorkoutToneType.DONE -> vib.vibrate(longArrayOf(0, 140, 70, 140, 70, 350), -1)
          }
        }
      }
    } catch (_: Exception) {
      // Ignore vibration errors if restricted on device
    }
  }

  private fun playPcm(samples: ShortArray, userVolume: Float = 1.0f) {
    var audioTrack: AudioTrack? = null
    try {
      val sampleRate = 44100
      val bufferSize = samples.size * 2

      // Apply master volume level to sample buffer
      val scaledSamples = ShortArray(samples.size)
      val scale = userVolume.coerceIn(0f, 1.0f)
      for (i in samples.indices) {
        scaledSamples[i] = (samples[i] * scale).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }

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

      audioTrack.write(scaledSamples, 0, scaledSamples.size)
      audioTrack.play()

      val durationMs = (samples.size * 1000L) / sampleRate
      Thread.sleep(durationMs + 30)
    } catch (_: Exception) {
      // Ignore audio track interruption
    } finally {
      try {
        audioTrack?.stop()
        audioTrack?.release()
      } catch (_: Exception) {}
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      tts = null
    } catch (_: Exception) {}
  }

  // --- Mellow Chimes Waveform Generators (Kid-friendly, warm, gentle decay) ---

  private fun generateWarmChime(f1: Double, f2: Double, durationMs: Int): ShortArray {
    val sampleRate = 44100
    val numSamples = (sampleRate * durationMs) / 1000
    val samples = ShortArray(numSamples)

    val attackSamples = (sampleRate * 0.03).toInt() // Soft 30ms smooth attack

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      // Smooth exponential decay curve
      val decay = exp(-3.2 * (i.toDouble() / numSamples))
      val attack = if (i < attackSamples) i.toDouble() / attackSamples else 1.0
      val envelope = attack * decay

      // Blend fundamental plus soft 2nd harmonic
      val s1 = sin(2.0 * PI * f1 * t)
      val s2 = 0.5 * sin(2.0 * PI * f2 * t)
      val sample = ((s1 + s2) * envelope * 0.65 * Short.MAX_VALUE).toInt()
      samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
    return samples
  }

  private fun generateTwoNoteMarimba(f1: Double, f2: Double, note1Ms: Int, note2Ms: Int): ShortArray {
    val p1 = generateSingleMarimbaNote(f1, note1Ms, 0.7)
    val p2 = generateSingleMarimbaNote(f2, note2Ms, 0.75)
    return p1 + p2
  }

  private fun generateSingleMarimbaNote(freq: Double, durationMs: Int, volume: Double): ShortArray {
    val sampleRate = 44100
    val numSamples = (sampleRate * durationMs) / 1000
    val samples = ShortArray(numSamples)
    val attackSamples = (sampleRate * 0.008).toInt()

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val decay = exp(-5.0 * (i.toDouble() / numSamples))
      val attack = if (i < attackSamples) i.toDouble() / attackSamples else 1.0
      val s = sin(2.0 * PI * freq * t) + 0.2 * sin(2.0 * PI * (freq * 2) * t)
      val sample = (s * attack * decay * volume * Short.MAX_VALUE).toInt()
      samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
    return samples
  }

  private fun generateAscendingChimes(frequencies: List<Double>, noteDurationMs: Int, isFinalLong: Boolean = false): ShortArray {
    var result = ShortArray(0)
    frequencies.forEachIndexed { index, freq ->
      val dur = if (index == frequencies.lastIndex && isFinalLong) noteDurationMs * 2 else noteDurationMs
      val note = generateSingleMarimbaNote(freq, dur, 0.72)
      result = result + note
    }
    return result
  }

  private fun generateWoodBlockTick(freq: Double, durationMs: Int): ShortArray {
    val sampleRate = 44100
    val numSamples = (sampleRate * durationMs) / 1000
    val samples = ShortArray(numSamples)
    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val decay = exp(-12.0 * (i.toDouble() / numSamples)) // Fast organic wood tap
      val s = sin(2.0 * PI * freq * t) + 0.3 * sin(2.0 * PI * (freq * 1.5) * t)
      val sample = (s * decay * 0.45 * Short.MAX_VALUE).toInt()
      samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
    return samples
  }

  // --- Modern Synth Waveform Generators ---

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
