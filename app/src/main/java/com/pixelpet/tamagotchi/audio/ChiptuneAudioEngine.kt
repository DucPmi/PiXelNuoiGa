package com.pixelpet.tamagotchi.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin

object ChiptuneAudioEngine {

    private const val SAMPLE_RATE = 22050

    @Volatile
    private var bgmRunning = false

    @Volatile
    var isSoundEnabled: Boolean = true

    private var bgmThread: Thread? = null

    private val melodyNotes = listOf(
        523.25 to 170, 659.25 to 170, 783.99 to 170, 1046.50 to 260,
        0.0 to 90,
        783.99 to 170, 880.00 to 170, 783.99 to 170, 659.25 to 260,
        0.0 to 90,
        587.33 to 170, 659.25 to 170, 783.99 to 170, 659.25 to 170,
        523.25 to 220, 587.33 to 220, 523.25 to 320,
        0.0 to 200
    )

    fun startBgm() {
        if (!isSoundEnabled || bgmRunning) return
        bgmRunning = true
        bgmThread = thread(start = true, isDaemon = true, name = "ChiptuneBgm") {
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(2048)

            val track = runCatching {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            }.getOrNull() ?: run {
                bgmRunning = false
                return@thread
            }

            runCatching {
                track.play()
                var idx = 0
                while (bgmRunning && isSoundEnabled) {
                    val (freq, ms) = melodyNotes[idx % melodyNotes.size]
                    val pcm = generateSquareWave(freq, ms, volume = 0.08)
                    track.write(pcm, 0, pcm.size)
                    idx++
                }
            }

            runCatching {
                track.stop()
                track.release()
            }
            bgmRunning = false
        }
    }

    fun stopBgm() {
        bgmRunning = false
        bgmThread = null
    }

    fun playFeed() = playSequence(listOf(523.25 to 70, 659.25 to 70, 783.99 to 100))
    fun playClean() = playSequence(listOf(783.99 to 60, 987.77 to 60, 1174.66 to 110))
    fun playPlay() = playSequence(listOf(587.33 to 70, 783.99 to 70, 987.77 to 70, 1174.66 to 120))
    fun playSleep() = playSequence(listOf(523.25 to 120, 440.00 to 120, 349.23 to 180))
    fun playHeal() = playSequence(listOf(659.25 to 80, 783.99 to 80, 1046.50 to 150))
    fun playFarm() = playSequence(listOf(440.00 to 60, 587.33 to 60, 880.00 to 110))
    fun playBake() = playSequence(listOf(523.25 to 70, 659.25 to 70, 783.99 to 70, 1046.50 to 140))
    fun playTick() = playSequence(listOf(880.0 to 38))
    fun playFanfare() = playSequence(listOf(523.25 to 85, 659.25 to 85, 783.99 to 85, 1046.50 to 220))

    private fun playSequence(notes: List<Pair<Double, Int>>) {
        if (!isSoundEnabled) return
        thread(start = true, isDaemon = true, name = "ChiptuneSfx") {
            runCatching {
                val totalSamples = notes.sumOf { (SAMPLE_RATE * it.second) / 1000 }
                val buffer = ShortArray(totalSamples)
                var offset = 0
                for ((freq, ms) in notes) {
                    val seg = generateSquareWave(freq, ms, volume = 0.20)
                    seg.copyInto(buffer, offset)
                    offset += seg.size
                }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes((buffer.size * 2).coerceAtLeast(2048))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buffer, 0, buffer.size)
                track.play()
                val totalMs = notes.sumOf { it.second }.toLong()
                Thread.sleep(totalMs + 60L)
                track.stop()
                track.release()
            }
        }
    }

    private fun generateSquareWave(freqHz: Double, durationMs: Int, volume: Double): ShortArray {
        val count = (SAMPLE_RATE * durationMs) / 1000
        val out = ShortArray(count)
        if (freqHz <= 0.0) return out
        val amp = (Short.MAX_VALUE * volume).toInt()
        for (i in 0 until count) {
            val env = if (i > count - 120) ((count - i).toDouble() / 120.0).coerceIn(0.0, 1.0) else 1.0
            val angle = 2.0 * PI * i * freqHz / SAMPLE_RATE
            val wave = if (sin(angle) >= 0.0) 1.0 else -1.0
            out[i] = (wave * amp * env).toInt().toShort()
        }
        return out
    }
}
