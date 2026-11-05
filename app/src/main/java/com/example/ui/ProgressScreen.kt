package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExercisePerformanceEntity
import com.example.model.DefaultRoutines
import com.example.model.Exercise
import com.example.model.ExerciseCategory
import com.example.model.PerformanceRank
import com.example.model.WorkoutLog
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.CardCharcoalElevated
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    performances: Map<String, ExercisePerformanceEntity>,
    workoutLogs: List<WorkoutLog>,
    onRetestExercise: (Exercise) -> Unit,
    onProceedToStage02: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 6 required categories
    val categories = listOf(
        ExerciseCategory.PUSH,
        ExerciseCategory.PULL,
        ExerciseCategory.LEGS,
        ExerciseCategory.CORE_STABILITY,
        ExerciseCategory.CORE_STRENGTH,
        ExerciseCategory.ATHLETE
    )

    // Check Stage 01 mastery (all 6 categories mastered with ALL exercises at Gold)
    val goldCount = categories.count { category ->
        val exs = when (category) {
            ExerciseCategory.PUSH -> DefaultRoutines.pushExercises
            ExerciseCategory.PULL -> DefaultRoutines.pullExercises
            ExerciseCategory.LEGS -> DefaultRoutines.legsExercises
            ExerciseCategory.CORE_STABILITY -> DefaultRoutines.coreStabilityExercises
            ExerciseCategory.CORE_STRENGTH -> DefaultRoutines.coreStrengthExercises
            ExerciseCategory.ATHLETE -> DefaultRoutines.athleteExercises
        }
        exs.all { ex -> performances[ex.id]?.currentRank == PerformanceRank.GOLD.name }
    }
    val isStage01Mastered = goldCount >= 6

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "STAGE 01 PROGRESSION",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Earn Gold rank across all 6 calisthenics disciplines to unlock Stage 02.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Stage 01 Mastered Banner or Progress Status
        item {
            Stage01MasteryBanner(
                goldCount = goldCount,
                isMastered = isStage01Mastered,
                onProceedToStage02 = onProceedToStage02
            )
        }

        // All 6 Categories are displayed — NEVER hidden even if no records yet!
        items(categories) { category ->
            val exercisesForCategory = when (category) {
                ExerciseCategory.PUSH -> DefaultRoutines.pushExercises
                ExerciseCategory.PULL -> DefaultRoutines.pullExercises
                ExerciseCategory.LEGS -> DefaultRoutines.legsExercises
                ExerciseCategory.CORE_STABILITY -> DefaultRoutines.coreStabilityExercises
                ExerciseCategory.CORE_STRENGTH -> DefaultRoutines.coreStrengthExercises
                ExerciseCategory.ATHLETE -> DefaultRoutines.athleteExercises
            }

            val logsForCategory = workoutLogs.filter { it.category == category }

            CategoryProgressCard(
                category = category,
                exercises = exercisesForCategory,
                logs = logsForCategory,
                performances = performances,
                onRetest = onRetestExercise
            )
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun Stage01MasteryBanner(
    goldCount: Int,
    isMastered: Boolean,
    onProceedToStage02: () -> Unit
) {
    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isMastered) GoldColor else ElectricYellow.copy(alpha = 0.5f),
        backgroundColor = if (isMastered) DarkCharcoal else CardCharcoal
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = if (isMastered) GoldColor else ElectricYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isMastered) "STAGE 01 MASTERED" else "STAGE 01 STATUS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isMastered) GoldColor else TextPrimary
                    )
                }

                Text(
                    text = "$goldCount / 6 GOLD",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isMastered) GoldColor else ElectricYellow
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { (goldCount.toFloat() / 6f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isMastered) GoldColor else ElectricYellow,
                trackColor = CardCharcoalElevated,
            )

            if (isMastered) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "You are eligible to proceed to Stage 02.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                ElectricButton(
                    text = "PROCEED TO STAGE 02",
                    onClick = onProceedToStage02,
                    containerColor = GoldColor,
                    contentColor = AmoledBlack,
                    leadingIcon = Icons.Default.EmojiEvents
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Requires Gold in every required exercise across all six categories before Stage 01 is considered mastered.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun CategoryProgressCard(
    category: ExerciseCategory,
    exercises: List<Exercise>,
    logs: List<WorkoutLog>,
    performances: Map<String, ExercisePerformanceEntity>,
    onRetest: (Exercise) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Calculate overall Stage Rank for this category:
    // ALL exercises inside must meet or exceed the rank tier!
    val categoryRanks = exercises.map { ex ->
        val perf = performances[ex.id]
        if (perf != null) {
            try {
                PerformanceRank.valueOf(perf.currentRank)
            } catch (_: Exception) {
                PerformanceRank.UNRANKED
            }
        } else PerformanceRank.UNRANKED
    }

    val overallCategoryRank = when {
        categoryRanks.all { it == PerformanceRank.GOLD } -> PerformanceRank.GOLD
        categoryRanks.all { it.levelNumber >= PerformanceRank.SILVER.levelNumber } -> PerformanceRank.SILVER
        categoryRanks.all { it.levelNumber >= PerformanceRank.BRONZE.levelNumber } -> PerformanceRank.BRONZE
        else -> PerformanceRank.UNRANKED
    }

    val nextTargetRank = when (overallCategoryRank) {
        PerformanceRank.UNRANKED -> PerformanceRank.BRONZE
        PerformanceRank.BRONZE -> PerformanceRank.SILVER
        PerformanceRank.SILVER -> PerformanceRank.GOLD
        PerformanceRank.GOLD -> null
    }

    val qualifiedCount = if (nextTargetRank != null) {
        categoryRanks.count { it.levelNumber >= nextTargetRank.levelNumber }
    } else exercises.size

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (overallCategoryRank == PerformanceRank.GOLD) GoldColor.copy(alpha = 0.5f) else BorderSubtle,
        backgroundColor = CardCharcoal,
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.displayName.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardCharcoalElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STAGE 01",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    val statusSubtitle = when {
                        overallCategoryRank == PerformanceRank.GOLD ->
                            "All ${exercises.size} exercises reached Gold • Mastered"
                        nextTargetRank != null ->
                            "Next ${nextTargetRank.displayName}: $qualifiedCount/${exercises.size} qualified (all required)"
                        else ->
                            "Complete all exercises to rank this stage"
                    }

                    Text(
                        text = statusSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (overallCategoryRank == PerformanceRank.GOLD) NeonGreen else TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RankBadgeChip(rank = overallCategoryRank)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded details: exercises breakdown, charts & history
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Exercise performance breakdown
                    Text(
                        text = "EXERCISE PERFORMANCE & TARGETS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        letterSpacing = 0.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    exercises.forEach { ex ->
                        val p = performances[ex.id]
                        val score = p?.bestValue ?: 0
                        val rank = if (p != null) {
                            try {
                                PerformanceRank.valueOf(p.currentRank)
                            } catch (_: Exception) {
                                PerformanceRank.UNRANKED
                            }
                        } else PerformanceRank.UNRANKED
                        val progressFraction = ex.progressFractionToNext(score)
                        val nextTarget = ex.nextRankTarget(score)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardCharcoalElevated)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ex.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = if (score > 0) "PR: $score ${ex.unit} • Next Target: $nextTarget ${ex.unit}" else "Target: $nextTarget ${ex.unit}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RankBadgeChip(rank = rank)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onRetest(ex) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Speed,
                                                contentDescription = "Test",
                                                tint = ElectricYellow,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (rank == PerformanceRank.GOLD) GoldColor else ElectricYellow,
                                    trackColor = DarkCharcoal,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Chart / Trend
                    Text(
                        text = "WORKOUT HISTORY & TREND",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        letterSpacing = 0.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (logs.isEmpty()) {
                        // Specified exact phrase from Section 10:
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkCharcoal)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "No records yet — complete your first workout to begin tracking.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        // Visual bar chart of past sessions
                        SimpleBarChart(logs = logs.take(6))

                        Spacer(modifier = Modifier.height(10.dp))

                        logs.take(3).forEach { log ->
                            val dateStr = try {
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(log.timestamp))
                            } catch (_: Exception) {
                                "Recent"
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "${log.setsCompleted} sets • ${log.durationSeconds / 60}m • +${log.xpEarned} XP",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleBarChart(logs: List<WorkoutLog>) {
    val maxSets = logs.maxOfOrNull { it.setsCompleted }?.coerceAtLeast(1) ?: 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCharcoal)
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = 24.dp.toPx()
            val spacing = (size.width - (logs.size * barWidth)) / (logs.size + 1).coerceAtLeast(1)

            logs.forEachIndexed { i, log ->
                val barHeight = (log.setsCompleted.toFloat() / maxSets.toFloat()) * (size.height - 20.dp.toPx())
                val x = spacing + i * (barWidth + spacing)
                val y = size.height - barHeight

                drawRoundRect(
                    color = ElectricYellow,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }
    }
}
