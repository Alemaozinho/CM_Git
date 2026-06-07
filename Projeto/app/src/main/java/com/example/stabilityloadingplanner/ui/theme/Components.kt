package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StabilityMetric(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ExactBottomNav(navController: NavController, currentRoute: String) {
    NavigationBar {
        val items = listOf(
            Triple("setup", "Setup", Icons.Default.DirectionsBoat),
            Triple("cargo_plan", "Loading", Icons.Outlined.Inventory2),
            Triple("stability", "Stability", Icons.Outlined.AccountBalance),
            Triple("marine", "Marine", Icons.Outlined.Waves),
            Triple("reports", "Reports", Icons.Outlined.Assessment)
        )
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = { navController.navigate(route) }
            )
        }
    }
}