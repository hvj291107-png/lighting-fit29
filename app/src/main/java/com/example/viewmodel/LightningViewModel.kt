package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExercisePerformanceEntity
import com.example.data.LightningRepository
import com.example.model.Achievement
import com.example.model.AthleteProfile
import com.example.model.DefaultRoutines
import com.example.model.Exercise
import com.example.model.ExerciseCategory
import com.example.model.ExerciseType
import com.example.model.PerformanceRank
import com.example.model.WorkoutLog
import com.example.model.WorkoutRoutine
import com.example.sound.SoundManager
import com.example.sound.SoundSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

enum class AppNavTab {
    HOME,
    TRAINING,
    ROUTINES,
    PROGRESS,
    ACHIEVEMENTS
}

sealed class AppScreen {
    object Splash : AppScreen()
    object Main : AppScreen()
    object ActiveWorkout : AppScreen()
    object WorkoutSummary : AppScreen()
    data class EditRoutine(val routine: WorkoutRoutine) : AppScreen()
    object Stage02ComingSoon : AppScreen()
}

data class SetResult(
    val exerciseId: String,
    val setNumber: Int,
    val repsOrSeconds: Int,
    val isCompleted: Boolean = false
)

data class ActiveWorkoutState(
    val routine: WorkoutRoutine,
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 1,
    val completedSetsMap: Map<String, List<SetResult>> = emptyMap(), // exerciseId -> list of sets
    val currentRepsInput: Int = 0,
    val timerSecondsRemaining: Int = 0,
    val isExerciseTimerRunning: Boolean = false,
    val timerTotalDuration: Int = 0,
    val isTimerCompleted: Boolean = false,
    val isRestActive: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val restTotalDuration: Int = 0,
    val elapsedSeconds: Int = 0,
    val isWorkoutPaused: Boolean = false
) {
    val currentExercise: Exercise?
        get() = routine.exercises.getOrNull(currentExerciseIndex)

    val isLastExercise: Boolean
        get() = currentExerciseIndex >= routine.exercises.lastIndex

    val isLastSetOfExercise: Boolean
        get() {
            val ex = currentExercise ?: return true
            return currentSetIndex >= ex.targetSets
        }

    val totalSetsInWorkout: Int
        get() = routine.exercises.sumOf { it.targetSets }

    val totalCompletedSetsCount: Int
        get() = completedSetsMap.values.flatten().count { it.isCompleted }

    val progressFraction: Float
        get() = if (totalSetsInWorkout > 0) {
            (totalCompletedSetsCount.toFloat() / totalSetsInWorkout.toFloat()).coerceIn(0f, 1f)
        } else 0f
}

data class FinishedWorkoutSummary(
    val routineName: String,
    val category: ExerciseCategory,
    val durationSeconds: Int,
    val exercisesCompleted: Int,
    val totalSetsCompleted: Int,
    val totalReps: Int,
    val totalHoldSeconds: Int,
    val xpEarned: Int,
    val prCount: Int,
    val newRanksAchieved: List<Pair<String, PerformanceRank>>
)

class LightningViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LightningRepository(application)
    val soundManager = SoundManager(application)

    // Current navigation state
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Sub-screens & Dialogs
    private val _showSoundSettings = MutableStateFlow(false)
    val showSoundSettings: StateFlow<Boolean> = _showSoundSettings.asStateFlow()

    private val _retestExercise = MutableStateFlow<Exercise?>(null)
    val retestExercise: StateFlow<Exercise?> = _retestExercise.asStateFlow()

    // Database Flows
    val athleteProfile: StateFlow<AthleteProfile> = repository.athleteProfile
    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.getAllWorkoutLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val routines: StateFlow<List<WorkoutRoutine>> = repository.getAllRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val performances: StateFlow<Map<String, ExercisePerformanceEntity>> = repository.getAllPerformances()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    val achievements: StateFlow<List<Achievement>> = repository.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val soundSettings: StateFlow<SoundSettings> = soundManager.settings

    // Active Workout state
    private val _activeWorkout = MutableStateFlow<ActiveWorkoutState?>(null)
    val activeWorkout: StateFlow<ActiveWorkoutState?> = _activeWorkout.asStateFlow()

    // Finished workout summary
    private val _finishedSummary = MutableStateFlow<FinishedWorkoutSummary?>(null)
    val finishedSummary: StateFlow<FinishedWorkoutSummary?> = _finishedSummary.asStateFlow()

    // Selected Routine for routine viewer/creator
    private val _selectedRoutine = MutableStateFlow<WorkoutRoutine?>(null)
    val selectedRoutine: StateFlow<WorkoutRoutine?> = _selectedRoutine.asStateFlow()

    // Active Achievement Notification Toast
    private val _activeAchievementNotification = MutableStateFlow<Achievement?>(null)
    val activeAchievementNotification: StateFlow<Achievement?> = _activeAchievementNotification.asStateFlow()

    // Timer jobs
    private var workoutElapsedJob: Job? = null
    private var exerciseTimerJob: Job? = null
    private var restTimerJob: Job? = null

    // Determine today's day of week: Monday=1 ... Sunday=7
    val currentDayOfWeek: Int
        get() = try {
            LocalDate.now().dayOfWeek.value // 1 (Monday) to 7 (Sunday)
        } catch (_: Exception) {
            1
        }

    init {
        // Listen for unlocked achievements and show onscreen notification
        repository.onAchievementUnlockedListener = { ach ->
            showAchievementNotification(ach)
        }

        // Play premium startup sound on initial boot
        viewModelScope.launch {
            delay(150)
            soundManager.playStartupSound()
        }
    }

    fun showAchievementNotification(achievement: Achievement) {
        soundManager.playAchievementUnlock()
        _activeAchievementNotification.value = achievement
        viewModelScope.launch {
            delay(4000)
            if (_activeAchievementNotification.value?.id == achievement.id) {
                _activeAchievementNotification.value = null
            }
        }
    }

    fun dismissAchievementNotification() {
        _activeAchievementNotification.value = null
    }

    fun finishSplash() {
        _currentScreen.value = AppScreen.Main
    }

    fun selectTab(tab: AppNavTab) {
        if (_currentTab.value != tab) {
            soundManager.playNavigation()
            _currentTab.value = tab
        }
    }

    fun openSoundSettings() {
        soundManager.playButtonClick()
        _showSoundSettings.value = true
    }

    fun closeSoundSettings() {
        soundManager.playButtonClick()
        _showSoundSettings.value = false
    }

    fun updateSoundSettings(newSettings: SoundSettings) {
        soundManager.updateSettings(newSettings)
    }

    fun openStage02ComingSoon() {
        soundManager.playButtonClick()
        _currentScreen.value = AppScreen.Stage02ComingSoon
    }

    fun closeStage02Modal() {
        soundManager.playButtonClick()
        _currentScreen.value = AppScreen.Main
    }

    fun openRetest(exercise: Exercise) {
        soundManager.playButtonClick()
        _retestExercise.value = exercise
    }

    fun closeRetest() {
        soundManager.playButtonClick()
        _retestExercise.value = null
    }

    fun submitRetestScore(exercise: Exercise, score: Int) {
        viewModelScope.launch {
            val (isNewPR, newRank) = repository.recordExercisePerformance(exercise, score)
            if (newRank == PerformanceRank.GOLD) {
                soundManager.playAchievementUnlock()
            } else if (isNewPR) {
                soundManager.playSetComplete()
            } else {
                soundManager.playButtonClick()
            }
            _retestExercise.value = null
        }
    }

    // --- Active Workout Logic ---
    fun startWorkout(routine: WorkoutRoutine) {
        soundManager.playButtonClick()
        cancelTimers()

        val firstEx = routine.exercises.firstOrNull()
        val initialReps = firstEx?.targetReps ?: 0
        val initialDur = firstEx?.targetDurationSeconds ?: 0

        _activeWorkout.value = ActiveWorkoutState(
            routine = routine,
            currentExerciseIndex = 0,
            currentSetIndex = 1,
            completedSetsMap = emptyMap(),
            currentRepsInput = initialReps,
            timerSecondsRemaining = initialDur,
            isExerciseTimerRunning = false,
            timerTotalDuration = initialDur,
            isTimerCompleted = false,
            isRestActive = false,
            restSecondsRemaining = 0,
            restTotalDuration = 0,
            elapsedSeconds = 0,
            isWorkoutPaused = false
        )

        _currentScreen.value = AppScreen.ActiveWorkout
        startElapsedTimer()
    }

    private fun startElapsedTimer() {
        workoutElapsedJob?.cancel()
        workoutElapsedJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _activeWorkout.value ?: break
                if (!state.isWorkoutPaused) {
                    _activeWorkout.value = state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                }
            }
        }
    }

    fun incrementReps() {
        soundManager.playButtonClick()
        _activeWorkout.value = _activeWorkout.value?.let {
            it.copy(currentRepsInput = it.currentRepsInput + 1)
        }
    }

    fun decrementReps() {
        soundManager.playButtonClick()
        _activeWorkout.value = _activeWorkout.value?.let {
            it.copy(currentRepsInput = (it.currentRepsInput - 1).coerceAtLeast(0))
        }
    }

    fun completeRepSet() {
        val state = _activeWorkout.value ?: return
        val currentEx = state.currentExercise ?: return

        soundManager.playSetComplete()

        val completedReps = state.currentRepsInput
        val currentList = state.completedSetsMap[currentEx.id]?.toMutableList() ?: mutableListOf()
        currentList.add(
            SetResult(
                exerciseId = currentEx.id,
                setNumber = state.currentSetIndex,
                repsOrSeconds = completedReps,
                isCompleted = true
            )
        )

        val updatedMap = state.completedSetsMap.toMutableMap()
        updatedMap[currentEx.id] = currentList

        // Check if more sets remain for this exercise
        if (state.currentSetIndex < currentEx.targetSets) {
            // Start rest timer
            val restSec = currentEx.restSeconds
            _activeWorkout.value = state.copy(
                completedSetsMap = updatedMap,
                currentSetIndex = state.currentSetIndex + 1,
                isRestActive = true,
                restSecondsRemaining = restSec,
                restTotalDuration = restSec
            )
            startRestTimer(restSec)
        } else {
            // Exercise finished! Move to next exercise or finish workout
            if (state.currentExerciseIndex < state.routine.exercises.lastIndex) {
                val nextIdx = state.currentExerciseIndex + 1
                val nextEx = state.routine.exercises[nextIdx]
                val restSec = currentEx.restSeconds
                _activeWorkout.value = state.copy(
                    completedSetsMap = updatedMap,
                    currentExerciseIndex = nextIdx,
                    currentSetIndex = 1,
                    currentRepsInput = nextEx.targetReps,
                    timerSecondsRemaining = nextEx.targetDurationSeconds,
                    timerTotalDuration = nextEx.targetDurationSeconds,
                    isExerciseTimerRunning = false,
                    isTimerCompleted = false,
                    isRestActive = true,
                    restSecondsRemaining = restSec,
                    restTotalDuration = restSec
                )
                startRestTimer(restSec)
            } else {
                // Workout completed!
                _activeWorkout.value = state.copy(completedSetsMap = updatedMap)
                finishActiveWorkout()
            }
        }
    }

    // --- Timed Exercise Controls ---
    fun startExerciseTimer() {
        val state = _activeWorkout.value ?: return
        soundManager.playButtonClick()
        _activeWorkout.value = state.copy(isExerciseTimerRunning = true, isTimerCompleted = false)

        exerciseTimerJob?.cancel()
        exerciseTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val cur = _activeWorkout.value ?: break
                if (!cur.isExerciseTimerRunning) break

                val remaining = cur.timerSecondsRemaining - 1
                if (remaining <= 0) {
                    // Timer finished!
                    soundManager.playTimerComplete()
                    _activeWorkout.value = cur.copy(
                        timerSecondsRemaining = 0,
                        isExerciseTimerRunning = false,
                        isTimerCompleted = true
                    )
                    break
                } else {
                    _activeWorkout.value = cur.copy(timerSecondsRemaining = remaining)
                }
            }
        }
    }

    fun pauseExerciseTimer() {
        soundManager.playButtonClick()
        exerciseTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value?.copy(isExerciseTimerRunning = false)
    }

    fun resumeExerciseTimer() {
        startExerciseTimer()
    }

    fun completeTimedSet() {
        val state = _activeWorkout.value ?: return
        val currentEx = state.currentExercise ?: return

        exerciseTimerJob?.cancel()
        soundManager.playSetComplete()

        val holdTime = state.timerTotalDuration - state.timerSecondsRemaining
        val actualSeconds = if (state.isTimerCompleted) state.timerTotalDuration else holdTime.coerceAtLeast(1)

        val currentList = state.completedSetsMap[currentEx.id]?.toMutableList() ?: mutableListOf()
        currentList.add(
            SetResult(
                exerciseId = currentEx.id,
                setNumber = state.currentSetIndex,
                repsOrSeconds = actualSeconds,
                isCompleted = true
            )
        )

        val updatedMap = state.completedSetsMap.toMutableMap()
        updatedMap[currentEx.id] = currentList

        if (state.currentSetIndex < currentEx.targetSets) {
            val restSec = currentEx.restSeconds
            _activeWorkout.value = state.copy(
                completedSetsMap = updatedMap,
                currentSetIndex = state.currentSetIndex + 1,
                timerSecondsRemaining = currentEx.targetDurationSeconds,
                timerTotalDuration = currentEx.targetDurationSeconds,
                isExerciseTimerRunning = false,
                isTimerCompleted = false,
                isRestActive = true,
                restSecondsRemaining = restSec,
                restTotalDuration = restSec
            )
            startRestTimer(restSec)
        } else {
            if (state.currentExerciseIndex < state.routine.exercises.lastIndex) {
                val nextIdx = state.currentExerciseIndex + 1
                val nextEx = state.routine.exercises[nextIdx]
                val restSec = currentEx.restSeconds
                _activeWorkout.value = state.copy(
                    completedSetsMap = updatedMap,
                    currentExerciseIndex = nextIdx,
                    currentSetIndex = 1,
                    currentRepsInput = nextEx.targetReps,
                    timerSecondsRemaining = nextEx.targetDurationSeconds,
                    timerTotalDuration = nextEx.targetDurationSeconds,
                    isExerciseTimerRunning = false,
                    isTimerCompleted = false,
                    isRestActive = true,
                    restSecondsRemaining = restSec,
                    restTotalDuration = restSec
                )
                startRestTimer(restSec)
            } else {
                _activeWorkout.value = state.copy(completedSetsMap = updatedMap)
                finishActiveWorkout()
            }
        }
    }

    // --- Rest Timer Controls ---
    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            var s = seconds
            while (s > 0) {
                delay(1000)
                s--
                val cur = _activeWorkout.value ?: break
                if (!cur.isRestActive) break
                _activeWorkout.value = cur.copy(restSecondsRemaining = s)
            }
            if (_activeWorkout.value?.isRestActive == true) {
                soundManager.playTimerComplete()
                skipRest()
            }
        }
    }

    fun skipRest() {
        soundManager.playButtonClick()
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value?.copy(isRestActive = false, restSecondsRemaining = 0)
    }

    fun add30SecondsRest() {
        soundManager.playButtonClick()
        val cur = _activeWorkout.value ?: return
        val newTime = cur.restSecondsRemaining + 30
        _activeWorkout.value = cur.copy(
            restSecondsRemaining = newTime,
            restTotalDuration = cur.restTotalDuration + 30
        )
    }

    fun exitActiveWorkout(savePartial: Boolean = false) {
        soundManager.playButtonClick()
        if (savePartial) {
            finishActiveWorkout()
        } else {
            cancelTimers()
            _activeWorkout.value = null
            _currentScreen.value = AppScreen.Main
        }
    }

    private fun finishActiveWorkout() {
        cancelTimers()
        val state = _activeWorkout.value ?: return
        val routine = state.routine

        viewModelScope.launch {
            var totalReps = 0
            var totalHoldSec = 0
            var totalSets = 0
            var prCount = 0
            val newRanks = mutableListOf<Pair<String, PerformanceRank>>()

            val allCompleted = state.completedSetsMap.values.flatten().filter { it.isCompleted }
            totalSets = allCompleted.size

            for (ex in routine.exercises) {
                val setsForEx = state.completedSetsMap[ex.id]?.filter { it.isCompleted } ?: emptyList()
                if (setsForEx.isNotEmpty()) {
                    val maxVal = setsForEx.maxOf { it.repsOrSeconds }
                    if (ex.type == ExerciseType.REPS) {
                        totalReps += setsForEx.sumOf { it.repsOrSeconds }
                    } else {
                        totalHoldSec += setsForEx.sumOf { it.repsOrSeconds }
                    }

                    // Record performance
                    val (isPR, rank) = repository.recordExercisePerformance(ex, maxVal)
                    if (isPR) prCount++
                    if (rank != PerformanceRank.UNRANKED) {
                        newRanks.add(Pair(ex.name, rank))
                    }
                }
            }

            // XP: base 100 + 10 per completed set + 25 per PR
            val xpEarned = 100 + (totalSets * 10) + (prCount * 25)

            val log = WorkoutLog(
                timestamp = System.currentTimeMillis(),
                routineId = routine.id,
                routineName = routine.name,
                category = routine.category,
                durationSeconds = state.elapsedSeconds.coerceAtLeast(60),
                exercisesCompleted = state.completedSetsMap.keys.size,
                setsCompleted = totalSets,
                totalReps = totalReps,
                totalHoldSeconds = totalHoldSec,
                xpEarned = xpEarned,
                personalRecordsCount = prCount
            )

            repository.saveWorkoutLog(log)
            soundManager.playWorkoutComplete()

            _finishedSummary.value = FinishedWorkoutSummary(
                routineName = routine.name,
                category = routine.category,
                durationSeconds = state.elapsedSeconds,
                exercisesCompleted = log.exercisesCompleted,
                totalSetsCompleted = totalSets,
                totalReps = totalReps,
                totalHoldSeconds = totalHoldSec,
                xpEarned = xpEarned,
                prCount = prCount,
                newRanksAchieved = newRanks
            )

            _activeWorkout.value = null
            _currentScreen.value = AppScreen.WorkoutSummary
        }
    }

    fun dismissSummary() {
        soundManager.playButtonClick()
        _finishedSummary.value = null
        _currentScreen.value = AppScreen.Main
    }

    private fun cancelTimers() {
        workoutElapsedJob?.cancel()
        exerciseTimerJob?.cancel()
        restTimerJob?.cancel()
    }

    // --- Routine Management ---
    fun openCreateRoutine() {
        soundManager.playButtonClick()
        val newRoutine = WorkoutRoutine(
            id = "custom_${UUID.randomUUID().toString().take(8)}",
            name = "Custom Routine",
            description = "Custom targeted calisthenics routine.",
            category = ExerciseCategory.PUSH,
            assignedDay = 1,
            isDefault = false,
            exercises = listOf(DefaultRoutines.pushExercises.first())
        )
        _currentScreen.value = AppScreen.EditRoutine(newRoutine)
    }

    fun openEditRoutine(routine: WorkoutRoutine) {
        soundManager.playButtonClick()
        _currentScreen.value = AppScreen.EditRoutine(routine)
    }

    fun closeEditRoutine() {
        soundManager.playButtonClick()
        _currentScreen.value = AppScreen.Main
    }

    fun saveRoutine(routine: WorkoutRoutine) {
        viewModelScope.launch {
            soundManager.playButtonClick()
            repository.saveCustomRoutine(routine)
            _currentScreen.value = AppScreen.Main
        }
    }

    fun duplicateRoutine(routine: WorkoutRoutine) {
        viewModelScope.launch {
            soundManager.playButtonClick()
            val dup = routine.copy(
                id = "custom_${UUID.randomUUID().toString().take(8)}",
                name = "${routine.name} (Copy)",
                isDefault = false
            )
            repository.saveCustomRoutine(dup)
        }
    }

    fun deleteRoutine(routine: WorkoutRoutine) {
        if (routine.isDefault) return // Protected!
        viewModelScope.launch {
            soundManager.playButtonClick()
            repository.deleteRoutine(routine.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimers()
    }
}
