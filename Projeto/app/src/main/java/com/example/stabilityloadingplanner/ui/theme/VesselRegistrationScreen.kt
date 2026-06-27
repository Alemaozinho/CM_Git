package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stabilityloadingplanner.data.models.Vessel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VesselRegistrationScreen(navController: NavController, viewModel: VesselViewModel) {
    var name by remember { mutableStateOf("") }
    var imo by remember { mutableStateOf("") }
    var loa by remember { mutableStateOf("") }
    var beam by remember { mutableStateOf("") }
    var displacement by remember { mutableStateOf("") }
    var lightshipWeight by remember { mutableStateOf("") }
    var lightshipKG by remember { mutableStateOf("") }
    var deadweight by remember { mutableStateOf("") }
    // NOVO: Variável para guardar o número de Holds
    var numberOfHolds by remember { mutableStateOf("") }

    val isFormValid = name.isNotBlank() && imo.isNotBlank() && deadweight.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Vessel", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "setup") },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Vessel Particulars", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = imo,
                        onValueChange = { imo = it },
                        label = { Text("IMO Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Vessel Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = loa, onValueChange = { loa = it }, label = { Text("LOA (m)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))
                        OutlinedTextField(value = beam, onValueChange = { beam = it }, label = { Text("Beam (m)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = lightshipWeight, onValueChange = { lightshipWeight = it }, label = { Text("Lightship (t)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))
                        OutlinedTextField(value = lightshipKG, onValueChange = { lightshipKG = it }, label = { Text("Lightship KG (m)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // NOVO: Linha com Deadweight e Number of Holds lado a lado
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = deadweight, onValueChange = { deadweight = it }, label = { Text("Deadweight (t)") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))
                        OutlinedTextField(value = numberOfHolds, onValueChange = { numberOfHolds = it }, label = { Text("Nº of Holds") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = displacement, onValueChange = { displacement = it }, label = { Text("Max Displacement (t)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary))

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val vessel = Vessel(
                                imo = imo,
                                name = name,
                                loa = loa.toDoubleOrNull() ?: 0.0,
                                beam = beam.toDoubleOrNull() ?: 0.0,
                                displacement = displacement.toDoubleOrNull() ?: 0.0,
                                lightshipWeight = lightshipWeight.toDoubleOrNull() ?: 0.0,
                                lightshipKG = lightshipKG.toDoubleOrNull() ?: 0.0,
                                deadweight = deadweight.toDoubleOrNull() ?: 0.0,
                                numberOfHolds = numberOfHolds.toIntOrNull() ?: 3 // Envia o número de holds (se estiver vazio assume 3 por defeito)
                            )
                            viewModel.selectVessel(vessel)
                            navController.navigate("cargo_plan")
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                    ) { Text("Save & Proceed", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}