package com.example.model

object DefaultRoutines {

    val pushExercises = listOf(
        Exercise(
            id = "push_std",
            name = "Standard Push-ups",
            category = ExerciseCategory.PUSH,
            type = ExerciseType.REPS,
            targetSets = 3,
            targetReps = 6,
            restSeconds = 60,
            cues = listOf(
                "Keep core and glutes tight in a solid plank",
                "Lower until chest is 1 inch off the ground",
                "Lock out arms fully at the top with hollow body"
            ),
            bronzeThreshold = 5,
            silverThreshold = 10,
            goldThreshold = 20
        ),
        Exercise(
            id = "push_wide",
            name = "Wide Push-ups",
            category = ExerciseCategory.PUSH,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 5,
            restSeconds = 60,
            cues = listOf(
                "Hands placed 1.5x shoulder width apart",
                "Elbows flaring back at 45 degrees, not 90",
                "Engage outer chest through smooth controlled cadence"
            ),
            bronzeThreshold = 5,
            silverThreshold = 10,
            goldThreshold = 18
        ),
        Exercise(
            id = "push_pike",
            name = "Pike Push-ups",
            category = ExerciseCategory.PUSH,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 5,
            restSeconds = 75,
            cues = listOf(
                "Hips high in an inverted V shape",
                "Shift weight forward over wrists and shoulders",
                "Lower head in front of hands into a tripod"
            ),
            bronzeThreshold = 4,
            silverThreshold = 8,
            goldThreshold = 15
        ),
        Exercise(
            id = "push_knee_diamond",
            name = "Knee Diamond Push-ups",
            category = ExerciseCategory.PUSH,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 5,
            restSeconds = 60,
            cues = listOf(
                "Form diamond index and thumbs under chest",
                "Keep torso straight from knees to shoulders",
                "Focus on triceps extension at top of push"
            ),
            bronzeThreshold = 5,
            silverThreshold = 10,
            goldThreshold = 18
        )
    )

    val pullExercises = listOf(
        Exercise(
            id = "pull_strict_chin",
            name = "Strict Chin-ups",
            category = ExerciseCategory.PULL,
            type = ExerciseType.REPS,
            targetSets = 4,
            targetReps = 1,
            restSeconds = 150,
            cues = listOf(
                "Underhand grip (palms facing you)",
                "Dead hang start with zero swing or kip",
                "Pull chin fully over the bar and hold 1s",
                "Rest 2-3 minutes between strict singles"
            ),
            bronzeThreshold = 1,
            silverThreshold = 3,
            goldThreshold = 5
        ),
        Exercise(
            id = "pull_neg_chin",
            name = "Negative Chin-ups",
            category = ExerciseCategory.PULL,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 2,
            restSeconds = 90,
            cues = listOf(
                "Use a safe step to reach the top position",
                "Control descent slowly for 3-5 full seconds",
                "Keep core tight and shoulders engaged throughout"
            ),
            bronzeThreshold = 2,
            silverThreshold = 4,
            goldThreshold = 8
        ),
        Exercise(
            id = "pull_scapular",
            name = "Scapular Pull-ups",
            category = ExerciseCategory.PULL,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 6,
            restSeconds = 60,
            cues = listOf(
                "Hang with arms completely straight",
                "Depress and retract scapulae without bending elbows",
                "Hold peak contraction for 1 second"
            ),
            bronzeThreshold = 5,
            silverThreshold = 10,
            goldThreshold = 15
        ),
        Exercise(
            id = "pull_dead_hang",
            name = "Dead Hangs",
            category = ExerciseCategory.PULL,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 20,
            restSeconds = 60,
            cues = listOf(
                "Full grip on bar with thumbs wrapped",
                "Decompress spine while keeping core steady",
                "Breathe calmly and fight forearm burn"
            ),
            bronzeThreshold = 15,
            silverThreshold = 30,
            goldThreshold = 45
        )
    )

