package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarineConditionsScreen(
    navController: NavController,
    viewModel: VesselViewModel
) {
    val marineData    = viewModel.marineData
    val isLoading     = viewModel.isLoadingMarine
    val marineError   = viewModel.marineError
    val isSafetyRisk  = viewModel.isSafetyRisk
    val currentVoyage = viewModel.currentVoyage
    var showMenu      by remember { mutableStateOf(false) }

    val waveHeight = marineData?.wave_height?.firstOrNull()
    val wavePeriod = marineData?.wave_period?.firstOrNull()
    val seaTemp    = marineData?.sea_surface_temperature?.firstOrNull()

    LaunchedEffect(Unit) {
        if (marineData == null) viewModel.fetchMarineData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Marine Conditions", fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                },
                actions = { AppMenuActions(navController) },
                colors  = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar      = { ExactBottomNav(navController, "marine") },
        containerColor = IndustrialBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Coordenadas
            Text(
                text  = "Coordinates: ${String.format("%.2f", viewModel.latitude)} / ${String.format("%.2f", viewModel.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            // Aviso de segurança
            if (isSafetyRisk) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape  = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier              = Modifier.padding(16.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828))
                        Text(
                            text  = "⚠ Safety risk — adverse sea conditions detected.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }

            // A carregar
            if (isLoading) {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(color = IndustrialPrimary)
                        Text("Loading marine data...", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            // Erro
            if (marineError != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Text(
                        text     = "Error loading marine data.",
                        modifier = Modifier.padding(16.dp),
                        color    = Color(0xFFC62828)
                    )
                }
            }

            // Dados marítimos
            if (marineData != null) {
                @Composable
                fun MarineRow(label: String, value: String) {
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    HorizontalDivider(color = OutlineVariant)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    shape    = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        MarineRow(
                            label = "Wave Height",
                            value = if (waveHeight != null) "${String.format("%.2f", waveHeight)} m" else "—"
                        )
                        MarineRow(
                            label = "Wave Period",
                            value = if (wavePeriod != null) "${String.format("%.2f", wavePeriod)} s" else "—"
                        )
                        MarineRow(
                            label = "Sea Temperature",
                            value = if (seaTemp != null) "${String.format("%.1f", seaTemp)} °C" else "—"
                        )
                    }
                }
            }

            // Viagem
            val departure = currentVoyage.departurePort.ifBlank { "Not Set" }
            val arrival   = currentVoyage.arrivalPort.ifBlank { "Not Set" }
            Text(
                text  = "Voyage: $departure → $arrival",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botões
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick  = { navController.navigate("voyage_settings") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Voyage Plan")
                }
                Button(
                    onClick  = { viewModel.fetchMarineData() },
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Refresh")
                }
            }
        }
    }
}