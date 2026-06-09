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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargoPlanScreen(navController: NavController, viewModel: VesselViewModel) {
    val totalLoaded = viewModel.tanks.sumOf { it.weightFloat.toDouble() }

    // Ler a capacidade máxima do navio (deadweight) a partir do modelo atualizado
    val vesselCapacity = if (viewModel.activeVessel.name.isEmpty()) 0 else viewModel.activeVessel.deadweight.toInt()

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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Cartão do Total
                Card(
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Vessel Load", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "${totalLoaded.toInt()} / $vesselCapacity tons",
                            fontSize = 24.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Gerar os cartões de tanques dinamicamente com base na lista do ViewModel
            items(viewModel.tanks) { tank ->
                TankInputCard(tank = tank, viewModel = viewModel)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TankInputCard(tank: Tank, viewModel: VesselViewModel) {
    val progress = if (tank.maxCapacity > 0) {
        (tank.weightFloat / tank.maxCapacity).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Controla se o menu das cargas está aberto ou fechado
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(tank.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text("Capacity: ${tank.capacity}", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))

                    // NOVO: Texto clicável que abre o menu Dropdown
                    Box {
                        TextButton(
                            onClick = { expanded = true },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp) // Mantém compacto
                        ) {
                            Text("Cargo: ${tank.selectedCargo.name} ▼", fontSize = 12.sp, color = IndustrialPrimary)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            CargoOptions.forEach { cargo ->
                                DropdownMenuItem(
                                    text = { Text(cargo.name) },
                                    onClick = {
                                        viewModel.updateTankCargo(tank.name, cargo)
                                        expanded = false // Fecha o menu depois de escolher
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = tank.currentWeight,
                    onValueChange = { newValue ->
                        viewModel.updateTankWeight(tank.name, newValue)
                    },
                    label = { Text("Load (t)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(120.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndustrialPrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = IndustrialPrimary,
                trackColor = Color.LightGray
            )
        }
    }
}