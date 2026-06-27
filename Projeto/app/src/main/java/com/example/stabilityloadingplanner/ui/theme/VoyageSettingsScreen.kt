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
import com.example.stabilityloadingplanner.data.models.PortResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoyageSettingsScreen(navController: NavController, viewModel: VesselViewModel) {
    var depQuery by remember { mutableStateOf(viewModel.currentVoyage.departurePort) }
    var arrQuery by remember { mutableStateOf(viewModel.currentVoyage.arrivalPort) }
    var depCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    var depResults by remember { mutableStateOf<List<PortResult>>(emptyList()) }
    var arrResults by remember { mutableStateOf<List<PortResult>>(emptyList()) }
    var depLoading by remember { mutableStateOf(false) }
    var arrLoading by remember { mutableStateOf(false) }
    var availableCountries by remember { mutableStateOf<List<String>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    // Carrega a lista de países ao abrir o ecrã
    LaunchedEffect(Unit) {
        availableCountries = viewModel.getAvailableCountriesAsync()
    }

    LaunchedEffect(depQuery) {
        if (depQuery.length >= 3) {
            depLoading = true
            delay(400) // Debounce de 400ms — evita disparar um pedido a cada letra escrita
            depResults = viewModel.searchPortsAsync(depQuery)
            depLoading = false
        } else {
            depResults = emptyList()
        }
    }

    LaunchedEffect(arrQuery) {
        if (arrQuery.length >= 3) {
            arrLoading = true
            delay(400)
            arrResults = viewModel.searchPortsAsync(arrQuery)
            arrLoading = false
        } else {
            arrResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voyage Settings", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = { AppMenuActions(navController) },
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
                    Text("Search any port in the world (World Port Index).", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Controlos para abrir/fechar os menus dropdown
                    PortSearchField(
                        label = "Departure Port",
                        query = depQuery,
                        onQueryChange = { depQuery = it; depCoords = null },
                        results = depResults,
                        isSearching = depLoading,
                        countries = availableCountries,
                        onPortSelected = { port ->
                            depQuery = port.name
                            depCoords = Pair(port.lat, port.lon)
                            depResults = emptyList()
                        },
                        onBrowseCountry = { code ->
                            coroutineScope.launch {
                                depLoading = true
                                depResults = viewModel.getPortsByCountryAsync(code)
                                depLoading = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PortSearchField(
                        label = "Arrival Port",
                        query = arrQuery,
                        onQueryChange = { arrQuery = it },
                        results = arrResults,
                        isSearching = arrLoading,
                        countries = availableCountries,
                        onPortSelected = { port ->
                            arrQuery = port.name
                            arrResults = emptyList()
                        },
                        onBrowseCountry = { code ->
                            coroutineScope.launch {
                                arrLoading = true
                                arrResults = viewModel.getPortsByCountryAsync(code)
                                arrLoading = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.updateVoyage(depQuery, arrQuery, depCoords?.first, depCoords?.second)
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