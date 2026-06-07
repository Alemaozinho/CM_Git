package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: VesselViewModel = viewModel()

    NavHost(navController = navController, startDestination = "setup") {
        composable("setup") { VesselSetupScreen(navController, viewModel) }
        composable("cargo_plan") { CargoPlanScreen(navController, viewModel) }
        composable("stability") { StabilityScreen(navController, viewModel) }
        composable("marine") { MarineConditionsScreen(navController, viewModel) }
        composable("reports") { ReportsScreen(navController, viewModel) }
        composable("vessel_registration") { VesselRegistrationScreen(navController, viewModel) }
        composable("voyage_settings") { VoyageSettingsScreen(navController, viewModel) }
    }
}