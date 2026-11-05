package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.SoundManager
import com.example.sound.SoundSettings
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SoundSettingsDialog(
    settings: SoundSettings,
    soundManager: SoundManager,
    onUpdateSettings: (SoundSettings) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCharcoal,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SOUND SYSTEM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Master Volume Slider
                Text(
                    text = "MASTER VOLUME: ${(settings.volume * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElectricYellow,
                    fontSize = 11.sp
                )

                Slider(
                    value = settings.volume,
                    onValueChange = { newVol ->
                        onUpdateSettings(settings.copy(volume = newVol))
                    },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricYellow,
                        activeTrackColor = ElectricYellow,
                        inactiveTrackColor = CardCharcoal
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Master Switch
                SoundToggleRow(
                    label = "Master Sound",
                    subtitle = "Master audio power toggle",
                    enabled = settings.masterEnabled,
                    onToggle = { onUpdateSettings(settings.copy(masterEnabled = it)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // UI Sounds
                SoundToggleRow(
                    label = "UI & Tap Sounds",
                    subtitle = "Buttons and navigation chirps",
                    enabled = settings.uiSoundsEnabled && settings.masterEnabled,
                    onToggle = {
                        onUpdateSettings(settings.copy(uiSoundsEnabled = it))
                        if (it) soundManager.playButtonClick()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Workout Sounds
                SoundToggleRow(
                    label = "Workout Audio",
                    subtitle = "Set complete & timer beeps",
                    enabled = settings.workoutSoundsEnabled && settings.masterEnabled,
                    onToggle = {
                        onUpdateSettings(settings.copy(workoutSoundsEnabled = it))
                        if (it) soundManager.playSetComplete()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Achievement Sounds
                SoundToggleRow(
                    label = "Achievement Fanfare",
                    subtitle = "Triumphant unlocks & PR alerts",
                    enabled = settings.achievementSoundsEnabled && settings.masterEnabled,
                    onToggle = {
                        onUpdateSettings(settings.copy(achievementSoundsEnabled = it))
                        if (it) soundManager.playAchievementUnlock()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Startup Sound
                SoundToggleRow(
                    label = "Startup Sound",
                    subtitle = "Boot charging pulse",
                    enabled = settings.startupSoundEnabled && settings.masterEnabled,
                    onToggle = {
                        onUpdateSettings(settings.copy(startupSoundEnabled = it))
                        if (it) soundManager.playStartupSound()
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Test audio buttons
                Text(
                    text = "AUDITION SYNTH SOUNDS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        text = "Set Beep",
                        onClick = { soundManager.playSetComplete() },
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "Fanfare",
                        onClick = { soundManager.playAchievementUnlock() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            ElectricButton(
                text = "DONE",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@Composable
private fun SoundToggleRow(
    label: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardCharcoal)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AmoledBlack,
                checkedTrackColor = ElectricYellow,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = DarkCharcoal
            )
        )
    }
}
