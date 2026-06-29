package com.example.stabilityloadingplanner

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stabilityloadingplanner.ui.theme.AppNavigation
import com.example.stabilityloadingplanner.ui.theme.StabilityLoadingPlannerTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang  = prefs.getString("lang", "auto") ?: "auto"

        val context = if (lang == "auto") {
            // Segue a língua do sistema
            newBase
        } else {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StabilityLoadingPlannerTheme {
                AppNavigation()
            }
        }
    }
}