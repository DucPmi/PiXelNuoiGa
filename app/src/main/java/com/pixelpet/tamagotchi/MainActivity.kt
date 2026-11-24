package com.pixelpet.tamagotchi

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelpet.tamagotchi.audio.ChiptuneAudioEngine
import com.pixelpet.tamagotchi.ui.PetViewModel
import com.pixelpet.tamagotchi.ui.TamagotchiRootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            val vm: PetViewModel = viewModel()
            MaterialTheme {
                TamagotchiRootScreen(viewModel = vm)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ChiptuneAudioEngine.startBgm()
    }

    override fun onPause() {
        super.onPause()
        ChiptuneAudioEngine.stopBgm()
    }
}
