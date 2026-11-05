package com.example.model

enum class ExerciseType {
    REPS,
    TIME
}

enum class ExerciseCategory(val displayName: String) {
    PUSH("Push"),
    PULL("Pull"),
    LEGS("Legs"),
    CORE_STABILITY("Core Stability"),
    CORE_STRENGTH("Core Strength"),
    ATHLETE("Athlete")
}

enum class PerformanceRank(val displayName: String, val levelNumber: Int) {
    UNRANKED("Unranked", 0),
    BRONZE("Bronze", 1),
    SILVER("Silver", 2),
    GOLD("Gold", 3)
}

data class Exercise(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val type: ExerciseType,
    val targetSets: Int,
    val targetReps: Int = 0,
    val targetDurationSeconds: Int = 0,
    val restSeconds: Int = 60,
    val cues: List<String> = emptyList(),
    val bronzeThreshold: Int,
    val silverThreshold: Int,
    val goldThreshold: Int,
    val unit: String = if (type == ExerciseType.REPS) "reps" else "seconds"
) {
    fun calculateRank(actualMax: Int): PerformanceRank {
        return when {
            actualMax >= goldThreshold -> PerformanceRank.GOLD
            actualMax >= silverThreshold -> PerformanceRank.SILVER
            actualMax >= bronzeThreshold -> PerformanceRank.BRONZE
            else -> PerformanceRank.UNRANKED
        }
    }

    fun nextRankTarget(actualMax: Int): Int {
        return when {
            actualMax < bronzeThreshold -> bronzeThreshold
            actualMax < silverThreshold -> silverThreshold
            actualMax < goldThreshold -> goldThreshold
            else -> goldThreshold
        }
    }

    fun progressFractionToNext(actualMax: Int): Float {
        return when {
            actualMax < bronzeThreshold -> (actualMax.toFloat() / bronzeThreshold.toFloat()).coerceIn(0f, 1f)
            actualMax < silverThreshold -> {
                val range = silverThreshold - bronzeThreshold
                val cur = actualMax - bronzeThreshold
                (cur.toFloat() / range.toFloat()).coerceIn(0f, 1f)
            }
            actualMax < goldThreshold -> {
                val range = goldThreshold - silverThreshold
                val cur = actualMax - silverThreshold
                (cur.toFloat() / range.toFloat()).coerceIn(0f, 1f)
            }
            else -> 1.0f
        }
    }
}
