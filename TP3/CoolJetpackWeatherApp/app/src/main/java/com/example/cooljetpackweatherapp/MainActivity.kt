package com.example.cooljetpackweatherapp

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cooljetpackweatherapp.ui.WeatherUI
import com.example.cooljetpackweatherapp.ui.theme.CoolJetpackWeatherAppTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tentei configurar o OSMDroid para o mapa aqui (Exercício 4),
        // mas deixei em comentário para não causar erros de inicialização
        // enquanto o emulador tiver problemas de ligação.
        /*
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = packageName
        */

        enableEdgeToEdge()
        setContent {
            CoolJetpackWeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherUI()
                }
            }
        }
    }
}