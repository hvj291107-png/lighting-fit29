package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Exercise
import com.example.model.ExerciseType
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.CardCharcoalElevated
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.ElectricYellowGlow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.ActiveWorkoutState
import com.example.viewmodel.LightningViewModel

@Composable
fun ActiveWorkoutScreen(
    viewModel: LightningViewModel,
    state: ActiveWorkoutState,
    modifier: Modifier = Modifier
) {
    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept hardware / gesture back button
    BackHandler {
        showExitDialog = true
    }

    val currentEx = state.currentExercise ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar: Quit button, Workout Timer, Exercise Counter
            WorkoutTopBar(
                routineName = state.routine.name,
                exerciseIndex = state.currentExerciseIndex + 1,
                totalExercises = state.routine.exercises.size,
                elapsedSeconds = state.elapsedSeconds,
                onQuitClick = { showExitDialog = true }
            )

            // Overall Workout Progress Bar
            LinearProgressIndicator(
                progress = { state.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ElectricYellow,
                trackColor = CardCharcoalElevated,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Active Content Scrollable View
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Exercise Header
                ExerciseHeader(exercise = currentEx)

                Spacer(modifier = Modifier.height(14.dp))

                // Set indicators (pills/dots)
                SetIndicatorsRow(
                    targetSets = currentEx.targetSets,
                    currentSetIndex = state.currentSetIndex,
                    completedCount = state.completedSetsMap[currentEx.id]?.size ?: 0
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Distinction between Rep-based and Timer-based interfaces
                if (currentEx.type == ExerciseType.REPS) {
                    RepBasedInterface(
                        exercise = currentEx,
                        currentSetIndex = state.currentSetIndex,
                        currentRepsInput = state.currentRepsInput,
                        onIncrement = { viewModel.incrementReps() },
                        onDecrement = { viewModel.decrementReps() },
                        onCompleteSet = { viewModel.completeRepSet() }
                    )
                } else {
                    TimerBasedInterface(
                        exercise = currentEx,
                        currentSetIndex = state.currentSetIndex,
                        secondsRemaining = state.timerSecondsRemaining,
                        totalDuration = state.timerTotalDuration,
                        isRunning = state.isExerciseTimerRunning,
                        isCompleted = state.isTimerCompleted,
                        onStart = { viewModel.startExerciseTimer() },
                        onPause = { viewModel.pauseExerciseTimer() },
                        onResume = { viewModel.resumeExerciseTimer() },
                        onCompleteSet = { viewModel.completeTimedSet() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Technique coaching cues card
                TechniqueCuesCard(cues = currentEx.cues)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Animated Rest Timer Overlay (shown when isRestActive)
        if (state.isRestActive) {
            RestTimerOverlay(
                secondsRemaining = state.restSecondsRemaining,
                totalDuration = state.restTotalDuration,
                onSkip = { viewModel.skipRest() },
                onAdd30 = { viewModel.add30SecondsRest() }
            )
        }

        // Exit confirmation dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                containerColor = DarkCharcoal,
                title = {
                    Text(
                        text = "Leave Workout?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "You have completed ${state.totalCompletedSetsCount} sets so far. Would you like to finish and save your progress or discard?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            viewModel.exitActiveWorkout(savePartial = true)
                        }
                    ) {
                        Text(
                            text = "Save & Finish",
                            color = ElectricYellow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            viewModel.exitActiveWorkout(savePartial = false)
                        }
                    ) {
                        Text(
                            text = "Discard",
                            color = Color(0xFFFF5252)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun WorkoutTopBar(
    routineName: String,
    exerciseIndex: Int,
    totalExercises: Int,
    elapsedSeconds: Int,
    onQuitClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onQuitClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardCharcoal)
                .border(1.dp, BorderSubtle, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Exit Workout",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = routineName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ElectricYellow,
                fontSize = 11.sp,
                letterSpacing = 0.sp
            )
            Text(
                text = "EXERCISE $exerciseIndex OF $totalExercises",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Workout Elapsed Timer
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(CardCharcoal)
                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            val mins = elapsedSeconds / 60
            val secs = elapsedSeconds % 60
            Text(
                text = String.format("%02d:%02d", mins, secs),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ExerciseHeader(exercise: Exercise) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(ElectricYellow.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = exercise.category.displayName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ElectricYellow,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = exercise.name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun SetIndicatorsRow(
    targetSets: Int,
    currentSetIndex: Int,
    completedCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..targetSets) {
            val isCompleted = i <= completedCount
            val isCurrent = i == currentSetIndex && !isCompleted

            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isCompleted -> NeonGreen
                            isCurrent -> ElectricYellow.copy(alpha = 0.2f)
                            else -> CardCharcoalElevated
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = when {
                            isCompleted -> NeonGreen
                            isCurrent -> ElectricYellow
                            else -> BorderSubtle
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = AmoledBlack,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "$i",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) ElectricYellow else TextSecondary
                    )
                }
            }
        }
    }
}

// ---------------- REP-BASED INTERFACE ----------------
@Composable
private fun RepBasedInterface(
    exercise: Exercise,
    currentSetIndex: Int,
    currentRepsInput: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onCompleteSet: () -> Unit
) {
    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = ElectricYellow.copy(alpha = 0.4f),
        backgroundColor = CardCharcoal
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SET $currentSetIndex OF ${exercise.targetSets}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ElectricYellow,
                letterSpacing = 0.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Target: ${exercise.targetReps} reps",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Large Rep Stepper with Plus / Minus
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Minus button
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(CardCharcoalElevated)
                        .border(1.dp, BorderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Reps",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(28.dp))

                // Rep counter digit
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentRepsInput",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 58.sp,
                        letterSpacing = 0.sp
                    )
                    Text(
                        text = "REPS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(28.dp))

                // Plus button
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ElectricYellow.copy(alpha = 0.2f))
                        .border(1.dp, ElectricYellow, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Reps",
                        tint = ElectricYellow,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Large COMPLETE SET button
            ElectricButton(
                text = "COMPLETE SET $currentSetIndex",
                onClick = onCompleteSet,
                leadingIcon = Icons.Default.Check,
                containerColor = ElectricYellow,
                contentColor = AmoledBlack,
                testTag = "complete_set_button"
            )
        }
    }
}

// ---------------- TIMER-BASED INTERFACE ----------------
@Composable
private fun TimerBasedInterface(
    exercise: Exercise,
    currentSetIndex: Int,
    secondsRemaining: Int,
    totalDuration: Int,
    isRunning: Boolean,
    isCompleted: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCompleteSet: () -> Unit
) {
    val progress = if (totalDuration > 0) {
        ((totalDuration - secondsRemaining).toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val mins = secondsRemaining / 60
    val secs = secondsRemaining % 60
    val timeDisplay = String.format("%02d:%02d", mins, secs)

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isCompleted) NeonGreen.copy(alpha = 0.5f) else BorderSubtle,
        backgroundColor = CardCharcoal
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SET $currentSetIndex OF ${exercise.targetSets} • DURATION",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) NeonGreen else ElectricYellow,
                letterSpacing = 0.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Circular Timer
            CircularProgressTimer(
                fraction = progress,
                timeText = timeDisplay,
                statusText = when {
                    isCompleted -> "TIME COMPLETED"
                    isRunning -> "HOLD POSTURE"
                    else -> "READY"
                },
                isCompleted = isCompleted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Timer controls (START / PAUSE / RESUME)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isRunning && !isCompleted && secondsRemaining == totalDuration) {
                    ElectricButton(
                        text = "START TIMER",
                        onClick = onStart,
                        leadingIcon = Icons.Default.PlayArrow,
                        modifier = Modifier.weight(1f),
                        testTag = "start_timer_button"
                    )
                } else if (isRunning) {
                    SecondaryButton(
                        text = "PAUSE",
                        onClick = onPause,
                        leadingIcon = Icons.Default.Pause,
                        modifier = Modifier.weight(1f),
                        testTag = "pause_timer_button"
                    )
                } else if (!isCompleted) {
                    ElectricButton(
                        text = "RESUME",
                        onClick = onResume,
                        leadingIcon = Icons.Default.PlayArrow,
                        modifier = Modifier.weight(1f),
                        testTag = "resume_timer_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Finish/Complete Set button
            ElectricButton(
                text = if (isCompleted) "CONFIRM COMPLETED SET" else "COMPLETE SET EARLY",
                onClick = onCompleteSet,
                leadingIcon = Icons.Default.Check,
                containerColor = if (isCompleted) NeonGreen else CardCharcoalElevated,
                contentColor = if (isCompleted) AmoledBlack else TextPrimary,
                testTag = "complete_timed_set_button"
            )
        }
    }
}

@Composable
private fun TechniqueCuesCard(cues: List<String>) {
    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = BorderSubtle,
        backgroundColor = CardCharcoalElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "EXECUTION KEYS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ElectricYellow,
                letterSpacing = 0.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            cues.forEach { cue ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        color = ElectricYellow,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cue,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ---------------- REST TIMER OVERLAY ----------------
@Composable
private fun RestTimerOverlay(
    secondsRemaining: Int,
    totalDuration: Int,
    onSkip: () -> Unit,
    onAdd30: () -> Unit
) {
    val progress = if (totalDuration > 0) {
        (secondsRemaining.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack.copy(alpha = 0.94f))
            .clickable(enabled = false) {}, // Intercept clicks
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonGreen.copy(alpha = 0.2f))
                    .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "SET FINISHED! REST & RECOVER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen,
                    letterSpacing = 0.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val mins = secondsRemaining / 60
            val secs = secondsRemaining % 60
            val timeText = String.format("%02d:%02d", mins, secs)

            CircularProgressTimer(
                fraction = progress,
                timeText = timeText,
                statusText = "REST PERIOD",
                isCompleted = false,
                size = 220.dp
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SecondaryButton(
                    text = "+30 SECONDS",
                    onClick = onAdd30,
                    modifier = Modifier.weight(1f)
                )

                ElectricButton(
                    text = "SKIP REST",
                    onClick = onSkip,
                    leadingIcon = Icons.Default.FastForward,
                    modifier = Modifier.weight(1f),
                    containerColor = ElectricYellow,
                    contentColor = AmoledBlack
                )
            }
        }
    }
}
