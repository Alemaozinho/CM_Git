package com.example.stabilityloadingplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// Importa o teu tema e a navegação
import com.example.stabilityloadingplanner.ui.theme.StabilityLoadingPlannerTheme
import com.example.stabilityloadingplanner.ui.theme.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aplica o tema que define as cores da tua app
            StabilityLoadingPlannerTheme {
                // Surface garante um fundo consistente
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Chama a tua navegação aqui
                    AppNavigation()
                }
            }
        }
    }
}