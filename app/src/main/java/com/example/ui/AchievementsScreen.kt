package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Achievement
import com.example.ui.theme.AmoledBlack
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AchievementsScreen(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filters = listOf("ALL", "UNLOCKED", "LOCKED")

    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size

    val filteredList = when (selectedFilterIndex) {
        1 -> achievements.filter { it.isUnlocked }
        2 -> achievements.filter { !it.isUnlocked }
        else -> achievements
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // Top Header
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)) {
            Text(
                text = "ACHIEVEMENTS",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$unlockedCount of $totalCount Unlocked",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { if (totalCount > 0) (unlockedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ElectricYellow,
                trackColor = CardCharcoalElevated,
            )
        }

        // Filter Tabs (ALL, UNLOCKED, LOCKED)
        TabRow(
            selectedTabIndex = selectedFilterIndex,
            containerColor = AmoledBlack,
            contentColor = ElectricYellow,
            divider = { HorizontalDivider(color = BorderSubtle) }
        ) {
            filters.forEachIndexed { index, title ->
                val isSelected = selectedFilterIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedFilterIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ElectricYellow else TextSecondary,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(filteredList) { ach ->
                AchievementCardItem(achievement = ach)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AchievementCardItem(
    achievement: Achievement
) {
    val isUnlocked = achievement.isUnlocked

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isUnlocked) ElectricYellow.copy(alpha = 0.4f) else BorderSubtle,
        backgroundColor = if (isUnlocked) CardCharcoal else DarkCharcoal
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) ElectricYellow.copy(alpha = 0.15f) else CardCharcoalElevated)
                    .border(1.dp, if (isUnlocked) ElectricYellow else BorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) ElectricYellow else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) TextPrimary else TextSecondary,
                        fontSize = 15.sp
                    )

                    // Rarity chip kept on one line
                    RarityChip(rarity = achievement.rarity)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) TextSecondary else TextTertiary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                if (isUnlocked && achievement.unlockedTimestamp > 0L) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val dateFormatted = try {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(achievement.unlockedTimestamp))
                    } catch (_: Exception) {
                        "Unlocked"
                    }
                    Text(
                        text = "Unlocked $dateFormatted",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonGreen,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
