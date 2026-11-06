# PulseTimer ⏱️⚡

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg?style=flat)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue.svg?style=flat)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**PulseTimer** is a modern, high-contrast workout interval timer and routine playlist manager built with Jetpack Compose. Tailored specifically for low-light gym floors and intense interval training, PulseTimer features custom time playlists for each day of the week, configurable sets and reps with optional inter-set rest intervals, real-time synthesized audio tones, and an OLED Gym Dark mode.

---

## 🌟 Key Features

### 📅 Daily Workout Playlists & Day Schedules
- **Day-by-Day Tagging**: Assign custom routines to specific days of the week (**Mon – Sun** or **Daily**).
- **Today's Routine Hero**: Automatically detects the current day and features your scheduled routine on the home screen with instant 1-tap start.
- **Pre-Configured Presets**: Seeded with 7 days of realistic routines:
  - *Monday*: Chest & Triceps Push
  - *Tuesday*: HIIT Cardio & Core Blast
  - *Wednesday*: Back & Biceps Pull
  - *Thursday*: Mobility & Active Recovery
  - *Friday*: Legs & Core Crusher
  - *Saturday*: Full Body Tabata Protocol
  - *Sunday*: Zen Stretch & Recharge
- **Offline Room Database**: Instant SQLite persistence with Kotlin Flow reactivity; all routines remain saved on your device.

### 🏋️ Sets, Reps & Inter-Set Rest Configuration
- **Custom Exercise Labels**: Give every interval clear labels (*e.g., "Barbell Bench Press", "Burpees", "Plank"*).
- **Target Sets**: Set multi-round repetition counts (1 to 20 sets).
- **Target Reps**: Configurable rep targets (*"8 reps"*, *"10 reps"*, *"12 reps"*, *"AMRAP"*, *"To Failure"*).
- **Optional Rest Between Sets**: Toggle automatic rest periods between sets (*e.g., 3 sets of 30s work with 30s rest between sets*).
- **Standalone Rest Intervals**: Dedicated rest blocks for water breaks, station transitions, and post-circuit recovery.
- **Interactive Sequence Preview**: Live preview shows step timing and the exact interval flow before saving.

### 🔊 Customizable Audio & Family-Friendly Sound Profiles
Built using low-latency real-time PCM synthesis via Android `AudioTrack` and Android `TextToSpeech` (zero external asset dependencies):
- **In-App Master Volume Slider**: Direct 0%–100% volume scaling independent of system ringer volume (dial down to a 20% gentle gym whisper).
- 🔔 **Mellow Chimes (Kid & Family Friendly)**: Warm acoustic bells, marimba chords, and soft exponential decay without piercing electronic beeps.
- 🗣️ **Spoken Voice Coach**: Speaks exercise names, sets, rests, and completion aloud in a natural calm voice.
- 🎛️ **Modern Synth**: Original crisp electronic gym transition beeps for noisy workout spaces.
- 📳 **Stealth (Vibration Only)**: 100% silent speaker mode using tactile haptic pulses (short double-buzz for next set, long pulse for rest, fanfare pulse for done).
- ⏱️ **3-2-1 Countdown Ticks Toggle**: Easily disable pre-set countdown beeps to reduce noise while keeping transition chimes.
- **Audio & Ambience Modal**: Audition each tone directly and configure volume, profile, and vibration settings.

### 🌙 Low-Light Gym OLED Dark Mode
- **Pure Black (#000000)** background reduces eye strain in dim gym lighting and maximizes battery efficiency on OLED screens.
- **High-Contrast Neon Accents**:
  - `Neon Lime (#00E676)` — Work / Exercise
  - `Neon Cyan (#00E5FF)` — Rest Periods
  - `Neon Amber (#FF9100)` — Warmup
  - `Neon Violet (#B388FF)` — Cooldown
- **Chakra Petch & Rubik Typography**: High-legibility digital clock readable from across the gym floor.
- **Screen Lock Guard**: One-tap touch lock prevents accidental inputs when your device is in a pocket or gym bag.
- **Screen Wake Lock**: Automatically prevents the display from sleeping while a workout timer is active.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3).
- **Language**: [Kotlin](https://kotlinlang.org/) (Coroutines, StateFlow, Flow).
- **Architecture**: MVVM (Model-View-ViewModel) + Repository pattern.
- **Database**: [AndroidX Room](https://developer.android.com/training/data-storage/room) with KSP compiler.
- **Audio & Haptics**: Native `AudioTrack` PCM waveform synthesis + `Vibrator` / `VibrationEffect` API.
- **Typography**: Bundled Google Fonts (`Chakra Petch` and `Rubik`) via local `.ttf` font resources.
- **Adaptive Launcher Icon**: Custom adaptive vector/raster launcher icons across all mipmap densities.

---

## 📂 Project Structure

```
app/src/main/
├── java/com/example/
│   ├── MainActivity.kt               # Main entry point & screen navigation
│   ├── audio/
│   │   └── ToneManager.kt            # AudioTrack PCM tone synthesizer & haptics
│   ├── data/
│   │   ├── DefaultWorkouts.kt        # Pre-seeded Monday–Sunday workout presets
│   │   ├── WorkoutDao.kt             # Room DAO interface
│   │   ├── WorkoutDatabase.kt        # Room database & auto-population
│   │   ├── WorkoutEntity.kt          # Room entity model
│   │   ├── WorkoutJsonConverter.kt   # JSON serialization for steps
│   │   └── WorkoutRepository.kt      # Data repository layer
│   ├── model/
│   │   ├── ExpandedInterval.kt       # Playback interval queue engine
│   │   ├── PhaseType.kt              # Work, Rest, Warmup, Cooldown enum
│   │   ├── WorkoutPlaylist.kt        # Workout playlist & day models
│   │   └── WorkoutStep.kt            # Step data model with sets & reps
│   └── ui/
│       ├── components/
│       │   ├── AddEditStepDialog.kt  # Step & sets/reps modal editor
│       │   ├── CircularTimerDisplay.kt # Large neon timer display
│       │   ├── SoundTesterDialog.kt  # Audio audition & settings dialog
│       │   └── UpNextCard.kt         # Upcoming interval preview card
│       ├── screens/
│       │   ├── HomeScreen.kt         # Daily playlists, hero card, filters
│       │   ├── PlaylistEditScreen.kt # Routine builder & step reordering
│       │   └── TimerScreen.kt        # Fullscreen workout player
│       └── theme/
│           ├── Color.kt              # Gym OLED & neon color tokens
│           ├── Theme.kt              # PulseTimerTheme configuration
│           └── Type.kt               # Chakra Petch & Rubik typography
└── res/
    ├── font/                         # Bundled local TTF fonts
    └── mipmap-*/                     # Custom adaptive launcher icons
```

---

## 🚀 Building & Running

### Prerequisites
- **Android Studio** Ladybug / Koala or later
- **JDK 17+**
- **Android SDK Platform 36** (Compile SDK: 36, Min SDK: 24)

### Clone & Build
```bash
# Clone repository
git clone https://github.com/<your-username>/pulsetimer.git
cd pulsetimer

# Build debug APK
gradle assembleDebug

# Run unit tests
gradle :app:testDebugUnitTest
```

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
