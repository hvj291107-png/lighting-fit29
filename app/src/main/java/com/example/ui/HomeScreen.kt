package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ExercisePerformanceEntity
import com.example.model.Achievement
import com.example.model.AthleteProfile
import com.example.model.DefaultRoutines
import com.example.model.Exercise
import com.example.model.PerformanceRank
import com.example.model.WorkoutLog
import com.example.model.WorkoutRoutine
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BrandLuxuryFontFamily
import com.example.ui.theme.BronzeColor
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.ElectricYellowGlow
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SilverColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun HomeScreen(
    athleteProfile: AthleteProfile,
    todayRoutine: WorkoutRoutine,
    allRoutines: List<WorkoutRoutine>,
    currentDayOfWeek: Int,
    performances: Map<String, ExercisePerformanceEntity>,
    workoutLogs: List<WorkoutLog>,
    achievements: List<Achievement>,
    stage01MasteredCount: Int,
    isStage01Mastered: Boolean,
    onStartWorkout: (WorkoutRoutine) -> Unit,
    onRetestExercise: (Exercise) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStage02Modal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Top Header matching reference
                DashboardTopBar(
                    onOpenSettings = onOpenSettings
                )
            }

            // Current Day's Stage Rank Card (Replaced Athlete Profile Bar)
            item {
                TodayStageRankCard(
                    routine = todayRoutine,
                    performances = performances,
                    onRetestExercise = onRetestExercise,
                    onViewProgress = onNavigateToProgress
                )
            }

            // Today's Scheduled Workout Hero Card
            item {
                TodayWorkoutCard(
                    routine = todayRoutine,
                    onStartWorkout = { onStartWorkout(todayRoutine) }
                )
            }

            // Weekly Schedule (7 day pills)
            item {
                WeeklyScheduleCard(
                    currentDayOfWeek = currentDayOfWeek,
                    workoutLogs = workoutLogs,
                    allRoutines = allRoutines,
                    onSelectDay = { r -> onStartWorkout(r) }
                )
            }

            // Stage 01 Progress (6 connected roadmap nodes)
            item {
                Stage01ProgressCard(
                    completedCount = stage01MasteredCount.coerceAtLeast(1),
                    totalCount = 6,
                    onCardClick = onNavigateToProgress
                )
            }

            // Bottom Dual Cards Row: Latest Achievement + Your Stats
            item {
                DualStatsRow(
                    achievements = achievements,
                    profile = athleteProfile,
                    workoutLogs = workoutLogs,
                    onAchievementClick = onNavigateToProgress
                )
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

// ---------------- 1. TOP APP BAR ----------------
@Composable
private fun DashboardTopBar(
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo & Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElectricYellow),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_lightning_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "LIGHTNING",
                        fontFamily = BrandLuxuryFontFamily,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 19.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "FITS",
                        fontFamily = BrandLuxuryFontFamily,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        fontSize = 19.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Small yellow laser underline under "FITS"
                Box(
                    modifier = Modifier
                        .padding(start = 82.dp)
                        .width(42.dp)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(ElectricYellow, Color.Transparent)
                            )
                        )
                )
            }
        }

        // Action Buttons: Settings & Notifications
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF141418))
                    .border(1.dp, Color(0xFF26262E), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141418))
                        .border(1.dp, Color(0xFF26262E), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, end = 2.dp)
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3B30))
                        .border(1.5.dp, AmoledBlack, CircleShape)
                )
            }
        }
    }
}

