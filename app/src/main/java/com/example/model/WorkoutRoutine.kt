package com.example.model

data class WorkoutRoutine(
    val id: String,
    val name: String,
    val description: String,
    val category: ExerciseCategory,
    val assignedDay: Int, // 1 = Monday, ..., 7 = Sunday
    val isDefault: Boolean = false,
    val exercises: List<Exercise> = emptyList()
) {
    val dayName: String
        get() = when (assignedDay) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            7 -> "Sunday"
            else -> "Any Day"
        }

    val totalSets: Int
        get() = exercises.sumOf { it.targetSets }

    val estimatedMinutes: Int
        get() {
            var totalSeconds = 0
            for (ex in exercises) {
                if (ex.type == ExerciseType.REPS) {
                    // ~3 seconds per rep + rest
                    totalSeconds += ex.targetSets * (ex.targetReps * 3 + ex.restSeconds)
                } else {
                    totalSeconds += ex.targetSets * (ex.targetDurationSeconds + ex.restSeconds)
                }
            }
            return ((totalSeconds / 60) + 5).coerceAtLeast(15)
        }
}
