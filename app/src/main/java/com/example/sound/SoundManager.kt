package com.example.sound

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

data class SoundSettings(
    val masterEnabled: Boolean = true,
    val uiSoundsEnabled: Boolean = true,
    val workoutSoundsEnabled: Boolean = true,
    val achievementSoundsEnabled: Boolean = true,
    val startupSoundEnabled: Boolean = true,
    val volume: Float = 0.8f
)

class SoundManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lightning_sound_prefs", Context.MODE_PRIVATE)
    private val soundScope = CoroutineScope(Dispatchers.Default)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<SoundSettings> = _settings.asStateFlow()

    private val sampleRate = 44100

    // Pre-cached audio buffers for instant zero-latency playback
    private val audioCache = ConcurrentHashMap<String, ShortArray>()
    @Volatile private var staticClickTrack: AudioTrack? = null
    @Volatile private var staticNavTrack: AudioTrack? = null

    init {
        // Pre-compute sound buffers and pre-allocate static AudioTracks for zero latency
        soundScope.launch {
            val clickBuf = generateClickBuffer()
            val navBuf = generateNavBuffer()
            audioCache["startup"] = generateStartupBuffer()
            audioCache["click"] = clickBuf
            audioCache["nav"] = navBuf
            audioCache["set_complete"] = generateSetCompleteBuffer()
            audioCache["timer_complete"] = generateTimerCompleteBuffer()
            audioCache["achievement"] = generateAchievementBuffer()
            audioCache["workout_complete"] = generateWorkoutCompleteBuffer()

            try {
                staticClickTrack = buildStaticTrack(clickBuf)
                staticNavTrack = buildStaticTrack(navBuf)
            } catch (_: Exception) {}
        }
    }

    private fun buildStaticTrack(buffer: ShortArray): AudioTrack {
        val currentVol = _settings.value.volume.coerceIn(0f, 1f)
        val scaled = ShortArray(buffer.size) { i ->
            (buffer[i] * currentVol).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(scaled.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(scaled, 0, scaled.size)
        return track
    }

    private fun loadSettings(): SoundSettings {
        return SoundSettings(
            masterEnabled = prefs.getBoolean("master_enabled", true),
            uiSoundsEnabled = prefs.getBoolean("ui_sounds_enabled", true),
            workoutSoundsEnabled = prefs.getBoolean("workout_sounds_enabled", true),
            achievementSoundsEnabled = prefs.getBoolean("achievement_sounds_enabled", true),
            startupSoundEnabled = prefs.getBoolean("startup_sound_enabled", true),
            volume = prefs.getFloat("volume", 0.8f)
        )
    }

    fun updateSettings(newSettings: SoundSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putBoolean("master_enabled", newSettings.masterEnabled)
            .putBoolean("ui_sounds_enabled", newSettings.uiSoundsEnabled)
            .putBoolean("workout_sounds_enabled", newSettings.workoutSoundsEnabled)
            .putBoolean("achievement_sounds_enabled", newSettings.achievementSoundsEnabled)
            .putBoolean("startup_sound_enabled", newSettings.startupSoundEnabled)
            .putFloat("volume", newSettings.volume)
            .apply()
    }

    private fun playPcm(buffer: ShortArray) {
        val current = _settings.value
        if (!current.masterEnabled || current.volume <= 0f) return

        soundScope.launch {
            try {
                val scaledBuffer = ShortArray(buffer.size)
                val vol = current.volume.coerceIn(0f, 1f)
                for (i in buffer.indices) {
                    scaledBuffer[i] = (buffer[i] * vol).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val bufferSize = scaledBuffer.size * 2
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(scaledBuffer, 0, scaledBuffer.size)
                audioTrack.play()
                val durationMs = (scaledBuffer.size * 1000L) / sampleRate
                kotlinx.coroutines.delay(durationMs + 30)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    // --- Premium Luxury Startup Sound ---
    // Deep cinematic bass swell paired with crystal glass harmonic chime chord
    private fun generateStartupBuffer(): ShortArray {
        val durationSeconds = 1.2
        val totalSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate

            // Warm cinematic sub-bass body (65Hz -> 82Hz gentle swell)
            val bassEnv = if (t < 0.25) (t / 0.25) else exp(-(t - 0.25) * 2.8)
            val bassWave = sin(2.0 * PI * (65.0 + 17.0 * (t / durationSeconds)) * t)

            // Pristine crystal glass bell chord (C#5: 554.37Hz, G#5: 830.61Hz, C#6: 1108.73Hz, E#6: 1396.91Hz)
            val chimeEnv = if (t < 0.08) (t / 0.08) else exp(-(t - 0.08) * 3.2)
            val chimeWave = 0.55 * sin(2.0 * PI * 554.37 * t) +
                    0.40 * sin(2.0 * PI * 830.61 * t) +
                    0.30 * sin(2.0 * PI * 1108.73 * t) +
                    0.15 * sin(2.0 * PI * 1396.91 * t)

            val mixed = (0.50 * bassWave * bassEnv) + (0.50 * chimeWave * chimeEnv)
            buffer[i] = (mixed * 26000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateClickBuffer(): ShortArray {
        // High-end tactile acoustic click: crisp micro-transient with warm mechanical body
        val durationSeconds = 0.018
        val totalSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 220.0)
            // Down-chirping transient for tactile snap
            val freq = 2200.0 - (1400.0 * (t / durationSeconds))
            val transient = sin(2.0 * PI * freq * t)
            // Subtle body tone
            val body = 0.3 * sin(2.0 * PI * 220.0 * t) * exp(-t * 180.0)
            val mixed = (transient + body) * decay
            buffer[i] = (mixed * 24000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateNavBuffer(): ShortArray {
        // Refined soft menu navigation tap
        val durationSeconds = 0.024
        val totalSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 140.0)
            val freq = 1250.0 - (450.0 * (t / durationSeconds))
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * decay * 18000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateSetCompleteBuffer(): ShortArray {
        val noteDur = 0.07
        val totalSamples = (sampleRate * noteDur * 3).toInt()
        val buffer = ShortArray(totalSamples)
        val freqs = doubleArrayOf(587.33, 739.99, 880.00) // D5, F#5, A5 uplifting triad

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val noteIndex = (t / noteDur).toInt().coerceIn(0, 2)
            val noteT = t - (noteIndex * noteDur)
            val env = exp(-noteT * 20.0)
            val wave = sin(2.0 * PI * freqs[noteIndex] * t) + 0.25 * sin(2.0 * PI * (freqs[noteIndex] * 2) * t)
            buffer[i] = (wave * env * 24000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateTimerCompleteBuffer(): ShortArray {
        val durationSeconds = 0.3
        val totalSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val pulse = if (t < 0.12) 880.0 else if (t in 0.15..0.27) 1174.0 else 0.0
            val subT = if (t < 0.12) t else (t - 0.15)
            val env = if (pulse > 0.0) exp(-subT * 18.0) else 0.0
            val wave = sin(2.0 * PI * pulse * t)
            buffer[i] = (wave * env * 25000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateAchievementBuffer(): ShortArray {
        val noteDur = 0.09
        val totalSamples = (sampleRate * noteDur * 4).toInt()
        val buffer = ShortArray(totalSamples)
        val freqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val noteIndex = (t / noteDur).toInt().coerceIn(0, 3)
            val noteT = t - (noteIndex * noteDur)
            val env = exp(-noteT * 12.0)
            val wave = sin(2.0 * PI * freqs[noteIndex] * t) + 0.3 * sin(2.0 * PI * (freqs[noteIndex] * 1.5) * t)
            buffer[i] = (wave * env * 26000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateWorkoutCompleteBuffer(): ShortArray {
        val durationSeconds = 0.65
        val totalSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(totalSamples)
        val notes = doubleArrayOf(440.0, 554.37, 659.25, 880.0)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val noteIdx = (t / 0.14).toInt().coerceIn(0, 3)
            val noteT = t - (noteIdx * 0.14)
            val env = exp(-noteT * 9.0)
            val wave = sin(2.0 * PI * notes[noteIdx] * t) + 0.25 * sin(2.0 * PI * (notes[noteIdx] * 2) * t)
            buffer[i] = (wave * env * 26000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    fun playStartupSound() {
        if (!_settings.value.masterEnabled || !_settings.value.startupSoundEnabled) return
        val buf = audioCache["startup"] ?: generateStartupBuffer()
        playPcm(buf)
    }

    fun playButtonClick() {
        if (!_settings.value.masterEnabled || !_settings.value.uiSoundsEnabled) return
        val track = staticClickTrack
        if (track != null && track.state == AudioTrack.STATE_INITIALIZED) {
            try {
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.stop()
                }
                track.setPlaybackHeadPosition(0)
                track.play()
                return
            } catch (_: Exception) {}
        }
        val buf = audioCache["click"] ?: generateClickBuffer()
        playPcm(buf)
    }

    fun playNavigation() {
        if (!_settings.value.masterEnabled || !_settings.value.uiSoundsEnabled) return
        val track = staticNavTrack
        if (track != null && track.state == AudioTrack.STATE_INITIALIZED) {
            try {
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.stop()
                }
                track.setPlaybackHeadPosition(0)
                track.play()
                return
            } catch (_: Exception) {}
        }
        val buf = audioCache["nav"] ?: generateNavBuffer()
        playPcm(buf)
    }

    fun playSetComplete() {
        if (!_settings.value.masterEnabled || !_settings.value.workoutSoundsEnabled) return
        val buf = audioCache["set_complete"] ?: generateSetCompleteBuffer()
        playPcm(buf)
    }

    fun playTimerComplete() {
        if (!_settings.value.masterEnabled || !_settings.value.workoutSoundsEnabled) return
        val buf = audioCache["timer_complete"] ?: generateTimerCompleteBuffer()
        playPcm(buf)
    }

    fun playAchievementUnlock() {
        if (!_settings.value.masterEnabled || !_settings.value.achievementSoundsEnabled) return
        val buf = audioCache["achievement"] ?: generateAchievementBuffer()
        playPcm(buf)
    }

    fun playWorkoutComplete() {
        if (!_settings.value.masterEnabled || !_settings.value.workoutSoundsEnabled) return
        val buf = audioCache["workout_complete"] ?: generateWorkoutCompleteBuffer()
        playPcm(buf)
    }
}
