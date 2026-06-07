package com.example.stabilityloadingplanner.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarineConditionsScreen(navController: NavController, viewModel: VesselViewModel) {
    LaunchedEffect(Unit) { viewModel.fetchMarineData() }

    var showLocationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current // Necessário para os Toasts

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marine Environment", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "marine") },
        containerColor = IndustrialBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            val m = viewModel.marineData
            if (m != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        Text(
                            text = "Coordinates: ${viewModel.latitude} / ${viewModel.longitude}",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        StabilityMetric("Wave Height", "${m.wave_height?.firstOrNull() ?: 0} m")
                        StabilityMetric("Wave Period", "${m.wave_period?.firstOrNull() ?: 0} s")
                        StabilityMetric("Current Velocity", "${m.ocean_current_velocity?.firstOrNull() ?: 0} m/s")
                        StabilityMetric("Sea Temp", "${m.sea_surface_temperature?.firstOrNull() ?: 0} °C")
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = IndustrialPrimary)
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("voyage_settings") },
                    containerColor = IndustrialSurface,
                    contentColor = IndustrialPrimary,
                    text = { Text("Voyage Plan") },
                    icon = { Icon(Icons.Default.Map, "Voyage Plan") }
                )

                ExtendedFloatingActionButton(
                    onClick = { showLocationDialog = true },
                    containerColor = IndustrialPrimary,
                    contentColor = Color.White,
                    text = { Text("Change Location") },
                    icon = { Icon(Icons.Default.LocationOn, "Location") }
                )
            }
        }

        if (showLocationDialog) {
            var inputLat by remember { mutableStateOf(viewModel.latitude.toString()) }
            var inputLon by remember { mutableStateOf(viewModel.longitude.toString()) }

            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Update Coordinates", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputLat,
                            onValueChange = { inputLat = it },
                            label = { Text("Latitude") },
                            // Mudei para Text normal para evitar que o teclado bloqueie o sinal de menos
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputLon,
                            onValueChange = { inputLon = it },
                            label = { Text("Longitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Limpeza extrema: Substitui vírgulas por pontos, e limpa TODOS os tipos de hifens/traços manhosos
                            val safeLatStr = inputLat.replace(",", ".").replace("−", "-").replace("–", "-").replace(" ", "").trim()
                            val safeLonStr = inputLon.replace(",", ".").replace("−", "-").replace("–", "-").replace(" ", "").trim()

                            val newLat = safeLatStr.toDoubleOrNull()
                            val newLon = safeLonStr.toDoubleOrNull()

                            if (newLat != null && newLon != null) {
                                viewModel.updateCoordinates(newLat, newLon)
                                showLocationDialog = false
                                Toast.makeText(context, "A atualizar meteorologia...", Toast.LENGTH_SHORT).show()
                            } else {
                                // Se o Kotlin não conseguir converter mesmo assim, avisa-te no ecrã!
                                Toast.makeText(context, "Erro: Formato inválido. Confirma os sinais e pontos.", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                    ) {
                        Text("Update Weather", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLocationDialog = false }) {
                        Text("Cancel", color = IndustrialPrimary)
                    }
                },
                containerColor = IndustrialSurface
            )
        }
    }
}