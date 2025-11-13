package com.example.alphakids.ui.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

// 🌐 URLs de música desde Firebase Storage
private const val MUSICA_FONDO_APP_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/musica_fondo_app.mp3?alt=media&token=e199b012-8522-4d1c-8f82-86c49d6a8677"

const val MUSICA_FONDO_JUEGO_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/musica_fondo_juego.mp3?alt=media&token=9ad53b6e-bc50-4b0a-a6cc-5c1913f2c889"

// 🎉 Nueva URL: Música de Éxito
const val AUDIO_EXITO_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/audio_exito.mp3?alt=media&token=8fd13d76-d100-4bff-9490-35a02138599d"

// 💀 Nueva URL: Música de Fallo
const val AUDIO_FALLO_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/audio_fallo.mp3?alt=media&token=bd92cf80-ac33-494f-aac3-bb252369cfb9"

// ⏰ Nueva URL: Audio de “Se te acabó el tiempo”
const val AUDIO_TIMEOUT_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/audio_timeout.mp3?alt=media&token=69a45c12-3478-4bc5-8b48-38ab92d019eb"

object MusicManager {

    private var musicaApp: MediaPlayer? = null
    private var musicaJuego: MediaPlayer? = null
    private var musicaExito: MediaPlayer? = null
    private var musicaFallo: MediaPlayer? = null
    private var timeoutPlayer: MediaPlayer? = null // ⏰ nuevo audio para “se te acabó el tiempo”

    // -------------------------------------------------------------------------
    // 🔊 CONTROL DE VOLUMEN
    // -------------------------------------------------------------------------
    fun setJuegoVolume(vol: Float) { musicaJuego?.setVolume(vol, vol) }
    fun setAppVolume(vol: Float) { musicaApp?.setVolume(vol, vol) }

    // -------------------------------------------------------------------------
    // 🎵 MÚSICA DE LA APP (HOME, MENÚS)
    // -------------------------------------------------------------------------
    fun startMusicaApp(context: Context) {
        if (musicaApp?.isPlaying == true) return
        stopMusicaApp()
        musicaApp = MediaPlayer().apply {
            try {
                setDataSource(MUSICA_FONDO_APP_URL)
                isLooping = true
                setOnPreparedListener { it.start() }
                prepareAsync()
            } catch (e: Exception) {
                Log.e("MusicManager", "Error startMusicaApp: ${e.message}")
            }
        }
    }

    fun pauseMusicaApp() { musicaApp?.pause() }
    fun resumeMusicaApp() { if (musicaApp != null && !musicaApp!!.isPlaying) musicaApp?.start() }
    fun stopMusicaApp() { musicaApp?.stop(); musicaApp?.release(); musicaApp = null }

    // -------------------------------------------------------------------------
    // 🎮 MÚSICA DEL JUEGO
    // -------------------------------------------------------------------------
    fun startMusicaJuego(context: Context, url: String = MUSICA_FONDO_JUEGO_URL) {
        pauseMusicaApp()
        stopMusicaJuego()
        musicaJuego = MediaPlayer().apply {
            try {
                setDataSource(url)
                isLooping = true
                setOnPreparedListener { it.start() }
                prepareAsync()
            } catch (e: IOException) {
                Log.e("MusicManager", "Error música juego: ${e.message}")
            }
        }
    }

    fun stopMusicaJuego() { musicaJuego?.stop(); musicaJuego?.release(); musicaJuego = null }

    // -------------------------------------------------------------------------
    // 🎉 MÚSICA DE ÉXITO ("¡LO LOGRASTE!")
    // -------------------------------------------------------------------------
    fun startMusicaExito(context: Context, url: String = AUDIO_EXITO_URL) {
        Log.d("MusicManager", "🎉 Iniciando música de ÉXITO con URL: $url")

        // Pausa cualquier música anterior
        pauseMusicaApp()
        stopMusicaJuego()
        stopMusicaExito()
        stopMusicaFallo()
        stopTimeoutSound()

        musicaExito = MediaPlayer().apply {
            try {
                setDataSource(url)
                isLooping = false
                setOnPreparedListener { it.start() }
                setOnCompletionListener {
                    Log.d("MusicManager", "🎉 Música de éxito terminada.")
                    stopMusicaExito()
                    resumeMusicaApp()
                }
                prepareAsync()
            } catch (e: Exception) {
                Log.e("MusicManager", "Error música de éxito: ${e.message}")
            }
        }
    }

    fun stopMusicaExito() { musicaExito?.stop(); musicaExito?.release(); musicaExito = null }

    // -------------------------------------------------------------------------
    // 💀 MÚSICA DE FALLO ("SE ACABÓ EL TIEMPO")
    // -------------------------------------------------------------------------
    fun startMusicaFallo(context: Context, url: String = AUDIO_FALLO_URL) {
        Log.d("MusicManager", "💀 Iniciando música de FALLO con URL: $url")

        // Pausa cualquier música anterior
        pauseMusicaApp()
        stopMusicaJuego()
        stopMusicaExito()
        stopMusicaFallo()
        stopTimeoutSound()

        musicaFallo = MediaPlayer().apply {
            try {
                setDataSource(url)
                isLooping = false
                setOnPreparedListener { it.start() }
                setOnCompletionListener {
                    Log.d("MusicManager", "💀 Música de fallo terminada.")
                    stopMusicaFallo()
                    resumeMusicaApp()
                }
                prepareAsync()
            } catch (e: Exception) {
                Log.e("MusicManager", "Error música de fallo: ${e.message}")
            }
        }
    }

    fun stopMusicaFallo() { musicaFallo?.stop(); musicaFallo?.release(); musicaFallo = null }

    // -------------------------------------------------------------------------
    // ⏰ AUDIO DE “SE TE ACABÓ EL TIEMPO”
    // -------------------------------------------------------------------------
    fun playTimeoutSound(context: Context, url: String = AUDIO_TIMEOUT_URL) {
        Log.d("MusicManager", "⏰ Reproduciendo audio de timeout: $url")

        stopTimeoutSound() // detener si ya se estaba reproduciendo

        timeoutPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                isLooping = false
                setOnPreparedListener { it.start() }
                setOnCompletionListener {
                    Log.d("MusicManager", "⏰ Audio timeout finalizado.")
                    stopTimeoutSound()
                }
                prepareAsync()
            } catch (e: Exception) {
                Log.e("MusicManager", "Error al reproducir timeout: ${e.message}")
            }
        }
    }

    fun stopTimeoutSound() {
        timeoutPlayer?.stop()
        timeoutPlayer?.release()
        timeoutPlayer = null
    }

    // -------------------------------------------------------------------------
    // 🧹 LIMPIEZA TOTAL
    // -------------------------------------------------------------------------
    fun releaseAllMusic() {
        stopMusicaApp()
        stopMusicaJuego()
        stopMusicaExito()
        stopMusicaFallo()
        stopTimeoutSound()
    }
}
