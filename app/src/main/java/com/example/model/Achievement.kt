package com.example.model

enum class AchievementRarity(val label: String) {
    COMMON("COMMON"),
    UNCOMMON("UNCOMMON"),
    RARE("RARE"),
    EPIC("EPIC"),
    LEGENDARY("LEGENDARY")
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val rarity: AchievementRarity,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long = 0L
)

object DefaultAchievements {
    fun findById(id: String): Achievement? = initialList.firstOrNull { it.id == id }

    val initialList = listOf(
        Achievement(
            id = "ach_started",
            title = "Lightning Routine Started",
            description = "Began your journey with the official Stage 01 Lightning Routine.",
            rarity = AchievementRarity.COMMON,
            iconName = "bolt"
        ),
        Achievement(
            id = "ach_first_pushup",
            title = "First Push-up",
            description = "Completed your first set of standard or wide push-ups with clean form.",
            rarity = AchievementRarity.COMMON,
            iconName = "fitness_center"
        ),
        Achievement(
            id = "ach_first_chinup",
            title = "First Chin-up",
            description = "Executed your first strict dead-hang chin-up with zero momentum.",
            rarity = AchievementRarity.COMMON,
            iconName = "straighten"
        ),
        Achievement(
            id = "ach_push_cleared",
            title = "Push Stage 01 Cleared",
            description = "Completed a full Monday Push session without skipping any sets.",
            rarity = AchievementRarity.COMMON,
            iconName = "shield"
        ),
        Achievement(
            id = "ach_pull_cleared",
            title = "Pull Stage 01 Cleared",
            description = "Completed a full Tuesday Pull session including 4 strict singles.",
            rarity = AchievementRarity.COMMON,
            iconName = "shield"
        ),
        Achievement(
            id = "ach_legs_cleared",
            title = "Legs Stage 01 Cleared",
            description = "Completed all sets of Wednesday Legs and survived the wall sits.",
            rarity = AchievementRarity.COMMON,
            iconName = "directions_run"
        ),
        Achievement(
            id = "ach_core_stab_cleared",
            title = "Core Stability Cleared",
            description = "Mastered Thursday Core Stability with locked dead bugs and side planks.",
            rarity = AchievementRarity.COMMON,
            iconName = "lock"
        ),
        Achievement(
            id = "ach_core_str_cleared",
            title = "Core Strength Cleared",
            description = "Conquered Friday Core Strength including maximum tension planks.",
            rarity = AchievementRarity.COMMON,
            iconName = "local_fire_department"
        ),
        Achievement(
            id = "ach_ath_cleared",
            title = "Athlete Stage 01 Cleared",
            description = "Finished all 8 sprint intervals and athletic mobility drills.",
            rarity = AchievementRarity.COMMON,
            iconName = "speed"
        ),
        Achievement(
            id = "ach_first_gold",
            title = "First Gold Rank",
            description = "Elevated any Stage 01 calisthenics movement to the coveted Gold tier.",
            rarity = AchievementRarity.EPIC,
            iconName = "workspace_premium"
        ),
        Achievement(
            id = "ach_pr",
            title = "Personal Record",
            description = "Surpassed your previous best repetitions or hold time in any exercise.",
            rarity = AchievementRarity.UNCOMMON,
            iconName = "trending_up"
        ),
        Achievement(
            id = "ach_10_workouts",
            title = "10 Workouts Completed",
            description = "Completed 10 total training sessions in Lightning Fits.",
            rarity = AchievementRarity.RARE,
            iconName = "military_tech"
        ),
        Achievement(
            id = "ach_50_workouts",
            title = "50 Workouts Completed",
            description = "Reached the elite threshold of 50 completed calisthenics sessions.",
            rarity = AchievementRarity.LEGENDARY,
            iconName = "diamond"
        ),
        Achievement(
            id = "ach_7_streak",
            title = "7-Day Consistency",
            description = "Trained consistently throughout a full 7-day weekly schedule.",
            rarity = AchievementRarity.RARE,
            iconName = "event_available"
        ),
        Achievement(
            id = "ach_30_streak",
            title = "30-Day Consistency",
            description = "Built an unshakeable month-long habit of calisthenics dedication.",
            rarity = AchievementRarity.LEGENDARY,
            iconName = "star"
        ),
        Achievement(
            id = "ach_stage01_mastered",
            title = "Stage 01 Mastered",
            description = "Achieved Gold Rank across all 6 core disciplines. Eligible for Stage 02.",
            rarity = AchievementRarity.LEGENDARY,
            iconName = "emoji_events"
        )
    )
}