    val legsExercises = listOf(
        Exercise(
            id = "leg_squat",
            name = "Bodyweight Squats",
            category = ExerciseCategory.LEGS,
            type = ExerciseType.REPS,
            targetSets = 3,
            targetReps = 8,
            restSeconds = 60,
            cues = listOf(
                "Feet shoulder-width apart, toes slightly angled out",
                "Break at hips and knees simultaneously",
                "Drop hip crease below parallel, knees tracking toes"
            ),
            bronzeThreshold = 10,
            silverThreshold = 20,
            goldThreshold = 30
        ),
        Exercise(
            id = "leg_lunge",
            name = "Reverse Lunges",
            category = ExerciseCategory.LEGS,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 8,
            restSeconds = 60,
            cues = listOf(
                "Step back smoothly keeping chest upright",
                "Front shin near vertical, back knee kisses floor",
                "Drive through front heel to return"
            ),
            bronzeThreshold = 6,
            silverThreshold = 12,
            goldThreshold = 18
        ),
        Exercise(
            id = "leg_glute_bridge",
            name = "Glute Bridges",
            category = ExerciseCategory.LEGS,
            type = ExerciseType.REPS,
            targetSets = 3,
            targetReps = 12,
            restSeconds = 45,
            cues = listOf(
                "Feet flat, heels close to glutes",
                "Drive through heels and squeeze glutes at the peak",
                "Maintain neutral spine without hyperextending lower back"
            ),
            bronzeThreshold = 10,
            silverThreshold = 18,
            goldThreshold = 25
        ),
        Exercise(
            id = "leg_calf_raise",
            name = "Calf Raises",
            category = ExerciseCategory.LEGS,
            type = ExerciseType.REPS,
            targetSets = 3,
            targetReps = 15,
            restSeconds = 45,
            cues = listOf(
                "Rise high onto balls of feet",
                "Hold peak contraction for 1 full second",
                "Lower with slow controlled 2-second eccentric"
            ),
            bronzeThreshold = 15,
            silverThreshold = 25,
            goldThreshold = 40
        ),
        Exercise(
            id = "leg_wall_sit",
            name = "Wall Sit",
            category = ExerciseCategory.LEGS,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 25,
            restSeconds = 60,
            cues = listOf(
                "Back flat against sturdy wall",
                "Thighs strictly parallel to floor (90° angle)",
                "Hands on thighs forbidden — keep arms crossed"
            ),
            bronzeThreshold = 25,
            silverThreshold = 40,
            goldThreshold = 60
        )
    )

    val coreStabilityExercises = listOf(
        Exercise(
            id = "core_dead_bug_stab",
            name = "Dead Bugs",
            category = ExerciseCategory.CORE_STABILITY,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 10,
            restSeconds = 45,
            cues = listOf(
                "Press lower back firmly into floor with zero gap",
                "Extend opposite arm and leg slowly",
                "Pause 1s at extension without lumbar arching"
            ),
            bronzeThreshold = 8,
            silverThreshold = 14,
            goldThreshold = 20
        ),
        Exercise(
            id = "core_bird_dog",
            name = "Bird Dogs",
            category = ExerciseCategory.CORE_STABILITY,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 10,
            restSeconds = 45,
            cues = listOf(
                "Start on hands and knees under shoulders and hips",
                "Reach opposite arm forward and leg back in straight line",
                "Hold 2 seconds with hips level to floor"
            ),
            bronzeThreshold = 8,
            silverThreshold = 14,
            goldThreshold = 20
        ),
        Exercise(
            id = "core_heel_taps",
            name = "Heel Taps",
            category = ExerciseCategory.CORE_STABILITY,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 16,
            restSeconds = 45,
            cues = listOf(
                "Lie on back, knees bent, shoulders elevated",
                "Reach side to side touching right then left heel",
                "Keep constant contraction on obliques"
            ),
            bronzeThreshold = 12,
            silverThreshold = 20,
            goldThreshold = 30
        ),
        Exercise(
            id = "core_knee_tucks",
            name = "Knee Tucks",
            category = ExerciseCategory.CORE_STABILITY,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 10,
            restSeconds = 45,
            cues = listOf(
                "Seated balance on tailbone with hands behind hips",
                "Extend legs outward then tuck knees tightly to chest",
                "Brace abdominal wall throughout movement"
            ),
            bronzeThreshold = 8,
            silverThreshold = 14,
            goldThreshold = 20
        ),
        Exercise(
            id = "core_side_plank_stab",
            name = "Side Plank",
            category = ExerciseCategory.CORE_STABILITY,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 20,
            restSeconds = 45,
            cues = listOf(
                "Elbow directly under shoulder, feet stacked",
                "Lift hips high creating straight line head to heels",
                "20 seconds each side"
            ),
            bronzeThreshold = 20,
            silverThreshold = 35,
            goldThreshold = 50
        )
    )

