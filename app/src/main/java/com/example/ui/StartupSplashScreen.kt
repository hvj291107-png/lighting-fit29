package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BrandLuxuryFontFamily
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StartupSplashScreen(
    onFinished: () -> Unit
) {
    val logoScale = remember { Animatable(0.82f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val badgeAlpha = remember { Animatable(0f) }

    // Subtle breathing glow animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 36f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowElevation"
    )

    LaunchedEffect(Unit) {
        // 1. Logo entrance & gentle pop
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }

        // 2. Slow, cinematic luxury fade for LIGHTNING FITS text for 3 seconds
        delay(400)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
            )
        }

        delay(800)
        launch {
            badgeAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2200, easing = LinearOutSlowInEasing)
            )
        }

        // Total smooth display for 3.4 seconds before transitioning to Home
        delay(3400)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic Razor-Sharp Diagonal Gold Light Rays (Top-Left and Bottom-Right)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 2.5f
            val glowColor = Color(0x66FFE600)
            val lineColor = Color(0xFFFFE600)

            // Top-left diagonal beam 1
            drawLine(
                color = glowColor,
                start = Offset(-40f, size.height * 0.28f),
                end = Offset(size.width * 0.45f, -40f),
                strokeWidth = 8f
            )
            drawLine(
                color = lineColor,
                start = Offset(-40f, size.height * 0.28f),
                end = Offset(size.width * 0.45f, -40f),
                strokeWidth = strokeW
            )

            // Top-left diagonal beam 2
            drawLine(
                color = Color(0x33FFE600),
                start = Offset(-40f, size.height * 0.16f),
                end = Offset(size.width * 0.25f, -40f),
                strokeWidth = strokeW
            )

            // Bottom-right diagonal beam 1
            drawLine(
                color = glowColor,
                start = Offset(size.width * 0.55f, size.height + 40f),
                end = Offset(size.width + 40f, size.height * 0.72f),
                strokeWidth = 8f
            )
            drawLine(
                color = lineColor,
                start = Offset(size.width * 0.55f, size.height + 40f),
                end = Offset(size.width + 40f, size.height * 0.72f),
                strokeWidth = strokeW
            )

            // Bottom-right diagonal beam 2
            drawLine(
                color = Color(0x33FFE600),
                start = Offset(size.width * 0.72f, size.height + 40f),
                end = Offset(size.width + 40f, size.height * 0.84f),
                strokeWidth = strokeW
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Central Emblem: Black rounded square with glowing electric yellow border & yellow lightning bolt
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .shadow(elevation = glowPulse.dp, shape = RoundedCornerShape(32.dp), spotColor = ElectricYellow)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF0A0A0C))
                    .border(2.5.dp, ElectricYellow, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Electric Yellow Lightning Bolt
                Canvas(modifier = Modifier.size(70.dp)) {
                    val w = size.width
                    val h = size.height

                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.58f, 0f)
                        lineTo(w * 0.20f, h * 0.52f)
                        lineTo(w * 0.50f, h * 0.52f)
                        lineTo(w * 0.35f, h)
                        lineTo(w * 0.85f, h * 0.44f)
                        lineTo(w * 0.52f, h * 0.44f)
                        close()
                    }
                    drawPath(path = path, color = ElectricYellow)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // "LIGHTNING" in luxury Syne typography with metallic gradient
            Text(
                text = "LIGHTNING",
                fontFamily = BrandLuxuryFontFamily,
                style = MaterialTheme.typography.displayLarge.copy(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFD4D4DC))
                    )
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // "— FITS —" in luxury Syne typography with gold wings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                // Left laser wing
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, ElectricYellow)
                            )
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "FITS",
                    fontFamily = BrandLuxuryFontFamily,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricYellow,
                    fontSize = 34.sp,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Right laser wing
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(ElectricYellow, Color.Transparent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle: "TRAIN • PROGRESS • EVOLVE"
            Text(
                text = "TRAIN  •  PROGRESS  •  EVOLVE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFA1A1AA),
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(badgeAlpha.value)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // "BETA v1.0" pill badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F0F12))
                    .border(1.dp, ElectricYellow.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .alpha(badgeAlpha.value)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BETA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricYellow,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "v1.0",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
