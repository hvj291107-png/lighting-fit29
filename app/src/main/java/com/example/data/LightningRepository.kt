package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Achievement
import com.example.model.AchievementRarity
import com.example.model.AthleteProfile
import com.example.model.DefaultAchievements
import com.example.model.DefaultRoutines
import com.example.model.Exercise
import com.example.model.ExerciseCategory
import com.example.model.ExerciseLibrary
import com.example.model.ExerciseType
import com.example.model.PerformanceRank
import com.example.model.WorkoutLog
import com.example.model.WorkoutRoutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class LightningRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val workoutLogDao = db.workoutLogDao()
    private val customRoutineDao = db.customRoutineDao()
    private val performanceDao = db.exercisePerformanceDao()
    private val achievementDao = db.achievementDao()

    private val prefs: SharedPreferences =
        context.getSharedPreferences("lightning_fits_profile", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _athleteProfile = MutableStateFlow(loadProfile())
    val athleteProfile: StateFlow<AthleteProfile> = _athleteProfile.asStateFlow()

    init {
        scope.launch {
            seedDefaultsIfEmpty()
        }
    }

    private suspend fun seedDefaultsIfEmpty() {
        // Seed default routines if none exist
        val existingRoutines = customRoutineDao.getAllRoutines().firstOrNull()
        if (existingRoutines.isNullOrEmpty()) {
            val defaults = DefaultRoutines.getAllDefaultRoutines()
            for (routine in defaults) {
                customRoutineDao.insertRoutine(
                    CustomRoutineEntity(
                        id = routine.id,
                        name = routine.name,
                        description = routine.description,
                        category = routine.category.name,
                        assignedDay = routine.assignedDay,
                        isDefault = routine.isDefault,
                        exercisesJson = serializeExercises(routine.exercises)
                    )
                )
            }
        }

        // Seed achievements if none exist
        val existingAchievements = achievementDao.getAllAchievements().firstOrNull()
        if (existingAchievements.isNullOrEmpty()) {
            val defaultAchEntities = DefaultAchievements.initialList.map {
                AchievementEntity(
                    id = it.id,
                    isUnlocked = false,
                    unlockedTimestamp = 0L
                )
            }
            achievementDao.insertAll(defaultAchEntities)
        }
    }

    // --- Profile Preferences ---
    private fun loadProfile(): AthleteProfile {
        return AthleteProfile(
            name = prefs.getString("athlete_name", "Athlete") ?: "Athlete",
            title = prefs.getString("athlete_title", "Calisthenics Cadet") ?: "Calisthenics Cadet",
            level = prefs.getInt("athlete_level", 1),
            currentXp = prefs.getInt("athlete_xp", 0),
            xpForNextLevel = prefs.getInt("athlete_xp_next", 500),
            totalWorkouts = prefs.getInt("total_workouts", 0),
            totalSets = prefs.getInt("total_sets", 0),
            totalReps = prefs.getInt("total_reps", 0),
            totalTrainingSeconds = prefs.getLong("total_seconds", 0L),
            currentStreak = prefs.getInt("current_streak", 0)
        )
    }

    fun updateProfileName(name: String) {
        val updated = _athleteProfile.value.copy(name = name)
        _athleteProfile.value = updated
        prefs.edit().putString("athlete_name", name).apply()
    }

    suspend fun addXpAndStats(
        earnedXp: Int,
        setsDone: Int,
        repsDone: Int,
        durationSeconds: Int
    ) {
        val current = _athleteProfile.value
        var newXp = current.currentXp + earnedXp
        var newLevel = current.level
        var nextTarget = current.xpForNextLevel

        while (newXp >= nextTarget) {
            newXp -= nextTarget
            newLevel++
            nextTarget = (nextTarget * 1.25).toInt()
        }

        val totalW = current.totalWorkouts + 1
        val totalS = current.totalSets + setsDone
        val totalR = current.totalReps + repsDone
        val totalSec = current.totalTrainingSeconds + durationSeconds
        val newStreak = (current.currentStreak + 1).coerceAtLeast(1)

        val updated = current.copy(
            level = newLevel,
            currentXp = newXp,
            xpForNextLevel = nextTarget,
            totalWorkouts = totalW,
            totalSets = totalS,
            totalReps = totalR,
            totalTrainingSeconds = totalSec,
            currentStreak = newStreak
        )

        _athleteProfile.value = updated

        prefs.edit()
            .putInt("athlete_level", newLevel)
            .putInt("athlete_xp", newXp)
            .putInt("athlete_xp_next", nextTarget)
            .putInt("total_workouts", totalW)
            .putInt("total_sets", totalS)
            .putInt("total_reps", totalR)
            .putLong("total_seconds", totalSec)
            .putInt("current_streak", newStreak)
            .apply()
    }

    // --- Workout Logs ---
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>> {
        return workoutLogDao.getAllLogs().map { list ->
            list.map { it.toModel() }
        }
    }

    suspend fun saveWorkoutLog(log: WorkoutLog): Long {
        val id = workoutLogDao.insertLog(log.toEntity())
        addXpAndStats(log.xpEarned, log.setsCompleted, log.totalReps, log.durationSeconds)
        checkAchievementsAfterWorkout(log)
        return id
    }

    // --- Routines ---
    fun getAllRoutines(): Flow<List<WorkoutRoutine>> {
        return customRoutineDao.getAllRoutines().map { list ->
            list.map { it.toModel() }
        }
    }

    suspend fun saveCustomRoutine(routine: WorkoutRoutine) {
        customRoutineDao.insertRoutine(
            CustomRoutineEntity(
                id = routine.id,
                name = routine.name,
                description = routine.description,
                category = routine.category.name,
                assignedDay = routine.assignedDay,
                isDefault = routine.isDefault,
                exercisesJson = serializeExercises(routine.exercises)
            )
        )
    }

    suspend fun deleteRoutine(id: String) {
        customRoutineDao.deleteCustomRoutineById(id)
    }

    // --- Performance & Ranks ---
    fun getAllPerformances(): Flow<Map<String, ExercisePerformanceEntity>> {
        return performanceDao.getAllPerformances().map { list ->
            list.associateBy { it.exerciseId }
        }
    }

    suspend fun recordExercisePerformance(
        exercise: Exercise,
        actualScore: Int
    ): Pair<Boolean, PerformanceRank> {
        val existing = performanceDao.getPerformance(exercise.id)
        val prevBest = existing?.bestValue ?: 0
        val isNewPR = actualScore > prevBest
        val newBest = maxOf(actualScore, prevBest)

        val newRank = exercise.calculateRank(newBest)
        val entity = ExercisePerformanceEntity(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            category = exercise.category.name,
            bestValue = newBest,
            currentRank = newRank.name,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        performanceDao.insertOrUpdate(entity)

        if (isNewPR) {
            unlockAchievement("ach_pr")
        }
        if (newRank == PerformanceRank.GOLD) {
            unlockAchievement("ach_first_gold")
            checkStage01Mastery()
        }

        return Pair(isNewPR, newRank)
    }

    // --- Achievements ---
    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { storedList ->
            val storedMap = storedList.associateBy { it.id }
            DefaultAchievements.initialList.map { def ->
                val record = storedMap[def.id]
                if (record != null && record.isUnlocked) {
                    def.copy(isUnlocked = true, unlockedTimestamp = record.unlockedTimestamp)
                } else {
                    def.copy(isUnlocked = false)
                }
            }
        }
    }

    var onAchievementUnlockedListener: ((Achievement) -> Unit)? = null

    suspend fun unlockAchievement(id: String): Boolean {
        val existing = achievementDao.getAllAchievements().firstOrNull()?.firstOrNull { it.id == id }
        if (existing != null && existing.isUnlocked) {
            return false // Already unlocked
        }
        val entity = AchievementEntity(
            id = id,
            isUnlocked = true,
            unlockedTimestamp = System.currentTimeMillis()
        )
        achievementDao.insertOrUpdate(entity)
        DefaultAchievements.findById(id)?.let { ach ->
            onAchievementUnlockedListener?.invoke(ach.copy(isUnlocked = true, unlockedTimestamp = entity.unlockedTimestamp))
        }
        return true
    }

    private suspend fun checkAchievementsAfterWorkout(log: WorkoutLog) {
        unlockAchievement("ach_started")

        when (log.category) {
            ExerciseCategory.PUSH -> {
                unlockAchievement("ach_first_pushup")
                unlockAchievement("ach_push_cleared")
            }
            ExerciseCategory.PULL -> {
                unlockAchievement("ach_first_chinup")
                unlockAchievement("ach_pull_cleared")
            }
            ExerciseCategory.LEGS -> unlockAchievement("ach_legs_cleared")
            ExerciseCategory.CORE_STABILITY -> unlockAchievement("ach_core_stab_cleared")
            ExerciseCategory.CORE_STRENGTH -> unlockAchievement("ach_core_str_cleared")
            ExerciseCategory.ATHLETE -> unlockAchievement("ach_ath_cleared")
        }

        val totalW = _athleteProfile.value.totalWorkouts
        if (totalW >= 10) unlockAchievement("ach_10_workouts")
        if (totalW >= 50) unlockAchievement("ach_50_workouts")

        val streak = _athleteProfile.value.currentStreak
        if (streak >= 7) unlockAchievement("ach_7_streak")
        if (streak >= 30) unlockAchievement("ach_30_streak")

        checkStage01Mastery()
    }

    suspend fun checkStage01Mastery(): Boolean {
        val required = DefaultRoutines.stage01RequiredExercises
        val allPerfs = performanceDao.getAllPerformances().firstOrNull()?.associateBy { it.exerciseId } ?: emptyMap()

        val allGold = required.all { ex ->
            val p = allPerfs[ex.id]
            p != null && p.currentRank == PerformanceRank.GOLD.name
        }

        if (allGold) {
            unlockAchievement("ach_stage01_mastered")
        }
        return allGold
    }

    // --- JSON Helpers ---
    private fun serializeExercises(exercises: List<Exercise>): String {
        val array = JSONArray()
        for (ex in exercises) {
            val obj = JSONObject()
            obj.put("id", ex.id)
            obj.put("name", ex.name)
            obj.put("category", ex.category.name)
            obj.put("type", ex.type.name)
            obj.put("targetSets", ex.targetSets)
            obj.put("targetReps", ex.targetReps)
            obj.put("targetDurationSeconds", ex.targetDurationSeconds)
            obj.put("restSeconds", ex.restSeconds)
            obj.put("bronzeThreshold", ex.bronzeThreshold)
            obj.put("silverThreshold", ex.silverThreshold)
            obj.put("goldThreshold", ex.goldThreshold)
            obj.put("unit", ex.unit)

            val cuesArray = JSONArray()
            ex.cues.forEach { cuesArray.put(it) }
            obj.put("cues", cuesArray)

            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializeExercises(json: String): List<Exercise> {
        val list = mutableListOf<Exercise>()
        if (json.isBlank()) return list
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val cuesList = mutableListOf<String>()
                val cuesArray = obj.optJSONArray("cues")
                if (cuesArray != null) {
                    for (c in 0 until cuesArray.length()) {
                        cuesList.add(cuesArray.getString(c))
                    }
                }
                val type = try {
                    ExerciseType.valueOf(obj.getString("type"))
                } catch (_: Exception) {
                    ExerciseType.REPS
                }
                val cat = try {
                    ExerciseCategory.valueOf(obj.getString("category"))
                } catch (_: Exception) {
                    ExerciseCategory.PUSH
                }
                list.add(
                    Exercise(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        category = cat,
                        type = type,
                        targetSets = obj.optInt("targetSets", 3),
                        targetReps = obj.optInt("targetReps", 5),
                        targetDurationSeconds = obj.optInt("targetDurationSeconds", 0),
                        restSeconds = obj.optInt("restSeconds", 60),
                        cues = cuesList,
                        bronzeThreshold = obj.optInt("bronzeThreshold", 5),
                        silverThreshold = obj.optInt("silverThreshold", 10),
                        goldThreshold = obj.optInt("goldThreshold", 20),
                        unit = obj.optString("unit", if (type == ExerciseType.REPS) "reps" else "seconds")
                    )
                )
            }
        } catch (_: Exception) {
            // Return empty list on parse failure
        }
        return list
    }

    private fun CustomRoutineEntity.toModel(): WorkoutRoutine {
        return WorkoutRoutine(
            id = id,
            name = name,
            description = description,
            category = try {
                ExerciseCategory.valueOf(category)
            } catch (_: Exception) {
                ExerciseCategory.PUSH
            },
            assignedDay = assignedDay,
            isDefault = isDefault,
            exercises = deserializeExercises(exercisesJson)
        )
    }

    private fun WorkoutLogEntity.toModel(): WorkoutLog {
        return WorkoutLog(
            id = id,
            timestamp = timestamp,
            routineId = routineId,
            routineName = routineName,
            category = try {
                ExerciseCategory.valueOf(category)
            } catch (_: Exception) {
                ExerciseCategory.PUSH
            },
            durationSeconds = durationSeconds,
            exercisesCompleted = exercisesCompleted,
            setsCompleted = setsCompleted,
            totalReps = totalReps,
            totalHoldSeconds = totalHoldSeconds,
            xpEarned = xpEarned,
            personalRecordsCount = personalRecordsCount
        )
    }

    private fun WorkoutLog.toEntity(): WorkoutLogEntity {
        return WorkoutLogEntity(
            id = id,
            timestamp = timestamp,
            routineId = routineId,
            routineName = routineName,
            category = category.name,
            durationSeconds = durationSeconds,
            exercisesCompleted = exercisesCompleted,
            setsCompleted = setsCompleted,
            totalReps = totalReps,
            totalHoldSeconds = totalHoldSeconds,
            xpEarned = xpEarned,
            personalRecordsCount = personalRecordsCount
        )
    }
}
