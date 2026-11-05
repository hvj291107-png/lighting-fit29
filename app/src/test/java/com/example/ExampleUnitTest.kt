package com.example

import com.example.data.ExercisePerformanceEntity
import com.example.model.DefaultRoutines
import com.example.model.Exercise
import com.example.model.PerformanceRank
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  private fun createPerf(ex: Exercise, rank: PerformanceRank, best: Int): ExercisePerformanceEntity {
    return ExercisePerformanceEntity(
      exerciseId = ex.id,
      exerciseName = ex.name,
      category = ex.category.name,
      bestValue = best,
      currentRank = rank.name,
      lastUpdatedTimestamp = System.currentTimeMillis()
    )
  }

  @Test
  fun testRoutineStageRankCalculation() {
    val pushRoutine = DefaultRoutines.getAllDefaultRoutines().first { it.id == "routine_mon" }
    assertEquals(4, pushRoutine.exercises.size)

    // 1. All unranked -> Stage is UNRANKED
    val emptyPerfs = emptyMap<String, ExercisePerformanceEntity>()
    var status = DefaultRoutines.calculateRoutineStageRank(pushRoutine, emptyPerfs)
    assertEquals(PerformanceRank.UNRANKED, status.rank)
    assertEquals(PerformanceRank.BRONZE, status.nextRank)
    assertEquals(0, status.exercisesAtOrAboveNext)

    // 2. All 4 exercises are BRONZE -> Stage is BRONZE
    val allBronze = pushRoutine.exercises.associate { ex ->
      ex.id to createPerf(ex, PerformanceRank.BRONZE, 10)
    }
    status = DefaultRoutines.calculateRoutineStageRank(pushRoutine, allBronze)
    assertEquals(PerformanceRank.BRONZE, status.rank)
    assertEquals(PerformanceRank.SILVER, status.nextRank)
    assertEquals(0, status.exercisesAtOrAboveNext)

    // 3. User reaches SILVER in Push-ups, but the other 3 remain BRONZE -> Stage MUST REMAIN BRONZE
    val oneSilverRestBronze = allBronze.toMutableMap().apply {
      val pushStd = pushRoutine.exercises.first { it.id == "push_std" }
      put("push_std", createPerf(pushStd, PerformanceRank.SILVER, 15))
    }
    status = DefaultRoutines.calculateRoutineStageRank(pushRoutine, oneSilverRestBronze)
    assertEquals(PerformanceRank.BRONZE, status.rank) // Still Bronze!
    assertEquals(PerformanceRank.SILVER, status.nextRank)
    assertEquals(1, status.exercisesAtOrAboveNext) // 1 of 4 qualified for Silver
    assertEquals(3, status.laggingExercises.size)

    // 4. User earns SILVER in each and every exercise -> Stage OVERALL BECOMES SILVER
    val allSilver = pushRoutine.exercises.associate { ex ->
      ex.id to createPerf(ex, PerformanceRank.SILVER, 15)
    }
    status = DefaultRoutines.calculateRoutineStageRank(pushRoutine, allSilver)
    assertEquals(PerformanceRank.SILVER, status.rank) // Now Silver!
    assertEquals(PerformanceRank.GOLD, status.nextRank)
    assertEquals(0, status.exercisesAtOrAboveNext)

    // 5. User reaches GOLD in 3 exercises, 1 is still SILVER -> Stage MUST REMAIN SILVER
    val threeGoldOneSilver = allSilver.toMutableMap().apply {
      pushRoutine.exercises.take(3).forEach { ex ->
        put(ex.id, createPerf(ex, PerformanceRank.GOLD, 25))
      }
    }
    status = DefaultRoutines.calculateRoutineStageRank(pushRoutine, threeGoldOneSilver)
    assertEquals(PerformanceRank.SILVER, status.rank) // Still Silver!
    assertEquals(PerformanceRank.GOLD, status.nextRank)
    assertEquals(3, status.exercisesAtOrAboveNext)

    // 6. All reach GOLD -> Stage BECOMES GOLD
    val allGold = pushRoutine.exercises.associate { ex ->
      ex.id to createPerf(ex, PerformanceRank.GOLD, 25)
    }
    status = DefaultRoutines.calculateRoutineStageRank(pushRoutine, allGold)
    assertEquals(PerformanceRank.GOLD, status.rank)
    assertEquals(null, status.nextRank)
    assertEquals(4, status.exercisesAtOrAboveNext)
  }
}