    val coreStrengthExercises = listOf(
        Exercise(
            id = "core_rev_crunch",
            name = "Reverse Crunches",
            category = ExerciseCategory.CORE_STRENGTH,
            type = ExerciseType.REPS,
            targetSets = 3,
            targetReps = 10,
            restSeconds = 45,
            cues = listOf(
                "Lie flat with hands at sides for stability",
                "Curl pelvis up toward ribs using lower abdominals",
                "Slow controlled lower without momentum"
            ),
            bronzeThreshold = 8,
            silverThreshold = 14,
            goldThreshold = 22
        ),
        Exercise(
            id = "core_bent_knee_raise",
            name = "Bent-Knee Raises",
            category = ExerciseCategory.CORE_STRENGTH,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 10,
            restSeconds = 45,
            cues = listOf(
                "Lie flat or hang from bar",
                "Raise bent knees to 90 degrees and curl hips upward",
                "Pause 1 second at top contraction"
            ),
            bronzeThreshold = 8,
            silverThreshold = 14,
            goldThreshold = 20
        ),
        Exercise(
            id = "core_bicycle_crunch",
            name = "Bicycle Crunches",
            category = ExerciseCategory.CORE_STRENGTH,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 16,
            restSeconds = 45,
            cues = listOf(
                "Hands behind head, elbows wide",
                "Rotate shoulder toward opposite knee without pulling neck",
                "Pause 1 second per rotation with crisp extension"
            ),
            bronzeThreshold = 12,
            silverThreshold = 20,
            goldThreshold = 30
        ),
        Exercise(
            id = "core_dead_bug_str",
            name = "Dead Bugs",
            category = ExerciseCategory.CORE_STRENGTH,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 8,
            restSeconds = 45,
            cues = listOf(
                "Deep diaphragmatic breathing while hollow body braced",
                "8 slow controlled reps per side",
                "Feel tension in the deep transversus abdominis"
            ),
            bronzeThreshold = 6,
            silverThreshold = 12,
            goldThreshold = 18
        ),
        Exercise(
            id = "core_std_plank",
            name = "Plank",
            category = ExerciseCategory.CORE_STRENGTH,
            type = ExerciseType.TIME,
            targetSets = 3,
            targetDurationSeconds = 30,
            restSeconds = 60,
            cues = listOf(
                "Forearms parallel, elbows under shoulders",
                "Squeeze glutes, quads, and pull belly button to spine",
                "Do not allow hips to sag or pike"
            ),
            bronzeThreshold = 30,
            silverThreshold = 45,
            goldThreshold = 60
        ),
        Exercise(
            id = "core_side_plank_str",
            name = "Side Plank",
            category = ExerciseCategory.CORE_STRENGTH,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 15,
            restSeconds = 45,
            cues = listOf(
                "15 seconds each side",
                "Elevate top arm toward ceiling",
                "Maintain rigid lateral pillar posture"
            ),
            bronzeThreshold = 15,
            silverThreshold = 30,
            goldThreshold = 45
        )
    )

