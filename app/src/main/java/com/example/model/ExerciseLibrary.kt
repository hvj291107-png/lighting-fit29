package com.example.model

object ExerciseLibrary {
    val allExercises: List<Exercise> by lazy {
        (DefaultRoutines.pushExercises +
                DefaultRoutines.pullExercises +
                DefaultRoutines.legsExercises +
                DefaultRoutines.coreStabilityExercises +
                DefaultRoutines.coreStrengthExercises +
                DefaultRoutines.athleteExercises).distinctBy { it.id }
    }

    fun findById(id: String): Exercise? {
        return allExercises.firstOrNull { it.id == id }
    }
}
