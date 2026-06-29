package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

// ── Destinos da Bottom Nav ────────────────────────────────────────────────────
sealed class BottomNavDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Setup     : BottomNavDestination("setup",        Icons.Default.Search,   "Setup")
    object Loading   : BottomNavDestination("cargo_plan",   Icons.Default.List,     "Loading")
    object Stability : BottomNavDestination("stability",    Icons.Default.Star,     "Stability")
    object Marine    : BottomNavDestination("marine",       Icons.Default.Place,    "Marine")
    object Reports   : BottomNavDestination("reports",      Icons.Default.DateRange,"Reports")
    object Profile   : BottomNavDestination("profile",      Icons.Default.Person,   "Profile")
}

val bottomNavItems = listOf(
    BottomNavDestination.Setup,
    BottomNavDestination.Loading,
    BottomNavDestination.Stability,
    BottomNavDestination.Marine,
    BottomNavDestination.Reports,
    BottomNavDestination.Profile
)

// ── Componente reutilizável — passa a route actual e a função de navegação ────
@Composable
fun AppBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { dest ->
            NavigationBarItem(
                icon     = { Icon(dest.icon, contentDescription = dest.label) },
                label    = { Text(dest.label) },
                selected = currentRoute == dest.route,
                onClick  = { if (currentRoute != dest.route) onNavigate(dest.route) }
            )
        }
    }
}