    val athleteExercises = listOf(
        // Warm-up
        Exercise(
            id = "ath_jog",
            name = "Easy Jog Warm-up",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 1,
            targetDurationSeconds = 300,
            restSeconds = 30,
            cues = listOf(
                "Light rhythmic jogging on the spot or track",
                "Elevate heart rate and body temperature gradually"
            ),
            bronzeThreshold = 180,
            silverThreshold = 240,
            goldThreshold = 300
        ),
        Exercise(
            id = "ath_jumping_jacks",
            name = "Jumping Jacks",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 30,
            restSeconds = 30,
            cues = listOf(
                "Land softly on balls of feet",
                "Full arm extension overhead with rhythmic breath"
            ),
            bronzeThreshold = 20,
            silverThreshold = 30,
            goldThreshold = 45
        ),
        Exercise(
            id = "ath_leg_swings",
            name = "Dynamic Leg Swings",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.REPS,
            targetSets = 1,
            targetReps = 10,
            restSeconds = 20,
            cues = listOf(
                "Hold wall for balance",
                "Swing forward and back dynamically (10 each leg)",
                "Open up hips and hamstrings"
            ),
            bronzeThreshold = 6,
            silverThreshold = 10,
            goldThreshold = 15
        ),
        Exercise(
            id = "ath_arm_circles",
            name = "Arm Circles",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 1,
            targetDurationSeconds = 20,
            restSeconds = 20,
            cues = listOf(
                "Small to large progressive circles",
                "Warm up rotator cuff and shoulders"
            ),
            bronzeThreshold = 15,
            silverThreshold = 20,
            goldThreshold = 30
        ),
        // Conditioning
        Exercise(
            id = "ath_sprints",
            name = "Sprint Intervals",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 8,
            targetDurationSeconds = 15,
            restSeconds = 75,
            cues = listOf(
                "8 × 15 seconds high-effort explosive sprints",
                "60-90s walking recovery between intervals",
                "Allow fast walking or jogging as beginner alternative"
            ),
            bronzeThreshold = 4,
            silverThreshold = 6,
            goldThreshold = 8,
            unit = "intervals"
        ),
        // Athletic movements
        Exercise(
            id = "ath_high_knees",
            name = "High Knees",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 30,
            restSeconds = 45,
            cues = listOf(
                "Drive knees upward to hip level with speed",
                "Pump arms actively in sync with steps"
            ),
            bronzeThreshold = 20,
            silverThreshold = 30,
            goldThreshold = 45
        ),
        Exercise(
            id = "ath_skater_steps",
            name = "Skater Steps",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 12,
            restSeconds = 45,
            cues = listOf(
                "Lateral bounding jump from side to side",
                "Land softly on single leg with bent knee",
                "Build dynamic knee and ankle stability"
            ),
            bronzeThreshold = 8,
            silverThreshold = 12,
            goldThreshold = 18
        ),
        Exercise(
            id = "ath_bear_crawl",
            name = "Bear Crawl",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 15,
            restSeconds = 45,
            cues = listOf(
                "Knees hovering 1 inch off floor, back flat like table",
                "Move contralateral arm and foot simultaneously"
            ),
            bronzeThreshold = 10,
            silverThreshold = 15,
            goldThreshold = 25
        ),
        Exercise(
            id = "ath_mountain_climbers",
            name = "Mountain Climbers",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.REPS,
            targetSets = 2,
            targetReps = 20,
            restSeconds = 45,
            cues = listOf(
                "Solid plank with hands under shoulders",
                "Drive knees to chest rhythmically without bouncing hips"
            ),
            bronzeThreshold = 14,
            silverThreshold = 20,
            goldThreshold = 35
        ),
        // Cool-down
        Exercise(
            id = "ath_walk_cooldown",
            name = "Easy Walk Cool-down",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 1,
            targetDurationSeconds = 180,
            restSeconds = 10,
            cues = listOf(
                "Slow paced walk to return heart rate to baseline",
                "Deep diaphragmatic box breathing"
            ),
            bronzeThreshold = 60,
            silverThreshold = 120,
            goldThreshold = 180
        ),
        Exercise(
            id = "ath_quad_stretch",
            name = "Gentle Quad Stretch",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 30,
            restSeconds = 10,
            cues = listOf(
                "Hold foot to glute, knees close together",
                "30 seconds each side"
            ),
            bronzeThreshold = 20,
            silverThreshold = 30,
            goldThreshold = 45
        ),
        Exercise(
            id = "ath_calf_stretch",
            name = "Calf Stretch",
            category = ExerciseCategory.ATHLETE,
            type = ExerciseType.TIME,
            targetSets = 2,
            targetDurationSeconds = 30,
            restSeconds = 10,
            cues = listOf(
                "Press back heel into floor with straight knee",
                "30 seconds each side"
            ),
            bronzeThreshold = 20,
            silverThreshold = 30,
            goldThreshold = 45
        )
    )

    fun getAllDefaultRoutines(): List<WorkoutRoutine> = listOf(
        WorkoutRoutine(
            id = "routine_mon",
            name = "Push Stage 01",
            description = "Fundamental pushing strength targeting chest, front deltoids, and triceps with strict calisthenics form.",
            category = ExerciseCategory.PUSH,
            assignedDay = 1,
            isDefault = true,
            exercises = pushExercises
        ),
        WorkoutRoutine(
            id = "routine_tue",
            name = "Pull Stage 01",
            description = "Strict vertical pulling progression and dead hangs to build foundational back width and grip strength.",
            category = ExerciseCategory.PULL,
            assignedDay = 2,
            isDefault = true,
            exercises = pullExercises
        ),
        WorkoutRoutine(
            id = "routine_wed",
            name = "Legs Stage 01",
            description = "Lower body power, knee resilience, and isometric endurance with squats, reverse lunges, and wall sits.",
            category = ExerciseCategory.LEGS,
            assignedDay = 3,
            isDefault = true,
            exercises = legsExercises
        ),
        WorkoutRoutine(
            id = "routine_thu",
            name = "Core Stability Stage 01",
            description = "Anti-extension and rotational core stability focusing on dead bugs, bird dogs, and lateral side planks.",
            category = ExerciseCategory.CORE_STABILITY,
            assignedDay = 4,
            isDefault = true,
            exercises = coreStabilityExercises
        ),
        WorkoutRoutine(
            id = "routine_fri",
            name = "Core Strength Stage 01",
            description = "Intense flexion, pelvic control, and endurance planks to harden the abdominal wall and posterior chain.",
            category = ExerciseCategory.CORE_STRENGTH,
            assignedDay = 5,
            isDefault = true,
            exercises = coreStrengthExercises
        ),
        WorkoutRoutine(
            id = "routine_sat",
            name = "Athlete Stage 01",
            description = "Dynamic athletic conditioning: warm-up, explosive sprint intervals, multi-planar movements, and mobility cool-down.",
            category = ExerciseCategory.ATHLETE,
            assignedDay = 6,
            isDefault = true,
            exercises = athleteExercises
        ),
        WorkoutRoutine(
            id = "routine_sun",
            name = "Complete Rest",
            description = "Full recovery day. Focus on hydration, sleep, protein intake, and muscular repair.",
            category = ExerciseCategory.ATHLETE,
            assignedDay = 7,
            isDefault = true,
            exercises = emptyList()
        )
    )

