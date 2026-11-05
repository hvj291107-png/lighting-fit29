package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AchievementRarity
import com.example.model.PerformanceRank
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BronzeColor
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.ElectricYellowGlow
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PurpleLegendary
import com.example.ui.theme.SilverColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.AppNavTab

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderSubtle,
    glowColor: Color = Color.Transparent,
    shape: RoundedCornerShape = RoundedCornerShape(18.dp),
    backgroundColor: Color = CardCharcoal,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        content()
    }
}

@Composable
fun ElectricButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    containerColor: Color = ElectricYellow,
    contentColor: Color = AmoledBlack,
    testTag: String = "electric_button"
) {
    Surface(
        modifier = modifier
            .testTag(testTag)
            .height(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .clickable(enabled = enabled) { onClick() },
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.4f),
        shape = RoundedCornerShape(27.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                fontSize = 15.sp,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    contentColor: Color = TextPrimary,
    borderColor: Color = BorderHighlight,
    testTag: String = "secondary_button"
) {
    Surface(
        modifier = modifier
            .testTag(testTag)
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = CardCharcoal,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RankBadgeChip(
    rank: PerformanceRank,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor, borderColor) = when (rank) {
        PerformanceRank.GOLD -> Triple(GoldColor.copy(alpha = 0.15f), GoldColor, GoldColor.copy(alpha = 0.5f))
        PerformanceRank.SILVER -> Triple(SilverColor.copy(alpha = 0.15f), SilverColor, SilverColor.copy(alpha = 0.5f))
        PerformanceRank.BRONZE -> Triple(BronzeColor.copy(alpha = 0.15f), BronzeColor, BronzeColor.copy(alpha = 0.5f))
        PerformanceRank.UNRANKED -> Triple(DarkCharcoal, TextMuted, BorderSubtle)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = fgColor,
            fontSize = 10.sp,
            letterSpacing = 0.sp
        )
    }
}

@Composable
fun RarityChip(
    rarity: AchievementRarity,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor, borderColor) = when (rarity) {
        AchievementRarity.COMMON -> Triple(Color(0xFF202024), Color(0xFFD4D4D8), BorderSubtle)
        AchievementRarity.UNCOMMON -> Triple(Color(0xFF003344), Color(0xFF38BDF8), Color(0xFF0284C7))
        AchievementRarity.RARE -> Triple(Color(0xFF1E1B4B), Color(0xFF818CF8), Color(0xFF6366F1))
        AchievementRarity.EPIC -> Triple(Color(0xFF3F2B00), ElectricYellow, ElectricYellow)
        AchievementRarity.LEGENDARY -> Triple(Color(0xFF3B0764), PurpleLegendary, PurpleLegendary)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rarity.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = fgColor,
            fontSize = 10.sp,
            letterSpacing = 0.sp,
            maxLines = 1
        )
    }
}

@Composable
fun CircularProgressTimer(
    fraction: Float,
    timeText: String,
    statusText: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 300),
        label = "timerProgress"
    )

    val strokeColor = if (isCompleted) NeonGreen else ElectricYellow
    val glowBrush = Brush.radialGradient(
        colors = listOf(
            if (isCompleted) NeonGreenGlow else ElectricYellowGlow,
            Color.Transparent
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .background(glowBrush, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val strokeWidth = 14.dp.toPx()
            // Track background
            drawCircle(
                color = CardCharcoal,
                radius = (this.size.minDimension - strokeWidth) / 2,
                style = Stroke(width = strokeWidth)
            )
            // Active sweep
            drawArc(
                color = strokeColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.displayLarge,
                color = if (isCompleted) NeonGreen else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 44.sp,
                letterSpacing = 0.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                color = if (isCompleted) NeonGreen else TextSecondary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
fun ModernBottomNav(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = AmoledBlack,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFF1C1C20),
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                )
                .background(AmoledBlack)
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    tab = AppNavTab.HOME,
                    label = "HOME",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    isSelected = currentTab == AppNavTab.HOME,
                    onClick = { onTabSelected(AppNavTab.HOME) }
                )
                BottomNavItem(
                    tab = AppNavTab.TRAINING,
                    label = "TRAINING",
                    selectedIcon = Icons.Filled.FitnessCenter,
                    unselectedIcon = Icons.Outlined.FitnessCenter,
                    isSelected = currentTab == AppNavTab.TRAINING,
                    onClick = { onTabSelected(AppNavTab.TRAINING) }
                )
                BottomNavItem(
                    tab = AppNavTab.ROUTINES,
                    label = "MY ROUTINES",
                    selectedIcon = Icons.Filled.Assignment,
                    unselectedIcon = Icons.Outlined.Assignment,
                    isSelected = currentTab == AppNavTab.ROUTINES,
                    onClick = { onTabSelected(AppNavTab.ROUTINES) }
                )
                BottomNavItem(
                    tab = AppNavTab.PROGRESS,
                    label = "PROGRESS",
                    selectedIcon = Icons.Filled.BarChart,
                    unselectedIcon = Icons.Outlined.BarChart,
                    isSelected = currentTab == AppNavTab.PROGRESS,
                    onClick = { onTabSelected(AppNavTab.PROGRESS) }
                )
                BottomNavItem(
                    tab = AppNavTab.ACHIEVEMENTS,
                    label = "ACHIEVEMENTS",
                    selectedIcon = Icons.Filled.EmojiEvents,
                    unselectedIcon = Icons.Outlined.EmojiEvents,
                    isSelected = currentTab == AppNavTab.ACHIEVEMENTS,
                    onClick = { onTabSelected(AppNavTab.ACHIEVEMENTS) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: AppNavTab,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .testTag("nav_tab_${tab.name.lowercase()}")
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) ElectricYellow else Color(0xFF8E8E93),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ElectricYellow else Color(0xFF8E8E93),
            fontSize = 9.5.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Active indicator underline pill matching reference image 2
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(if (isSelected) ElectricYellow else Color.Transparent)
        )
    }
}

@Composable
fun AchievementNotificationBanner(
    achievement: com.example.model.Achievement,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 20.dp, shape = RoundedCornerShape(18.dp), spotColor = ElectricYellowGlow)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F0F14))
            .border(1.5.dp, ElectricYellow, RoundedCornerShape(18.dp))
            .clickable { onDismiss() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ElectricYellow.copy(alpha = 0.2f))
                    .border(1.5.dp, ElectricYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = ElectricYellow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚡ ACHIEVEMENT UNLOCKED!",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = ElectricYellow,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RarityChip(rarity = achievement.rarity)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.EmojiEvents,
                contentDescription = "Dismiss",
                tint = ElectricYellow,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

