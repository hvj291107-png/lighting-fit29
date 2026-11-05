package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE category = :category ORDER BY timestamp DESC")
    fun getLogsByCategory(category: String): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WorkoutLogEntity): Long

    @Query("SELECT COUNT(*) FROM workout_logs")
    suspend fun getCount(): Int

    @Query("SELECT SUM(durationSeconds) FROM workout_logs")
    suspend fun getTotalDurationSeconds(): Long?

    @Query("SELECT SUM(totalReps) FROM workout_logs")
    suspend fun getTotalReps(): Int?

    @Query("SELECT SUM(setsCompleted) FROM workout_logs")
    suspend fun getTotalSets(): Int?
}

@Dao
interface CustomRoutineDao {
    @Query("SELECT * FROM custom_routines ORDER BY assignedDay ASC, name ASC")
    fun getAllRoutines(): Flow<List<CustomRoutineEntity>>

    @Query("SELECT * FROM custom_routines WHERE id = :id LIMIT 1")
    suspend fun getRoutineById(id: String): CustomRoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: CustomRoutineEntity)

    @Update
    suspend fun updateRoutine(routine: CustomRoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: CustomRoutineEntity)

    @Query("DELETE FROM custom_routines WHERE id = :id AND isDefault = 0")
    suspend fun deleteCustomRoutineById(id: String)
}

@Dao
interface ExercisePerformanceDao {
    @Query("SELECT * FROM exercise_performance")
    fun getAllPerformances(): Flow<List<ExercisePerformanceEntity>>

    @Query("SELECT * FROM exercise_performance WHERE category = :category")
    fun getPerformancesByCategory(category: String): Flow<List<ExercisePerformanceEntity>>

    @Query("SELECT * FROM exercise_performance WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getPerformance(exerciseId: String): ExercisePerformanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(performance: ExercisePerformanceEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<AchievementEntity>)
}
