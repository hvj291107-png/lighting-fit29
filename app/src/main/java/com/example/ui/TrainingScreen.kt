package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExercisePerformanceEntity
import com.example.model.DefaultRoutines
import com.example.model.Exercise
import com.example.model.ExerciseType
import com.example.model.PerformanceRank
import com.example.model.WorkoutRoutine
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.CardCharcoalElevated
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun TrainingScreen(
    routines: List<WorkoutRoutine>,
    performances: Map<String, ExercisePerformanceEntity>,
    onStartWorkout: (WorkoutRoutine) -> Unit,
    onRetestExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultRoutines = remember(routines) {
        val list = routines.filter { it.isDefault }
        if (list.isNotEmpty()) list else DefaultRoutines.getAllDefaultRoutines()
    }

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val currentRoutine = defaultRoutines.getOrNull(selectedDayIndex) ?: defaultRoutines.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // Top Header
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
            Text(
                text = "STAGE 01 TRAINING",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Six foundational calisthenics categories engineered for total body mastery.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Horizontal day tabs
        ScrollableTabRow(
            selectedTabIndex = selectedDayIndex,
            containerColor = AmoledBlack,
            contentColor = ElectricYellow,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = BorderSubtle) }
        ) {
            defaultRoutines.forEachIndexed { index, routine ->
                val isSelected = selectedDayIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedDayIndex = index },
                    text = {
                        Text(
                            text = routine.dayName.take(3).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ElectricYellow else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Routine Overview Header Card
                RoutineOverviewCard(
                    routine = currentRoutine,
                    performances = performances,
                    onStartWorkout = { onStartWorkout(currentRoutine) }
                )
            }

            if (currentRoutine.exercises.isEmpty()) {
                item {
                    RestDayMessageCard()
                }
            } else {
                item {
                    Text(
                        text = "WORKOUT EXERCISES (${currentRoutine.exercises.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.sp
                    )
                }

                items(currentRoutine.exercises) { exercise ->
                    val perf = performances[exercise.id]
                    val rank = if (perf != null) {
                        try {
                            PerformanceRank.valueOf(perf.currentRank)
                        } catch (_: Exception) {
                            PerformanceRank.UNRANKED
                        }
                    } else PerformanceRank.UNRANKED

                    ExerciseDetailCard(
                        exercise = exercise,
                        currentRank = rank,
                        bestValue = perf?.bestValue ?: 0,
                        onRetest = { onRetestExercise(exercise) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun RoutineOverviewCard(
    routine: WorkoutRoutine,
    performances: Map<String, ExercisePerformanceEntity>,
    onStartWorkout: () -> Unit
) {
    val rankStatus = remember(routine, performances) {
        DefaultRoutines.calculateRoutineStageRank(routine, performances)
    }

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (routine.exercises.isNotEmpty()) ElectricYellow.copy(alpha = 0.4f) else BorderSubtle,
        backgroundColor = CardCharcoal
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElectricYellow.copy(alpha = 0.15f))
                        .border(1.dp, ElectricYellow.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = routine.dayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        fontSize = 10.sp
                    )
                }

                if (routine.exercises.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "STAGE RANK: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                        RankBadgeChip(rank = rankStatus.rank)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = routine.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = routine.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            if (routine.exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                // Rank progression info
                val rankText = if (rankStatus.nextRank != null) {
                    "Next: ${rankStatus.nextRank.displayName} (${rankStatus.exercisesAtOrAboveNext}/${rankStatus.totalExercises} exercises qualified • all must qualify)"
                } else {
                    "★ Maximum Stage Rank Achieved (All ${rankStatus.totalExercises} exercises Gold)"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCharcoal)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = rankText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (rankStatus.nextRank == null) NeonGreen else ElectricYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                ElectricButton(
                    text = "START WORKOUT NOW",
                    onClick = onStartWorkout,
                    leadingIcon = Icons.Default.PlayArrow
                )
            }
        }
    }
}

@Composable
private fun ExerciseDetailCard(
    exercise: Exercise,
    currentRank: PerformanceRank,
    bestValue: Int,
    onRetest: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = BorderSubtle,
        backgroundColor = CardCharcoalElevated,
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (exercise.type == ExerciseType.REPS) {
                            "${exercise.targetSets} sets × ${exercise.targetReps} reps • ${exercise.restSeconds}s rest"
                        } else {
                            "${exercise.targetSets} sets × ${exercise.targetDurationSeconds}s • ${exercise.restSeconds}s rest"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricYellow
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RankBadgeChip(rank = currentRank)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TECHNIQUE & EXECUTION CUES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    exercise.cues.forEach { cue ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
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
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rank thresholds info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCharcoal)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "BRONZE",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "${exercise.bronzeThreshold} ${exercise.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "SILVER",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "${exercise.silverThreshold} ${exercise.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "GOLD",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "${exercise.goldThreshold} ${exercise.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricYellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SecondaryButton(
                        text = "TEST PERFORMANCE RANK",
                        onClick = onRetest,
                        leadingIcon = Icons.Default.Speed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RestDayMessageCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardCharcoal)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CardCharcoalElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = ElectricYellow,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "COMPLETE REST DAY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "No scheduled workouts for Sunday. Rest, recover, and prepare your nervous system for Monday Push Stage 01.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
