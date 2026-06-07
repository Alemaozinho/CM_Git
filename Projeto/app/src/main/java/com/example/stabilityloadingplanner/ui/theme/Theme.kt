package com.example.stabilityloadingplanner.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MaritimeIndustrialScheme = lightColorScheme(
    primary = IndustrialPrimary,
    background = IndustrialBackground,
    surface = IndustrialSurface,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = OutlineVariant,
    secondaryContainer = ActiveOrange,
    onSecondaryContainer = OnActiveOrange
)

@Composable
fun StabilityLoadingPlannerTheme(content: @Composable () -> Unit) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Barra de estado no topo do telemóvel a bater certo com o fundo claro
            window.statusBarColor = IndustrialSurface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = MaritimeIndustrialScheme,
        typography = Typography,
        content = content
    )
}