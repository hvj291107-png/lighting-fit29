package com.example.model

data class WorkoutLog(
    val id: Long = 0,
    val timestamp: Long,
    val routineId: String,
    val routineName: String,
    val category: ExerciseCategory,
    val durationSeconds: Int,
    val exercisesCompleted: Int,
    val setsCompleted: Int,
    val totalReps: Int,
    val totalHoldSeconds: Int,
    val xpEarned: Int,
    val personalRecordsCount: Int
)

data class AthleteProfile(
    val name: String = "Athlete",
    val title: String = "Calisthenics Cadet",
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpForNextLevel: Int = 500,
    val totalWorkouts: Int = 0,
    val totalSets: Int = 0,
    val totalReps: Int = 0,
    val totalTrainingSeconds: Long = 0L,
    val currentStreak: Int = 0
) {
    val progressFraction: Float
        get() = (currentXp.toFloat() / xpForNextLevel.toFloat()).coerceIn(0f, 1f)

    val overallRank: PerformanceRank
        get() = when {
            level >= 10 -> PerformanceRank.GOLD
            level >= 5 -> PerformanceRank.SILVER
            level >= 1 && totalWorkouts > 0 -> PerformanceRank.BRONZE
            else -> PerformanceRank.UNRANKED
        }

    val rankTitle: String
        get() = when {
            level >= 15 -> "Titan"
            level >= 10 -> "Acrobat"
            level >= 7 -> "Gymnast"
            level >= 4 -> "Practitioner"
            level >= 2 -> "Initiate"
            else -> "Cadet"
        }
}
