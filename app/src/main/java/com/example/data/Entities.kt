package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val routineId: String,
    val routineName: String,
    val category: String,
    val durationSeconds: Int,
    val exercisesCompleted: Int,
    val setsCompleted: Int,
    val totalReps: Int,
    val totalHoldSeconds: Int,
    val xpEarned: Int,
    val personalRecordsCount: Int
)

@Entity(tableName = "custom_routines")
data class CustomRoutineEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val assignedDay: Int,
    val isDefault: Boolean = false,
    val exercisesJson: String // Serialized list of exercises
)

@Entity(tableName = "exercise_performance")
data class ExercisePerformanceEntity(
    @PrimaryKey
    val exerciseId: String,
    val exerciseName: String,
    val category: String,
    val bestValue: Int, // Best reps or best duration seconds
    val currentRank: String, // UNRANKED, BRONZE, SILVER, GOLD
    val lastUpdatedTimestamp: Long
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val isUnlocked: Boolean,
    val unlockedTimestamp: Long
)
