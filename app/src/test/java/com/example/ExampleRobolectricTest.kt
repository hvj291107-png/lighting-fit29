package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.DefaultRoutines
import com.example.model.PerformanceRank
import com.example.sound.SoundManager
import com.example.sound.SoundSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Lightning Fits", appName)
    }

    @Test
    fun testStage01WeeklySchedule() {
        val routines = DefaultRoutines.getAllDefaultRoutines()
        assertEquals(7, routines.size)

        val monday = DefaultRoutines.getRoutineForDay(1)
        assertEquals("Push Stage 01", monday.name)
        assertTrue(monday.exercises.isNotEmpty())

        val tuesday = DefaultRoutines.getRoutineForDay(2)
        assertEquals("Pull Stage 01", tuesday.name)

        val wednesday = DefaultRoutines.getRoutineForDay(3)
        assertEquals("Legs Stage 01", wednesday.name)

        val thursday = DefaultRoutines.getRoutineForDay(4)
        assertEquals("Core Stability Stage 01", thursday.name)

        val friday = DefaultRoutines.getRoutineForDay(5)
        assertEquals("Core Strength Stage 01", friday.name)

        val saturday = DefaultRoutines.getRoutineForDay(6)
        assertEquals("Athlete Stage 01", saturday.name)

        val sunday = DefaultRoutines.getRoutineForDay(7)
        assertEquals("Complete Rest", sunday.name)
        assertTrue(sunday.exercises.isEmpty())
    }

    @Test
    fun testExercisePerformanceRankCalculation() {
        val pushup = DefaultRoutines.pushExercises.first { it.id == "push_std" }
        assertEquals(5, pushup.bronzeThreshold)
        assertEquals(10, pushup.silverThreshold)
        assertEquals(20, pushup.goldThreshold)

        assertEquals(PerformanceRank.UNRANKED, pushup.calculateRank(3))
        assertEquals(PerformanceRank.BRONZE, pushup.calculateRank(5))
        assertEquals(PerformanceRank.BRONZE, pushup.calculateRank(9))
        assertEquals(PerformanceRank.SILVER, pushup.calculateRank(10))
        assertEquals(PerformanceRank.SILVER, pushup.calculateRank(19))
        assertEquals(PerformanceRank.GOLD, pushup.calculateRank(20))
        assertEquals(PerformanceRank.GOLD, pushup.calculateRank(25))
    }

    @Test
    fun testChinUpPerformanceRankCalculation() {
        val chinup = DefaultRoutines.pullExercises.first { it.id == "pull_strict_chin" }
        assertEquals(1, chinup.bronzeThreshold)
        assertEquals(3, chinup.silverThreshold)
        assertEquals(5, chinup.goldThreshold)

        assertEquals(PerformanceRank.UNRANKED, chinup.calculateRank(0))
        assertEquals(PerformanceRank.BRONZE, chinup.calculateRank(1))
        assertEquals(PerformanceRank.SILVER, chinup.calculateRank(3))
        assertEquals(PerformanceRank.GOLD, chinup.calculateRank(5))
    }

    @Test
    fun testSoundManagerSettingsPersistence() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = SoundManager(context)

        val initial = soundManager.settings.value
        assertTrue(initial.masterEnabled)

        val updated = initial.copy(masterEnabled = false, volume = 0.5f)
        soundManager.updateSettings(updated)

        assertEquals(false, soundManager.settings.value.masterEnabled)
        assertEquals(0.5f, soundManager.settings.value.volume, 0.01f)

        // Verify across reload
        val reloadedManager = SoundManager(context)
        assertEquals(false, reloadedManager.settings.value.masterEnabled)
        assertEquals(0.5f, reloadedManager.settings.value.volume, 0.01f)
    }
}
