package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = IndustrialPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Ícone da app
            Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = IndustrialPrimary) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsBoat, contentDescription = null, modifier = Modifier.size(56.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Stability Planner", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = IndustrialPrimary)
            Text("Maritime Cargo Management", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            Spacer(modifier = Modifier.height(32.dp))

            // Card do autor
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = IndustrialPrimary.copy(alpha = 0.12f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = IndustrialPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Lucas Alemão", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                    Text("Nº 15052", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card académico
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.School, contentDescription = null, tint = IndustrialPrimary, modifier = Modifier.size(20.dp))
                        Text("Academic Context", fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AboutInfoRow("Institution",   "ENIDH")
                    AboutInfoRow("Course",        "Engenharia Informática e de Computadores")
                    AboutInfoRow("Subject",       "Computação Móvel")
                    AboutInfoRow("Project",       "Final de Cadeira")
                    AboutInfoRow("Academic Year", "2025 / 2026")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card de APIs
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Data Sources", fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    AboutInfoRow("Vessel Data",    "VesselAPI")
                    AboutInfoRow("Ports",          "World Port Index — NGA")
                    AboutInfoRow("Marine Weather", "Open-Meteo Marine")
                    AboutInfoRow("Vessel Photos",  "Wikimedia Commons")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Built with Jetpack Compose · Kotlin",
                style     = MaterialTheme.typography.bodySmall,
                color     = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AboutInfoRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.weight(0.45f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.55f))
    }
}