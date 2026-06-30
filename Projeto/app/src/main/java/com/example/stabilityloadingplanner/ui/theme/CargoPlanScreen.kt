package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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

    var showEditDialog    by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title   = { Text("Cargo Plan", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = {
                    // Botão de edição sempre visível quando há navio seleccionado
                    if (viewModel.hasVesselSelected) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit vessel dimensions",
                                // Laranja = dados estimados, azul = dados já corrigidos
                                tint = if (viewModel.activeVesselHasEstimates) Color(0xFFFF8F00) else IndustrialPrimary
                            )
                        }
                    }
                    AppMenuActions(navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar      = { ExactBottomNav(navController, "cargo_plan") },
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = IndustrialBackground
    ) { padding ->

        if (!viewModel.hasVesselSelected) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Icon(Icons.Outlined.DirectionsBoat, null, modifier = Modifier.size(64.dp), tint = OutlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Vessel Selected", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select or register a vessel in the Setup screen to begin planning cargo.",
                        style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.navigate("setup") },
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)) {
                        Text("Go to Setup")
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier            = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Banner de estimativas
                if (viewModel.activeVesselHasEstimates) {
                    Card(
                        colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        shape    = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier              = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                "⚠  Dimensions estimated — tap ✎ to correct.",
                                style    = MaterialTheme.typography.bodySmall,
                                color    = Color(0xFF5D4037),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Card do total
                Card(
                    colors   = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    shape    = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Vessel Load", fontWeight = FontWeight.Bold, color = TextPrimary)
                        if (vesselCapacity > 0) {
                            Text(
                                "${totalLoaded.toInt()} / $vesselCapacity t  ($loadPercent%)",
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress   = { (totalLoaded / vesselCapacity).toFloat().coerceIn(0f, 1f) },
                                modifier   = Modifier.fillMaxWidth().height(6.dp),
                                color      = when {
                                    loadPercent >= 100 -> Color(0xFFC62828)
                                    loadPercent >= 90  -> Color(0xFFF57C00)
                                    else               -> IndustrialPrimary
                                },
                                trackColor = OutlineVariant
                            )
                            if (totalLoaded > vesselCapacity) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("⚠ Overloaded! Cargo exceeds deadweight capacity.",
                                    color = Color(0xFFC62828), style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("${viewModel.activeVessel.name} — tap ✎ to set capacity",
                                style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }

            items(viewModel.tanks) { tank -> TankInputCard(tank = tank, viewModel = viewModel) }

            // CTA para Stability — aparece assim que há carga lançada.
            // Resolve a hesitação observada nos testes de usabilidade (User 1 e 2):
            // a app não navega sozinha para Stability, mas agora mostra claramente o próximo passo.
            if (totalLoaded > 0) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick  = { navController.navigate("stability") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(8.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                    ) {
                        Text("View Stability Analysis", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Diálogo de edição pré-preenchido com os valores actuais
    if (showEditDialog) {
        val vessel = viewModel.activeVessel

        var dwtInput   by remember(vessel.imo) {
            mutableStateOf(if (vessel.deadweight > 0) vessel.deadweight.toInt().toString() else "")
        }
        var loaInput   by remember(vessel.imo) {
            mutableStateOf(if (vessel.loa > 0)  "%.1f".format(vessel.loa)  else "")
        }
        var beamInput  by remember(vessel.imo) {
            mutableStateOf(if (vessel.beam > 0) "%.1f".format(vessel.beam) else "")
        }
        var holdsInput by remember(vessel.imo) {
            mutableStateOf(if (vessel.numberOfHolds > 0) vessel.numberOfHolds.toString() else "")
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update Vessel Dimensions", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
            text  = {
                Column {
                    Text(
                        "Pre-filled with current values. Change only what you know is incorrect.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value           = dwtInput,
                        onValueChange   = { dwtInput = it },
                        label           = { Text("Deadweight (t)") },
                        placeholder     = { Text("e.g. 2953") },
                        modifier        = Modifier.fillMaxWidth(),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors          = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value           = loaInput,
                            onValueChange   = { loaInput = it },
                            label           = { Text("LOA (m)") },
                            placeholder     = { Text("e.g. 89.0") },
                            modifier        = Modifier.weight(1f),
                            singleLine      = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors          = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                        )
                        OutlinedTextField(
                            value           = beamInput,
                            onValueChange   = { beamInput = it },
                            label           = { Text("Beam (m)") },
                            placeholder     = { Text("e.g. 12.0") },
                            modifier        = Modifier.weight(1f),
                            singleLine      = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors          = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value           = holdsInput,
                        onValueChange   = { holdsInput = it },
                        label           = { Text("Number of Holds") },
                        placeholder     = { Text("e.g. 4") },
                        modifier        = Modifier.fillMaxWidth(),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors          = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Corrected data is saved to the shared database for all users.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dwt   = dwtInput.toDoubleOrNull()  ?: 0.0
                        val holds = holdsInput.toIntOrNull()   ?: 1
                        val loa   = loaInput.toDoubleOrNull()
                        val beam  = beamInput.toDoubleOrNull()
                        if (dwt > 0) {
                            viewModel.updateActiveVesselDimensions(dwt, holds, loa, beam)
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
    val progress = if (tank.maxCapacity > 0) (tank.weightFloat / tank.maxCapacity).coerceIn(0f, 1f) else 0f
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
                    Box {
                        TextButton(
                            onClick        = { expanded = true },
                            contentPadding = PaddingValues(0.dp),
                            modifier       = Modifier.height(24.dp)
                        ) {
                            Text("${tank.selectedCargo.name} ▼", fontSize = 12.sp, color = IndustrialPrimary)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            CargoOptions.forEach { cargo ->
                                DropdownMenuItem(
                                    text    = { Text("${cargo.name}  (VCG ×${"%.2f".format(cargo.vcgFactor)})") },
                                    onClick = { viewModel.updateTankCargo(tank.name, cargo); expanded = false }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value           = tank.currentWeight,
                    onValueChange   = { viewModel.updateTankWeight(tank.name, it) },
                    label           = { Text("Load (t)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier        = Modifier.width(110.dp),
                    singleLine      = true,
                    isError         = tank.isOverloaded,
                    colors          = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = IndustrialPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        errorBorderColor     = Color(0xFFC62828)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress   = { progress },
                modifier   = Modifier.fillMaxWidth().height(4.dp),
                color      = when {
                    progress >= 1f   -> Color(0xFFC62828)
                    progress >= 0.9f -> Color(0xFFF57C00)
                    else             -> IndustrialPrimary
                },
                trackColor = Color.LightGray
            )
            if (progress >= 0.9f && tank.maxCapacity > 0) {
                Text(
                    "${(progress * 100).toInt()}% of capacity",
                    style    = MaterialTheme.typography.bodySmall,
                    color    = if (progress >= 1f) Color(0xFFC62828) else Color(0xFFF57C00),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}