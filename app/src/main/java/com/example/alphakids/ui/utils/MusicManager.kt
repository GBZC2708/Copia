package com.example.alphakids.ui.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

// 🌐 URLs de música desde Firebase Storage
private const val MUSICA_FONDO_APP_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/musica_fondo_app.mp3?alt=media&token=e199b012-8522-4d1c-8f82-86c49d6a8677"

private const val MUSICA_FONDO_JUEGO_URL =
    "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/musica_fondo_juego.mp3?alt=media&token=9ad53b6e-bc50-4b0a-a6cc-5c1913f2c889"

/**
 * Clase Singleton para gestionar y controlar todo el ciclo de vida del audio de la aplicación.
 */
object MusicManager {

    // 🎧 Reproductores privados
    private var musicaApp: MediaPlayer? = null
    private var musicaJuego: MediaPlayer? = null


    // -------------------------------------------------------------------------
    // 🔊 CONTROL DE VOLUMEN
    // -------------------------------------------------------------------------

    /** Ajusta el volumen de la música del juego (0.0f a 1.0f) */
    fun setJuegoVolume(vol: Float) {
        try {
            musicaJuego?.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.e("MusicManager", "Error setJuegoVolume: ${e.message}")
        }
    }

    /** Ajusta el volumen de la música de la app (0.0f a 1.0f) */
    fun setAppVolume(vol: Float) {
        try {
            musicaApp?.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.e("MusicManager", "Error setAppVolume: ${e.message}")
        }
    }


    // -------------------------------------------------------------------------
    // 🎵 MÚSICA DE LA APLICACIÓN (HOME, MENÚS)
    // -------------------------------------------------------------------------

    /** Reproduce la música global (de la app) */
    fun startMusicaApp(context: Context) {
        if (musicaApp?.isPlaying == true) return

        stopMusicaApp() // eliminar duplicados
        musicaApp = MediaPlayer().apply {
            try {
                setDataSource(MUSICA_FONDO_APP_URL)
                isLooping = true
                setOnPreparedListener {
                    setVolume(1f, 1f)
                    it.start()
                    Log.d("MusicManager", "Música de APP iniciada.")
                }
                setOnErrorListener { mp, what, _ ->
                    Log.e("MusicManager", "Error música app: $what")
                    mp.release()
                    musicaApp = null
                    true
                }
                prepareAsync()
            } catch (e: Exception) {
                Log.e("MusicManager", "Error startMusicaApp: ${e.message}")
            }
        }
    }

    /** Reanuda la música global */
    fun resumeMusicaApp() {
        try {
            if (musicaApp != null && !musicaApp!!.isPlaying) {
                musicaApp?.start()
                Log.d("MusicManager", "Música de APP reanudada.")
            }
        } catch (e: Exception) {
            Log.e("MusicManager", "Error resumeMusicaApp: ${e.message}")
        }
    }

    /** Pausa la música global */
    fun pauseMusicaApp() {
        try {
            if (musicaApp?.isPlaying == true) {
                musicaApp?.pause()
                Log.d("MusicManager", "Música de APP pausada.")
            }
        } catch (e: Exception) {
            Log.e("MusicManager", "Error pauseMusicaApp: ${e.message}")
        }
    }

    /** Detiene completamente la música global */
    fun stopMusicaApp() {
        try {
            musicaApp?.stop()
            musicaApp?.release()
            musicaApp = null
        } catch (e: Exception) {
            Log.e("MusicManager", "Error stopMusicaApp: ${e.message}")
        }
    }


    // -------------------------------------------------------------------------
    // 🎮 MÚSICA DEL JUEGO (OCR, RFID, etc.)
    // -------------------------------------------------------------------------

    /** Reproduce música del juego y pausa la de la APP */
    fun startMusicaJuego(context: Context) {

        pauseMusicaApp() // aseguramos que la música global no se mezcle

        // eliminar duplicados sin reanudar música app
        musicaJuego?.stop()
        musicaJuego?.release()
        musicaJuego = null

        musicaJuego = MediaPlayer().apply {
            try {
                setDataSource(MUSICA_FONDO_JUEGO_URL)
                isLooping = true

                setOnPreparedListener {
                    setVolume(1f, 1f)
                    it.start()
                    Log.d("MusicManager", "Música del juego iniciada.")
                }

                setOnErrorListener { mp, what, _ ->
                    Log.e("MusicManager", "Error música juego: $what")
                    mp.release()
                    musicaJuego = null
                    true
                }

                prepareAsync()

            } catch (e: IOException) {
                Log.e("MusicManager", "Error configurando música del juego: ${e.message}")
            } catch (e: Exception) {
                Log.e("MusicManager", "Error general música juego: ${e.message}")
            }
        }
    }

    /** Detiene la música del juego sin reanudar música de APP automáticamente */
    fun stopMusicaJuego() {
        try {
            musicaJuego?.stop()
            musicaJuego?.release()
            musicaJuego = null
            Log.d("MusicManager", "Música del juego detenida.")
        } catch (e: Exception) {
            Log.e("MusicManager", "Error stopMusicaJuego: ${e.message}")
        }
    }


    // -------------------------------------------------------------------------
    // 🧹 LIMPIEZA TOTAL
    // -------------------------------------------------------------------------

    fun releaseAllMusic() {
        stopMusicaApp()
        stopMusicaJuego()
        Log.d("MusicManager", "Todos los recursos de audio liberados.")
    }
}
