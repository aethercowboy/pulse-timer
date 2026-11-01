package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.SoundTesterDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlaylistEditScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.theme.PulseTimerTheme
import com.example.ui.viewmodel.ScreenState
import com.example.ui.viewmodel.WorkoutViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: WorkoutViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val themeMode by viewModel.themeMode.collectAsState()

      PulseTimerTheme(themeMode = themeMode) {
        val currentScreen by viewModel.currentScreen.collectAsState()
        val isTimerRunning by viewModel.isTimerRunning.collectAsState()

        // Keep screen awake during active workout
        DisposableEffect(currentScreen, isTimerRunning) {
          if (currentScreen == ScreenState.TIMER && isTimerRunning) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
          } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
          }
          onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
          }
        }

        Surface(modifier = Modifier.fillMaxSize()) {
          WorkoutApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun WorkoutApp(viewModel: WorkoutViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val themeMode by viewModel.themeMode.collectAsState()
  val soundEnabled by viewModel.soundEnabled.collectAsState()
  val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
  val countdownTicksEnabled by viewModel.countdownTicksEnabled.collectAsState()

  var showSoundDialog by remember { mutableStateOf(false) }

  when (currentScreen) {
    ScreenState.HOME -> {
      val filteredPlaylists by viewModel.filteredPlaylists.collectAsState()
      val todayPlaylist by viewModel.todayPlaylist.collectAsState()
      val selectedDayFilter by viewModel.selectedDayFilter.collectAsState()

      HomeScreen(
        playlists = filteredPlaylists,
        todayPlaylist = todayPlaylist,
        selectedDayFilter = selectedDayFilter,
        todayDayCode = viewModel.todayDayCode,
        themeMode = themeMode,
        onSelectDayFilter = { viewModel.selectDayFilter(it) },
        onStartWorkout = { viewModel.startWorkout(it) },
        onEditPlaylist = { viewModel.startEditPlaylist(it) },
        onDuplicatePlaylist = { viewModel.duplicatePlaylist(it) },
        onDeletePlaylist = { viewModel.deletePlaylist(it) },
        onCreatePlaylist = { viewModel.startCreatePlaylist() },
        onRestorePresets = { viewModel.restoreDefaultPresets() },
        onToggleTheme = { viewModel.toggleTheme() },
        onOpenSoundSettings = { showSoundDialog = true }
      )
    }

    ScreenState.TIMER -> {
      val activePlaylist by viewModel.activePlaylist.collectAsState()
      val currentInterval by viewModel.currentInterval.collectAsState()
      val nextInterval by viewModel.nextInterval.collectAsState()
      val secondsLeft by viewModel.currentSecondsLeft.collectAsState()
      val isTimerRunning by viewModel.isTimerRunning.collectAsState()
      val isCompleted by viewModel.isWorkoutCompleted.collectAsState()
      val isScreenLocked by viewModel.isScreenLocked.collectAsState()
      val elapsedTotalSeconds by viewModel.elapsedTotalSeconds.collectAsState()

      if (activePlaylist != null) {
        TimerScreen(
          playlist = activePlaylist!!,
          currentInterval = currentInterval,
          nextInterval = nextInterval,
          secondsLeft = secondsLeft,
          isTimerRunning = isTimerRunning,
          isCompleted = isCompleted,
          isScreenLocked = isScreenLocked,
          soundEnabled = soundEnabled,
          elapsedTotalSeconds = elapsedTotalSeconds,
          onTogglePlayPause = { viewModel.togglePlayPause() },
          onSkipNext = { viewModel.skipToNext() },
          onPrevious = { viewModel.previousInterval() },
          onAdjustTime = { viewModel.adjustTime(it) },
          onToggleLock = { viewModel.toggleScreenLock() },
          onToggleSound = { viewModel.setSoundEnabled(!soundEnabled) },
          onRestart = { viewModel.restartCurrentWorkout() },
          onExit = { viewModel.exitWorkout() }
        )
      } else {
        viewModel.navigateTo(ScreenState.HOME)
      }
    }

    ScreenState.EDITOR -> {
      val editingPlaylist by viewModel.editingPlaylist.collectAsState()
      val editingSteps by viewModel.editingSteps.collectAsState()

      PlaylistEditScreen(
        playlist = editingPlaylist,
        steps = editingSteps,
        onAddStep = { viewModel.addEditingStep(it) },
        onUpdateStep = { idx, step -> viewModel.updateEditingStep(idx, step) },
        onDeleteStep = { idx -> viewModel.deleteEditingStep(idx) },
        onMoveStepUp = { idx -> viewModel.moveStepUp(idx) },
        onMoveStepDown = { idx -> viewModel.moveStepDown(idx) },
        onSave = { title, desc, days, colorHex ->
          viewModel.savePlaylist(title, desc, days, colorHex)
        },
        onCancel = { viewModel.navigateTo(ScreenState.HOME) }
      )
    }
  }

  // Audio & Ambience Dialog
  if (showSoundDialog) {
    SoundTesterDialog(
      soundEnabled = soundEnabled,
      vibrationEnabled = vibrationEnabled,
      countdownTicksEnabled = countdownTicksEnabled,
      themeMode = themeMode,
      onSoundToggle = { viewModel.setSoundEnabled(it) },
      onVibrationToggle = { viewModel.setVibrationEnabled(it) },
      onCountdownTicksToggle = { viewModel.setCountdownTicksEnabled(it) },
      onThemeChange = { viewModel.setThemeMode(it) },
      onPlayTestTone = { viewModel.playTestTone(it) },
      onDismiss = { showSoundDialog = false }
    )
  }
}