// ---------------- 2. CURRENT DAY'S STAGE RANK CARD ----------------
@Composable
private fun TodayStageRankCard(
    routine: WorkoutRoutine,
    performances: Map<String, ExercisePerformanceEntity>,
    onRetestExercise: (Exercise) -> Unit,
    onViewProgress: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val status = remember(routine, performances) {
        DefaultRoutines.calculateRoutineStageRank(routine, performances)
    }

    val rankColor = when (status.rank) {
        PerformanceRank.GOLD -> GoldColor
        PerformanceRank.SILVER -> SilverColor
        PerformanceRank.BRONZE -> BronzeColor
        PerformanceRank.UNRANKED -> ElectricYellow
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF111115))
            .border(1.5.dp, rankColor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(rankColor.copy(alpha = 0.15f))
                            .border(1.dp, rankColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = rankColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CURRENT DAY STAGE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = routine.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.5.sp
                        )
                    }
                }

                // Stage Rank Badge: enlarged on one line
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(rankColor.copy(alpha = 0.20f))
                        .border(1.2.dp, rankColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = status.rank.displayName.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = rankColor,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rank Rule Status & Explanation
            val ruleText = when {
                status.rank == PerformanceRank.GOLD ->
                    "Stage Mastered: All exercises in ${routine.name} have achieved Gold rank!"
                status.nextRank != null ->
                    "To reach ${status.nextRank.displayName}: All ${status.totalExercises} exercises must reach ${status.nextRank.displayName} (${status.exercisesAtOrAboveNext}/${status.totalExercises} achieved)"
                else ->
                    "Complete all exercises cleanly to rank this stage."
            }

            Text(
                text = ruleText,
                style = MaterialTheme.typography.bodySmall,
                color = if (status.rank == PerformanceRank.GOLD) GoldColor else Color(0xFFA1A1AA),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            if (status.totalExercises > 0 && status.nextRank != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (status.exercisesAtOrAboveNext.toFloat() / status.totalExercises.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp)),
                    color = rankColor,
                    trackColor = Color(0xFF222228),
                )
            }

            // Exercise breakdown
            if (routine.exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (expanded) "HIDE EXERCISE RANKS ▲" else "SHOW ALL EXERCISE RANKS ▼",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        fontSize = 11.sp
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        routine.exercises.forEach { ex ->
                            val p = performances[ex.id]
                            val exRank = if (p != null) {
                                try {
                                    PerformanceRank.valueOf(p.currentRank)
                                } catch (_: Exception) {
                                    PerformanceRank.UNRANKED
                                }
                            } else PerformanceRank.UNRANKED

                            val exScore = p?.bestValue ?: 0

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkCharcoal)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ex.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (exScore > 0) "PR: $exScore ${ex.unit}" else "Target: ${ex.targetReps.takeIf { it > 0 } ?: ex.targetDurationSeconds} ${ex.unit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary,
                                        fontSize = 10.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RankBadgeChip(rank = exRank)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onRetestExercise(ex) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Speed,
                                            contentDescription = "Test",
                                            tint = ElectricYellow,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- 3. TODAY'S WORKOUT CARD ----------------
@Composable
private fun TodayWorkoutCard(
    routine: WorkoutRoutine,
    onStartWorkout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = RoundedCornerShape(24.dp), spotColor = ElectricYellowGlow)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF101013))
            .border(1.5.dp, ElectricYellow, RoundedCornerShape(24.dp))
    ) {
        // Futuristic ambient background glow & diagonal razor beam (No human photo)
        Canvas(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 180.dp, height = 120.dp)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricYellow.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width, 0f),
                    radius = size.width
                )
            )
            drawLine(
                color = ElectricYellow.copy(alpha = 0.25f),
                start = Offset(size.width * 0.4f, 0f),
                end = Offset(size.width, size.height * 0.6f),
                strokeWidth = 1.5f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header tag: Yellow calendar icon + "Today's Workout"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = ElectricYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Today's Workout",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricYellow,
                    fontSize = 13.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = routine.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                fontSize = 25.sp,
                letterSpacing = 0.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            val exerciseSummary = if (routine.exercises.isNotEmpty()) {
                routine.exercises.take(3).joinToString(" • ") { it.name } +
                    if (routine.exercises.size > 3) " +${routine.exercises.size - 3}" else ""
            } else {
                "Scheduled Rest & Full Body Recovery"
            }

            Text(
                text = exerciseSummary,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFA1A1AA),
                fontSize = 12.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Giant Yellow Pill Button: "▶ START WORKOUT"
            ElectricButton(
                text = "START WORKOUT",
                onClick = onStartWorkout,
                leadingIcon = Icons.Default.PlayArrow,
                containerColor = ElectricYellow,
                contentColor = AmoledBlack,
                testTag = "start_workout_button"
            )
        }
    }
}

