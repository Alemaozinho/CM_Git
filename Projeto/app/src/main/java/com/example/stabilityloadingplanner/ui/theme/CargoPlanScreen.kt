package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stabilityloadingplanner.data.models.CargoOptions
import com.example.stabilityloadingplanner.data.models.Tank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargoPlanScreen(navController: NavController, viewModel: VesselViewModel) {
    val totalLoaded    = viewModel.tanks.sumOf { it.weightFloat.toDouble() }
    val vesselCapacity = viewModel.activeVessel.deadweight.toInt()
    val loadPercent    = if (vesselCapacity > 0) (totalLoaded / vesselCapacity * 100).toInt() else 0

    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cargo Plan", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface),
                actions = {
                    if (viewModel.activeVesselHasEstimates) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit dimensions", tint = IndustrialPrimary)
                        }
                    }
                    AppMenuActions(navController)
                }
            )
        },
        bottomBar      = { ExactBottomNav(navController, "cargo_plan") },
        containerColor = IndustrialBackground
    ) { padding ->

        if (!viewModel.hasVesselSelected) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Icon(Icons.Outlined.DirectionsBoat, contentDescription = null, modifier = Modifier.size(64.dp), tint = OutlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Vessel Selected", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select or register a vessel in the Setup screen to begin planning cargo.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.navigate("setup") }, colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)) {
                        Text("Go to Setup")
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier            = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Banner de aviso quando as dimensões foram estimadas
                if (viewModel.activeVesselHasEstimates) {
                    Card(
                        colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        shape    = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Dimensions estimated from vessel type. Tap ✎ to update.",
                            style    = MaterialTheme.typography.bodySmall,
                            color    = Color(0xFF5D4037),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Cartão do Total
                Card(
                    colors   = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    shape    = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Vessel Load", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            if (vesselCapacity > 0) "${totalLoaded.toInt()} / $vesselCapacity t  ($loadPercent%)"
                            else "${viewModel.activeVessel.name}  —  tap ✎ to set capacity",
                            fontSize   = if (vesselCapacity > 0) 22.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color      = if (vesselCapacity > 0) TextPrimary else TextSecondary
                        )
                        if (vesselCapacity > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            // Caso o numberOfHolds seja 0 ou inválido, evitamos divisão por zero
                            LinearProgressIndicator(
                                progress   = { (totalLoaded / vesselCapacity).toFloat().coerceIn(0f, 1f) },
                                modifier   = Modifier.fillMaxWidth().height(6.dp),
                                color      = if (loadPercent > 95) Color(0xFFC62828) else IndustrialPrimary,
                                trackColor = OutlineVariant
                            )
                            if (totalLoaded > vesselCapacity) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("⚠ Overloaded! Cargo exceeds deadweight capacity.", color = Color(0xFFC62828), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Gerar os cartões de tanques dinamicamente com base na lista do ViewModel
            items(viewModel.tanks) { tank ->
                TankInputCard(tank = tank, viewModel = viewModel)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Diálogo de edição rápida de dimensões
    if (showEditDialog) {
        var dwtInput   by remember { mutableStateOf(viewModel.activeVessel.deadweight.toInt().toString()) }
        var holdsInput by remember { mutableStateOf(viewModel.activeVessel.numberOfHolds.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update Vessel Dimensions", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
            text = {
                Column {
                    Text(
                        "These values were estimated from vessel type. Enter the correct values if known.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value           = dwtInput,
                        onValueChange   = { dwtInput = it },
                        label           = { Text("Deadweight (t)") },
                        modifier        = Modifier.fillMaxWidth(),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value           = holdsInput,
                        onValueChange   = { holdsInput = it },
                        label           = { Text("Number of Holds") },
                        modifier        = Modifier.fillMaxWidth(),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dwt   = dwtInput.toDoubleOrNull()  ?: 0.0
                        val holds = holdsInput.toIntOrNull() ?: 4
                        if (dwt > 0) {
                            viewModel.updateActiveVesselDimensions(dwt, holds)
                            showEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                ) { Text("Apply", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = IndustrialPrimary)
                }
            },
            containerColor = IndustrialSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TankInputCard(tank: Tank, viewModel: VesselViewModel) {
    val progress = if (tank.maxCapacity > 0) {
        (tank.weightFloat / tank.maxCapacity).coerceIn(0f, 1f)
    } else 0f

    // Controla se o menu das cargas está aberto ou fechado
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors   = CardDefaults.cardColors(containerColor = IndustrialSurface),
        shape    = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(tank.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text("Capacity: ${tank.capacity}", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Texto clicável que abre o menu Dropdown
                    Box {
                        TextButton(
                            onClick        = { expanded = true },
                            contentPadding = PaddingValues(0.dp),
                            modifier       = Modifier.height(24.dp)
                        ) {
                            Text("Cargo: ${tank.selectedCargo.name} ▼", fontSize = 12.sp, color = IndustrialPrimary)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            CargoOptions.forEach { cargo ->
                                DropdownMenuItem(
                                    text    = { Text("${cargo.name}  (VCG ×${cargo.vcgFactor})") },
                                    onClick = {
                                        viewModel.updateTankCargo(tank.name, cargo)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value         = tank.currentWeight,
                    onValueChange = { newValue -> viewModel.updateTankWeight(tank.name, newValue) },
                    label         = { Text("Load (t)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier      = Modifier.width(120.dp),
                    singleLine    = true,
                    isError       = tank.isOverloaded,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = IndustrialPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        errorBorderColor     = Color(0xFFC62828)
                    )
                )
            }

            if (tank.isOverloaded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Exceeds hold capacity", color = Color(0xFFC62828), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress   = { progress },
                modifier   = Modifier.fillMaxWidth().height(4.dp),
                color      = if (tank.isOverloaded) Color(0xFFC62828) else IndustrialPrimary,
                trackColor = Color.LightGray
            )
        }
    }
}