package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StabilityScreen(navController: NavController, viewModel: VesselViewModel) {
    val gmValue = viewModel.currentGM
    val kgValue = viewModel.currentKG
    val isStable = gmValue > 0.3 // Margem de segurança de estabilidade

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stability Analysis", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "stability") },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isStable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Vessel Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isStable) "STABLE (GO)" else "UNSTABLE (NO-GO)",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isStable) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard("GM", "%.2f m".format(gmValue), Modifier.weight(1f))
                MetricCard("KG", "%.2f m".format(kgValue), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = IndustrialSurface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}