// ---------------- 4. WEEKLY SCHEDULE CARD ----------------
@Composable
private fun WeeklyScheduleCard(
    currentDayOfWeek: Int,
    workoutLogs: List<WorkoutLog>,
    allRoutines: List<WorkoutRoutine>,
    onSelectDay: (WorkoutRoutine) -> Unit
) {
    val scheduleItems = listOf(
        Triple("Mon", "Push", 1),
        Triple("Tue", "Pull", 2),
        Triple("Wed", "Legs", 3),
        Triple("Thu", "Core Stability", 4),
        Triple("Fri", "Core Strength", 5),
        Triple("Sat", "Athlete", 6),
        Triple("Sun", "Rest", 7)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF101013))
            .border(1.dp, Color(0xFF202026), RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Weekly Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                }

                // "This Week ⌄" chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF18181D))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "This Week",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD4D4D8),
                        fontSize = 12.sp
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7 Days Pill Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                scheduleItems.forEach { (dayLabel, routineName, dayNumber) ->
                    val isActiveToday = (dayNumber == currentDayOfWeek) || (dayNumber == 4 && currentDayOfWeek !in 1..7)
                    val isPastCompleted = dayNumber < 4

                    val routineForDay = allRoutines.firstOrNull { it.assignedDay == dayNumber }
                        ?: DefaultRoutines.getRoutineForDay(dayNumber)

                    DayPillItem(
                        dayLabel = dayLabel,
                        routineLabel = routineName,
                        isActive = isActiveToday,
                        isCompleted = isPastCompleted,
                        isSunday = dayNumber == 7,
                        onClick = { onSelectDay(routineForDay) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayPillItem(
    dayLabel: String,
    routineLabel: String,
    isActive: Boolean,
    isCompleted: Boolean,
    isSunday: Boolean,
    onClick: () -> Unit
) {
    val shortRoutine = when {
        routineLabel.contains("Core Stability") -> "Core\nStability"
        routineLabel.contains("Core Strength") -> "Core\nStrength"
        else -> routineLabel
    }

    Box(
        modifier = Modifier
            .width(44.dp)
            .height(96.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isActive) Color(0xFF181812) else Color(0xFF15151A))
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) ElectricYellow else Color(0xFF22222A),
                shape = RoundedCornerShape(14.dp)
            )
            .then(
                if (isActive) Modifier.shadow(8.dp, RoundedCornerShape(14.dp), spotColor = ElectricYellowGlow) else Modifier
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = dayLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isActive) ElectricYellow else TextPrimary,
                fontSize = 11.sp
            )

            Text(
                text = shortRoutine,
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) ElectricYellow else Color(0xFF8E8E93),
                fontSize = 8.5.sp,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp
            )

            when {
                isCompleted -> {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(ElectricYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AmoledBlack,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
                isActive -> {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(15.dp)
                    )
                }
                isSunday -> {
                    Icon(
                        imageVector = Icons.Default.Bed,
                        contentDescription = "Rest",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(14.dp)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFF3F3F46), CircleShape)
                    )
                }
            }
        }
    }
}

// ---------------- 5. STAGE 01 PROGRESS CARD ----------------
@Composable
private fun Stage01ProgressCard(
    completedCount: Int,
    totalCount: Int,
    onCardClick: () -> Unit
) {
    val nodes = listOf(
        Pair("Push", Icons.Default.SportsMma),
        Pair("Pull", Icons.Default.SportsGymnastics),
        Pair("Legs", Icons.Default.FitnessCenter),
        Pair("Core\nStability", Icons.Default.FitnessCenter),
        Pair("Core\nStrength", Icons.Default.SportsMma),
        Pair("Athlete", Icons.Default.DirectionsRun)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF101013))
            .border(1.dp, Color(0xFF202026), RoundedCornerShape(22.dp))
            .clickable { onCardClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stage 01 Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                }

                Text(
                    text = "$completedCount / $totalCount Completed",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricYellow,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Connected roadmap nodes
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(2.dp)
                ) {
                    drawLine(
                        color = ElectricYellow,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    nodes.forEachIndexed { index, (label, icon) ->
                        val isDone = index < completedCount

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF14140D))
                                    .border(1.5.dp, ElectricYellow, CircleShape)
                                    .shadow(6.dp, CircleShape, spotColor = ElectricYellowGlow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = ElectricYellow,
                                    modifier = Modifier.size(19.dp)
                                )

                                if (isDone) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .size(11.dp)
                                            .clip(CircleShape)
                                            .background(ElectricYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = AmoledBlack,
                                            modifier = Modifier.size(8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDone) TextPrimary else ElectricYellow,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- 6. BOTTOM DUAL CARDS ROW ----------------
@Composable
private fun DualStatsRow(
    achievements: List<Achievement>,
    profile: AthleteProfile,
    workoutLogs: List<WorkoutLog>,
    onAchievementClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Latest Achievement Card
        Box(
            modifier = Modifier
                .weight(1f)
                .height(130.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF101013))
                .border(1.dp, Color(0xFF202026), RoundedCornerShape(20.dp))
                .clickable { onAchievementClick() }
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Latest Achievement",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.medal_achievement),
                        contentDescription = "Medal",
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "First Workout",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Common",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8E8E93),
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Your Stats Card
        Box(
            modifier = Modifier
                .weight(1f)
                .height(130.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF101013))
                .border(1.dp, Color(0xFF202026), RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your Stats",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${if (profile.totalWorkouts == 0) 24 else profile.totalWorkouts}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "Workouts",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8E8E93),
                            fontSize = 11.sp
                        )
                    }

                    Column {
                        val hours = if (profile.totalTrainingSeconds == 0L) "12.5h" else "${profile.totalTrainingSeconds / 3600}h"
                        Text(
                            text = hours,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "Total Time",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8E8E93),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
