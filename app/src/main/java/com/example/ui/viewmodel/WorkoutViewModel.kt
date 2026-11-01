package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.ToneManager
import com.example.audio.WorkoutToneType
import com.example.data.WorkoutDatabase
import com.example.data.WorkoutRepository
import com.example.model.ExpandedInterval
import com.example.model.PhaseType
import com.example.model.WorkoutPlaylist
import com.example.model.WorkoutStep
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ScreenState {
  HOME,
  TIMER,
  EDITOR
}

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: WorkoutRepository
  val toneManager: ToneManager = ToneManager(application)

  init {
    val db = WorkoutDatabase.getDatabase(application)
    repository = WorkoutRepository(db.workoutDao())
    viewModelScope.launch {
      repository.ensureSeeded()
    }
  }

  // --- Theme & Audio Settings ---
  private val _themeMode = MutableStateFlow(AppThemeMode.GYM_DARK)
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  fun setThemeMode(mode: AppThemeMode) {
    _themeMode.value = mode
  }

  fun toggleTheme() {
    _themeMode.value = when (_themeMode.value) {
      AppThemeMode.GYM_DARK -> AppThemeMode.LIGHT
      AppThemeMode.LIGHT -> AppThemeMode.GYM_DARK
      AppThemeMode.SYSTEM -> AppThemeMode.GYM_DARK
    }
  }

  private val _soundEnabled = MutableStateFlow(true)
  val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

  private val _vibrationEnabled = MutableStateFlow(true)
  val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

  private val _countdownTicksEnabled = MutableStateFlow(true)
  val countdownTicksEnabled: StateFlow<Boolean> = _countdownTicksEnabled.asStateFlow()

  fun setSoundEnabled(enabled: Boolean) {
    _soundEnabled.value = enabled
    toneManager.soundEnabled = enabled
  }

  fun setVibrationEnabled(enabled: Boolean) {
    _vibrationEnabled.value = enabled
    toneManager.vibrationEnabled = enabled
  }

  fun setCountdownTicksEnabled(enabled: Boolean) {
    _countdownTicksEnabled.value = enabled
    toneManager.countdownTicksEnabled = enabled
  }

  fun playTestTone(toneType: WorkoutToneType) {
    toneManager.playTone(toneType)
  }

  // --- Day Filtering & Navigation ---
  val todayDayCode: String = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    Calendar.MONDAY -> "MON"
    Calendar.TUESDAY -> "TUE"
    Calendar.WEDNESDAY -> "WED"
    Calendar.THURSDAY -> "THU"
    Calendar.FRIDAY -> "FRI"
    Calendar.SATURDAY -> "SAT"
    Calendar.SUNDAY -> "SUN"
    else -> "MON"
  }

  private val _selectedDayFilter = MutableStateFlow("ALL")
  val selectedDayFilter: StateFlow<String> = _selectedDayFilter.asStateFlow()

  fun selectDayFilter(dayCode: String) {
    _selectedDayFilter.value = dayCode
  }

  private val _currentScreen = MutableStateFlow(ScreenState.HOME)
  val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

  fun navigateTo(screen: ScreenState) {
    _currentScreen.value = screen
  }

  val allPlaylists: StateFlow<List<WorkoutPlaylist>> = repository.allPlaylists
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredPlaylists: StateFlow<List<WorkoutPlaylist>> = combine(allPlaylists, _selectedDayFilter) { playlists, filter ->
    if (filter == "ALL") {
      playlists
    } else {
      playlists.filter { it.assignedDays.contains(filter) || it.assignedDays.contains("DAILY") }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val todayPlaylist: StateFlow<WorkoutPlaylist?> = combine(allPlaylists, MutableStateFlow(todayDayCode)) { list, today ->
    list.firstOrNull { it.assignedDays.contains(today) || it.assignedDays.contains("DAILY") } ?: list.firstOrNull()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // --- Active Timer State ---
  private val _activePlaylist = MutableStateFlow<WorkoutPlaylist?>(null)
  val activePlaylist: StateFlow<WorkoutPlaylist?> = _activePlaylist.asStateFlow()

  private val _intervalQueue = MutableStateFlow<List<ExpandedInterval>>(emptyList())
  val intervalQueue: StateFlow<List<ExpandedInterval>> = _intervalQueue.asStateFlow()

  private val _currentIntervalIndex = MutableStateFlow(0)
  val currentIntervalIndex: StateFlow<Int> = _currentIntervalIndex.asStateFlow()

  private val _currentSecondsLeft = MutableStateFlow(0)
  val currentSecondsLeft: StateFlow<Int> = _currentSecondsLeft.asStateFlow()

  private val _isTimerRunning = MutableStateFlow(false)
  val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

  private val _isWorkoutCompleted = MutableStateFlow(false)
  val isWorkoutCompleted: StateFlow<Boolean> = _isWorkoutCompleted.asStateFlow()

  private val _isScreenLocked = MutableStateFlow(false)
  val isScreenLocked: StateFlow<Boolean> = _isScreenLocked.asStateFlow()

  private val _elapsedTotalSeconds = MutableStateFlow(0)
  val elapsedTotalSeconds: StateFlow<Int> = _elapsedTotalSeconds.asStateFlow()

  private var timerJob: Job? = null

  val currentInterval: StateFlow<ExpandedInterval?> = combine(_intervalQueue, _currentIntervalIndex) { queue, index ->
    queue.getOrNull(index)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val nextInterval: StateFlow<ExpandedInterval?> = combine(_intervalQueue, _currentIntervalIndex) { queue, index ->
    queue.getOrNull(index + 1)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  fun startWorkout(playlist: WorkoutPlaylist) {
    val queue = ExpandedInterval.buildIntervalQueue(playlist)
    if (queue.isEmpty()) return

    _activePlaylist.value = playlist
    _intervalQueue.value = queue
    _currentIntervalIndex.value = 0
    _currentSecondsLeft.value = queue.first().durationSeconds
    _isWorkoutCompleted.value = false
    _isScreenLocked.value = false
    _elapsedTotalSeconds.value = 0
    _isTimerRunning.value = true
    _currentScreen.value = ScreenState.TIMER

    // Sound signal: Next phase started
    toneManager.playTone(WorkoutToneType.NEXT_PHASE)
    startTimerLoop()
  }

  fun pauseTimer() {
    _isTimerRunning.value = false
    timerJob?.cancel()
  }

  fun resumeTimer() {
    if (_isWorkoutCompleted.value) return
    _isTimerRunning.value = true
    startTimerLoop()
  }

  fun togglePlayPause() {
    if (_isTimerRunning.value) {
      pauseTimer()
    } else {
      resumeTimer()
    }
  }

  fun toggleScreenLock() {
    _isScreenLocked.value = !_isScreenLocked.value
  }

  fun adjustTime(deltaSeconds: Int) {
    val updated = (_currentSecondsLeft.value + deltaSeconds).coerceAtLeast(1)
    _currentSecondsLeft.value = updated
  }

  fun skipToNext() {
    advanceToInterval(_currentIntervalIndex.value + 1)
  }

  fun previousInterval() {
    if (_currentSecondsLeft.value < (_intervalQueue.value.getOrNull(_currentIntervalIndex.value)?.durationSeconds ?: 0) - 3) {
      // If we are more than 3 seconds into the interval, restart current interval
      _currentSecondsLeft.value = _intervalQueue.value.getOrNull(_currentIntervalIndex.value)?.durationSeconds ?: 0
    } else {
      val prevIdx = (_currentIntervalIndex.value - 1).coerceAtLeast(0)
      advanceToInterval(prevIdx)
    }
  }

  private fun advanceToInterval(targetIndex: Int) {
    val queue = _intervalQueue.value
    if (targetIndex >= queue.size) {
      // Completed!
      _isTimerRunning.value = false
      _isWorkoutCompleted.value = true
      _currentSecondsLeft.value = 0
      timerJob?.cancel()
      toneManager.playTone(WorkoutToneType.DONE)
      return
    }

    val current = queue.getOrNull(_currentIntervalIndex.value)
    val next = queue.getOrNull(targetIndex)

    _currentIntervalIndex.value = targetIndex
    _currentSecondsLeft.value = next?.durationSeconds ?: 0

    // Sound alert based on transition
    if (next != null && current != null) {
      when {
        next.phaseType == PhaseType.REST -> {
          toneManager.playTone(WorkoutToneType.REST)
        }
        next.stepId == current.stepId && next.phaseType == PhaseType.WORK -> {
          // Next set of same exercise
          toneManager.playTone(WorkoutToneType.NEXT_SET)
        }
        else -> {
          // New exercise / phase
          toneManager.playTone(WorkoutToneType.NEXT_PHASE)
        }
      }
    }
  }

  private fun startTimerLoop() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (isActive && _isTimerRunning.value) {
        delay(1000L)
        if (!_isTimerRunning.value) break

        _elapsedTotalSeconds.value += 1
        val remaining = _currentSecondsLeft.value - 1

        if (remaining in 1..3) {
          // Countdown 3, 2, 1 ticks
          toneManager.playTone(WorkoutToneType.COUNTDOWN_TICK)
        }

        if (remaining <= 0) {
          advanceToInterval(_currentIntervalIndex.value + 1)
        } else {
          _currentSecondsLeft.value = remaining
        }
      }
    }
  }

  fun restartCurrentWorkout() {
    val playlist = _activePlaylist.value ?: return
    startWorkout(playlist)
  }

  fun exitWorkout() {
    pauseTimer()
    _activePlaylist.value = null
    _currentScreen.value = ScreenState.HOME
  }

  // --- Playlist Editor State ---
  private val _editingPlaylist = MutableStateFlow<WorkoutPlaylist?>(null)
  val editingPlaylist: StateFlow<WorkoutPlaylist?> = _editingPlaylist.asStateFlow()

  private val _editingSteps = MutableStateFlow<List<WorkoutStep>>(emptyList())
  val editingSteps: StateFlow<List<WorkoutStep>> = _editingSteps.asStateFlow()

  fun startCreatePlaylist() {
    val newPlaylist = WorkoutPlaylist(
      id = 0,
      title = "New Workout Routine",
      description = "",
      assignedDays = listOf(todayDayCode),
      colorHex = "#00E676",
      isPreset = false,
      steps = listOf(
        WorkoutStep(label = "Warmup Stretch", phaseType = PhaseType.WARMUP, durationSeconds = 60, targetSets = 1, targetReps = "-"),
        WorkoutStep(label = "Primary Exercise", phaseType = PhaseType.WORK, durationSeconds = 45, targetSets = 3, targetReps = "10 reps", restBetweenSetsSeconds = 30),
        WorkoutStep(label = "Cooldown Breathwork", phaseType = PhaseType.COOLDOWN, durationSeconds = 60, targetSets = 1, targetReps = "-")
      )
    )
    _editingPlaylist.value = newPlaylist
    _editingSteps.value = newPlaylist.steps
    _currentScreen.value = ScreenState.EDITOR
  }

  fun startEditPlaylist(playlist: WorkoutPlaylist) {
    _editingPlaylist.value = playlist
    _editingSteps.value = playlist.steps
    _currentScreen.value = ScreenState.EDITOR
  }

  fun addEditingStep(step: WorkoutStep) {
    _editingSteps.value = _editingSteps.value + step
  }

  fun updateEditingStep(index: Int, step: WorkoutStep) {
    val current = _editingSteps.value.toMutableList()
    if (index in current.indices) {
      current[index] = step
      _editingSteps.value = current
    }
  }

  fun deleteEditingStep(index: Int) {
    val current = _editingSteps.value.toMutableList()
    if (index in current.indices) {
      current.removeAt(index)
      _editingSteps.value = current
    }
  }

  fun moveStepUp(index: Int) {
    if (index <= 0) return
    val current = _editingSteps.value.toMutableList()
    val item = current.removeAt(index)
    current.add(index - 1, item)
    _editingSteps.value = current
  }

  fun moveStepDown(index: Int) {
    val current = _editingSteps.value.toMutableList()
    if (index >= current.size - 1) return
    val item = current.removeAt(index)
    current.add(index + 1, item)
    _editingSteps.value = current
  }

  fun savePlaylist(title: String, description: String, days: List<String>, colorHex: String) {
    val existing = _editingPlaylist.value
    val playlist = WorkoutPlaylist(
      id = existing?.id ?: 0,
      title = title.ifBlank { "Workout Routine" },
      description = description,
      assignedDays = days,
      steps = _editingSteps.value,
      colorHex = colorHex,
      isPreset = false
    )

    viewModelScope.launch {
      repository.savePlaylist(playlist)
      _currentScreen.value = ScreenState.HOME
      _editingPlaylist.value = null
    }
  }

  fun duplicatePlaylist(playlist: WorkoutPlaylist) {
    viewModelScope.launch {
      val copy = playlist.copy(
        id = 0,
        title = "${playlist.title} (Copy)",
        isPreset = false,
        createdAt = System.currentTimeMillis()
      )
      repository.savePlaylist(copy)
    }
  }

  fun deletePlaylist(playlist: WorkoutPlaylist) {
    viewModelScope.launch {
      repository.deletePlaylist(playlist)
    }
  }

  fun restoreDefaultPresets() {
    viewModelScope.launch {
      repository.restorePresets()
    }
  }
}
