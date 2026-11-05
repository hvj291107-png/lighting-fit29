package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.model.DefaultRoutines
import com.example.model.PerformanceRank
import com.example.model.WorkoutRoutine
import com.example.ui.AchievementNotificationBanner
import com.example.ui.AchievementsScreen
import com.example.ui.ActiveWorkoutScreen
import com.example.ui.HomeScreen
import com.example.ui.ModernBottomNav
import com.example.ui.MyRoutinesScreen
import com.example.ui.ProgressScreen
import com.example.ui.RetestRankDialog
import com.example.ui.RoutineEditorScreen
import com.example.ui.SoundSettingsDialog
import com.example.ui.Stage02ComingSoonDialog
import com.example.ui.StartupSplashScreen
import com.example.ui.TrainingScreen
import com.example.ui.WorkoutFinishScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.LightningFitsTheme
import com.example.viewmodel.AppNavTab
import com.example.viewmodel.AppScreen
import com.example.viewmodel.LightningViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LightningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LightningFitsTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: LightningViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val athleteProfile by viewModel.athleteProfile.collectAsStateWithLifecycle()
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val performances by viewModel.performances.collectAsStateWithLifecycle()
    val workoutLogs by viewModel.workoutLogs.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val soundSettings by viewModel.soundSettings.collectAsStateWithLifecycle()
    val showSoundSettings by viewModel.showSoundSettings.collectAsStateWithLifecycle()
    val retestExercise by viewModel.retestExercise.collectAsStateWithLifecycle()
    val activeWorkoutState by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val finishedSummary by viewModel.finishedSummary.collectAsStateWithLifecycle()
    val activeAchievement by viewModel.activeAchievementNotification.collectAsStateWithLifecycle()

    val currentDay = viewModel.currentDayOfWeek
    val todayRoutine: WorkoutRoutine = routines.firstOrNull { it.assignedDay == currentDay }
        ?: DefaultRoutines.getRoutineForDay(currentDay)

    val requiredExercises = DefaultRoutines.stage01RequiredExercises
    val goldCount = requiredExercises.count { ex ->
        performances[ex.id]?.currentRank == PerformanceRank.GOLD.name
    }
    val isStage01Mastered = goldCount >= 6

    val pagerState = rememberPagerState(initialPage = currentTab.ordinal, pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()

    // Sync swiping with selected tab
    LaunchedEffect(pagerState.currentPage) {
        val tab = AppNavTab.entries.getOrNull(pagerState.currentPage) ?: AppNavTab.HOME
        if (currentTab != tab) {
            viewModel.selectTab(tab)
        }
    }

    // Sync tab changes (e.g. from buttons) with pager
    LaunchedEffect(currentTab) {
        if (pagerState.currentPage != currentTab.ordinal) {
            pagerState.animateScrollToPage(currentTab.ordinal)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        Crossfade(targetState = currentScreen, label = "screenCrossfade") { screen ->
            when (screen) {
                is AppScreen.Splash -> {
                    StartupSplashScreen(
                        onFinished = { viewModel.finishSplash() }
                    )
                }

                is AppScreen.ActiveWorkout -> {
                    activeWorkoutState?.let { state ->
                        ActiveWorkoutScreen(
                            viewModel = viewModel,
                            state = state
                        )
                    }
                }

                is AppScreen.WorkoutSummary -> {
                    finishedSummary?.let { summary ->
                        WorkoutFinishScreen(
                            summary = summary,
                            onContinue = { viewModel.dismissSummary() }
                        )
                    }
                }

                is AppScreen.EditRoutine -> {
                    RoutineEditorScreen(
                        routine = screen.routine,
                        onSave = { updated -> viewModel.saveRoutine(updated) },
                        onCancel = { viewModel.closeEditRoutine() }
                    )
                }

                is AppScreen.Stage02ComingSoon -> {
                    Stage02ComingSoonDialog(
                        onDismiss = { viewModel.closeStage02Modal() }
                    )
                }

                is AppScreen.Main -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = AmoledBlack,
                        bottomBar = {
                            ModernBottomNav(
                                currentTab = currentTab,
                                onTabSelected = { tab ->
                                    viewModel.selectTab(tab)
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(tab.ordinal)
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                when (page) {
                                    0 -> {
                                        HomeScreen(
                                            athleteProfile = athleteProfile,
                                            todayRoutine = todayRoutine,
                                            allRoutines = routines,
                                            currentDayOfWeek = currentDay,
                                            performances = performances,
                                            workoutLogs = workoutLogs,
                                            achievements = achievements,
                                            stage01MasteredCount = goldCount,
                                            isStage01Mastered = isStage01Mastered,
                                            onStartWorkout = { r -> viewModel.startWorkout(r) },
                                            onRetestExercise = { ex -> viewModel.openRetest(ex) },
                                            onOpenSettings = { viewModel.openSoundSettings() },
                                            onOpenStage02Modal = { viewModel.openStage02ComingSoon() },
                                            onNavigateToProgress = {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(3)
                                                }
                                            }
                                        )
                                    }

                                    1 -> {
                                        TrainingScreen(
                                            routines = routines,
                                            performances = performances,
                                            onStartWorkout = { r -> viewModel.startWorkout(r) },
                                            onRetestExercise = { ex -> viewModel.openRetest(ex) }
                                        )
                                    }

                                    2 -> {
                                        MyRoutinesScreen(
                                            routines = routines,
                                            onStartWorkout = { r -> viewModel.startWorkout(r) },
                                            onCreateRoutine = { viewModel.openCreateRoutine() },
                                            onEditRoutine = { r -> viewModel.openEditRoutine(r) },
                                            onDuplicateRoutine = { r -> viewModel.duplicateRoutine(r) },
                                            onDeleteRoutine = { r -> viewModel.deleteRoutine(r) }
                                        )
                                    }

                                    3 -> {
                                        ProgressScreen(
                                            performances = performances,
                                            workoutLogs = workoutLogs,
                                            onRetestExercise = { ex -> viewModel.openRetest(ex) },
                                            onProceedToStage02 = { viewModel.openStage02ComingSoon() }
                                        )
                                    }

                                    4 -> {
                                        AchievementsScreen(
                                            achievements = achievements
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Onscreen Achievement Notification HUD
        AnimatedVisibility(
            visible = activeAchievement != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            activeAchievement?.let { achievement ->
                AchievementNotificationBanner(
                    achievement = achievement,
                    onDismiss = { viewModel.dismissAchievementNotification() }
                )
            }
        }

        // Global Dialogs
        if (showSoundSettings) {
            SoundSettingsDialog(
                settings = soundSettings,
                soundManager = viewModel.soundManager,
                onUpdateSettings = { updated -> viewModel.updateSoundSettings(updated) },
                onDismiss = { viewModel.closeSoundSettings() }
            )
        }

        retestExercise?.let { ex ->
            val best = performances[ex.id]?.bestValue ?: 0
            RetestRankDialog(
                exercise = ex,
                currentBest = best,
                onSubmitScore = { score -> viewModel.submitRetestScore(ex, score) },
                onDismiss = { viewModel.closeRetest() }
            )
        }
    }
}
