package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargoPlanScreen(navController: NavController, viewModel: VesselViewModel) {
    val totalLoaded = viewModel.tanks.sumOf { it.weightFloat.toDouble() }
    val deadweightLimit = viewModel.activeVessel.deadweight

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cargo Plan", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "cargo_plan") },
        containerColor = IndustrialBackground
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Vessel Load", fontWeight = FontWeight.Bold, color = TextSecondary)
                        Text("${totalLoaded.toInt()} / ${deadweightLimit.toInt()} tons", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                    }
                }
            }

            items(viewModel.tanks) { tank ->
                val progress = if (tank.maxCapacity > 0) (tank.weightFloat / tank.maxCapacity).coerceIn(0f, 1f) else 0f
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tank.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Capacity: ${tank.capacity}", fontSize = 12.sp, color = TextSecondary)

                                // O seletor do tipo de Carga
                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { expanded = true }, contentPadding = PaddingValues(0.dp)) {
                                        Text("Cargo: ${tank.selectedCargo.name}", fontSize = 12.sp, color = IndustrialPrimary)
                                    }
                                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                        CargoOptions.forEach { cargo ->
                                            DropdownMenuItem(
                                                text = { Text(cargo.name) },
                                                onClick = { viewModel.updateTankCargo(tank.name, cargo); expanded = false }
                                            )
                                        }
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = tank.currentWeight,
                                onValueChange = { viewModel.updateTankWeight(tank.name, it) },
                                modifier = Modifier.width(110.dp),
                                label = { Text("Load (t)") },
                                singleLine = true,
                                isError = tank.isOverloaded,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = if (tank.isOverloaded) MaterialTheme.colorScheme.error else IndustrialPrimary)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = if (tank.isOverloaded) MaterialTheme.colorScheme.error else IndustrialPrimary,
                            trackColor = OutlineVariant
                        )
                    }
                }
            }
        }
    }
}