    fun getRoutineForDay(dayOfWeek: Int): WorkoutRoutine {
        return getAllDefaultRoutines().firstOrNull { it.assignedDay == dayOfWeek }
            ?: getAllDefaultRoutines().first()
    }

    // Required key exercises to measure Stage 01 mastery (one key exercise per category)
    val stage01RequiredExercises = listOf(
        pushExercises.first { it.id == "push_std" },
        pullExercises.first { it.id == "pull_strict_chin" },
        legsExercises.first { it.id == "leg_squat" },
        coreStabilityExercises.first { it.id == "core_side_plank_stab" },
        coreStrengthExercises.first { it.id == "core_std_plank" },
        athleteExercises.first { it.id == "ath_sprints" }
    )

    fun calculateRoutineStageRank(
        routine: WorkoutRoutine,
        performances: Map<String, com.example.data.ExercisePerformanceEntity>
    ): RoutineRankStatus {
        if (routine.exercises.isEmpty()) {
            return RoutineRankStatus(
                rank = PerformanceRank.UNRANKED,
                nextRank = PerformanceRank.BRONZE,
                exercisesAtOrAboveNext = 0,
                totalExercises = 0,
                laggingExercises = emptyList()
            )
        }

        val exerciseRanks = routine.exercises.map { ex ->
            val p = performances[ex.id]
            val rank = if (p != null) {
                try {
                    PerformanceRank.valueOf(p.currentRank)
                } catch (_: Exception) {
                    PerformanceRank.UNRANKED
                }
            } else {
                PerformanceRank.UNRANKED
            }
            Pair(ex, rank)
        }

        val overallRank = when {
            exerciseRanks.all { it.second == PerformanceRank.GOLD } -> PerformanceRank.GOLD
            exerciseRanks.all { it.second.levelNumber >= PerformanceRank.SILVER.levelNumber } -> PerformanceRank.SILVER
            exerciseRanks.all { it.second.levelNumber >= PerformanceRank.BRONZE.levelNumber } -> PerformanceRank.BRONZE
            else -> PerformanceRank.UNRANKED
        }

        val nextTargetRank = when (overallRank) {
            PerformanceRank.UNRANKED -> PerformanceRank.BRONZE
            PerformanceRank.BRONZE -> PerformanceRank.SILVER
            PerformanceRank.SILVER -> PerformanceRank.GOLD
            PerformanceRank.GOLD -> null
        }

        val atOrAboveNext = if (nextTargetRank != null) {
            exerciseRanks.count { it.second.levelNumber >= nextTargetRank.levelNumber }
        } else {
            routine.exercises.size
        }

        val lagging = if (nextTargetRank != null) {
            exerciseRanks.filter { it.second.levelNumber < nextTargetRank.levelNumber }.map { it.first }
        } else emptyList()

        return RoutineRankStatus(
            rank = overallRank,
            nextRank = nextTargetRank,
            exercisesAtOrAboveNext = atOrAboveNext,
            totalExercises = routine.exercises.size,
            laggingExercises = lagging
        )
    }
}

data class RoutineRankStatus(
    val rank: PerformanceRank,
    val nextRank: PerformanceRank?,
    val exercisesAtOrAboveNext: Int,
    val totalExercises: Int,
    val laggingExercises: List<Exercise>
)
