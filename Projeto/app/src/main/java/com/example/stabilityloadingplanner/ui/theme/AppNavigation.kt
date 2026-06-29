package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController  = rememberNavController()
    val viewModel: VesselViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn = authViewModel.isLoggedIn

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("setup") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login")               { LoginScreen(navController, authViewModel) }
        composable("register")            { RegisterScreen(navController, authViewModel) }
        composable("setup")               { VesselSetupScreen(navController, viewModel) }
        composable("vessel_registration") { VesselRegistrationScreen(navController, viewModel) }
        composable("cargo_plan")          { CargoPlanScreen(navController, viewModel) }
        composable("stability")           { StabilityScreen(navController, viewModel) }
        composable("marine")              { MarineConditionsScreen(navController, viewModel) }
        composable("reports")             { ReportsScreen(navController, viewModel, authViewModel) }
        composable("voyage_settings")     { VoyageSettingsScreen(navController, viewModel) }
        composable("profile")             { ProfileScreen(navController, authViewModel) }
        composable("about")               { AboutScreen(navController) }
        composable("help")                { HelpScreen(navController) }
    }
}