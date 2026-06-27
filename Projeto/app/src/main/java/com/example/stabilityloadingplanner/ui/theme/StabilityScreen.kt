package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StabilityScreen(navController: NavController, viewModel: VesselViewModel) {
    val gm = viewModel.currentGM
    val kg = viewModel.currentKG

    val isStable   = gm >= 0.15
    val gmMinimum  = 0.15

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stability", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = { AppMenuActions(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar      = { ExactBottomNav(navController, "stability") },
        containerColor = IndustrialBackground
    ) { padding ->

        if (!viewModel.hasVesselSelected) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Icon(Icons.Outlined.AccountBalance, contentDescription = null, modifier = Modifier.size(64.dp), tint = OutlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Vessel Selected", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select a vessel and fill in the cargo plan to calculate stability.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.navigate("setup") }, colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)) {
                        Text("Go to Setup")
                    }
                }
            }
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {

            // Vessel info card
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Vessel", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Text(viewModel.activeVessel.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("IMO: ${viewModel.activeVessel.imo}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Métricas card
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Stability Metrics", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    StabilityMetric("KG — Centre of Gravity",   "${"%.3f".format(kg)} m")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    StabilityMetric("GM — Metacentric Height",  "${"%.3f".format(gm)} m")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    StabilityMetric("Min. Safe GM",             "${"%.3f".format(gmMinimum)} m")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    StabilityMetric("Total Cargo",
                        "${viewModel.tanks.sumOf { it.weightFloat.toDouble() }.toInt()} t")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    StabilityMetric("Deadweight Capacity",      "${viewModel.activeVessel.deadweight.toInt()} t")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resultado com AnimatedVisibility — entra com fade + escala
            AnimatedVisibility(
                visible = viewModel.hasVesselSelected,
                enter   = fadeIn() + scaleIn(initialScale = 0.85f),
                exit    = fadeOut() + scaleOut(targetScale = 0.85f)
            ) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(
                        containerColor = if (isStable) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                    ),
                    shape     = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier              = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment   = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text      = if (isStable) "✔  STABLE" else "✘  UNSTABLE",
                            fontSize  = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color     = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = if (isStable) "Vessel is safe to proceed." else "Adjust cargo distribution before departure.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = "GM = ${"%.3f".format(gm)} m  (min. ${"%.3f".format(gmMinimum)} m)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}