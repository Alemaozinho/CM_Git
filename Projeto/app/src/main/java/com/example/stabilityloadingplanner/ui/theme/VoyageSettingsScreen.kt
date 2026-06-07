package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// A lista de portos pré-definidos restaurada
val PredefinedPorts = listOf(
    "Port of Lisbon, PT",
    "Port of Sines, PT",
    "Port of Leixões, PT",
    "Port of Rotterdam, NL",
    "Port of Antwerp, BE",
    "Port of Hamburg, DE",
    "Port of Algeciras, ES"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoyageSettingsScreen(navController: NavController, viewModel: VesselViewModel) {
    var dep by remember { mutableStateOf(viewModel.currentVoyage.departurePort) }
    var arr by remember { mutableStateOf(viewModel.currentVoyage.arrivalPort) }

    // Controlos para abrir/fechar os menus dropdown
    var expandedDep by remember { mutableStateOf(false) }
    var expandedArr by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voyage Settings", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "marine") },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Voyage Plan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Set your departure and arrival ports.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown do Porto de Partida
                    ExposedDropdownMenuBox(
                        expanded = expandedDep,
                        onExpandedChange = { expandedDep = !expandedDep }
                    ) {
                        OutlinedTextField(
                            value = dep,
                            onValueChange = { dep = it },
                            label = { Text("Departure Port") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDep) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDep,
                            onDismissRequest = { expandedDep = false }
                        ) {
                            PredefinedPorts.forEach { port ->
                                DropdownMenuItem(
                                    text = { Text(port) },
                                    onClick = {
                                        dep = port
                                        expandedDep = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown do Porto de Chegada
                    ExposedDropdownMenuBox(
                        expanded = expandedArr,
                        onExpandedChange = { expandedArr = !expandedArr }
                    ) {
                        OutlinedTextField(
                            value = arr,
                            onValueChange = { arr = it },
                            label = { Text("Arrival Port") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArr) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedArr,
                            onDismissRequest = { expandedArr = false }
                        ) {
                            PredefinedPorts.forEach { port ->
                                DropdownMenuItem(
                                    text = { Text(port) },
                                    onClick = {
                                        arr = port
                                        expandedArr = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.updateVoyage(dep, arr, 10)
                            navController.navigate("marine")
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                    ) {
                        Text("Confirm Voyage Plan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}