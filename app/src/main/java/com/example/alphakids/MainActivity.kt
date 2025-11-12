package com.example.alphakids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.alphakids.navigation.AppNavHost
import com.example.alphakids.ui.theme.AlphakidsTheme
import dagger.hilt.android.AndroidEntryPoint

// 🛑 ELIMINAMOS las importaciones de utilidades antiguas:
// import com.example.alphakids.ui.utils.MUSICA_FONDO_APP_URL
// import com.example.alphakids.ui.utils.playBackgroundMusic
// import com.example.alphakids.ui.utils.stopBackgroundMusic

// ✅ Importamos el nuevo Singleton MusicManager:
import com.example.alphakids.ui.utils.MusicManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlphakidsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * 🔊 Inicia la música de fondo de la APP cuando la actividad se vuelve visible.
     * Usamos startMusicaApp() que maneja el loop y la lógica interna.
     */
    override fun onStart() {
        super.onStart()
        // ✅ USAMOS MusicManager.startMusicaApp()
        MusicManager.startMusicaApp(this)
    }

    /**
     * 🛑 Detiene la música cuando la app pasa a segundo plano (minimizar, bloquear pantalla).
     * Esto resuelve el problema de que el audio sigue sonando.
     */
    override fun onStop() {
        super.onStop()
        // ✅ USAMOS MusicManager.pauseMusicaApp() o releaseAllMusic() si no se espera reanudar rápido.
        // Usaremos releaseAllMusic para asegurar la limpieza total.
        MusicManager.releaseAllMusic()
    }

    /**
     * 💀 Libera los recursos cuando la actividad se destruye completamente.
     * releaseAllMusic() se encarga de esto también, pero lo llamamos de nuevo por seguridad.
     */
    override fun onDestroy() {
        super.onDestroy()
        // ✅ USAMOS MusicManager.releaseAllMusic()
        MusicManager.releaseAllMusic()
    }
}