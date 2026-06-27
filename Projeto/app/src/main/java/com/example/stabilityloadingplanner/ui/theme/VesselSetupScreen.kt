package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VesselSetupScreen(navController: NavController, viewModel: VesselViewModel) {
    var imoNumber by remember { mutableStateOf("") }
    val searchResult   = viewModel.searchResult
    val isSearching    = viewModel.isSearchingVessel
    val searchError    = viewModel.vesselSearchError
    val hasEstimates   = viewModel.searchHasEstimates

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vessel Setup", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = { AppMenuActions(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "setup") },
        containerColor = IndustrialBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("vessel_registration") },
                containerColor = IndustrialPrimary,
                contentColor = Color.White,
                text = { Text("Register Manually") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Find Vessel in Database", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Enter the 7-digit IMO number.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = imoNumber,
                        onValueChange = { imoNumber = it },
                        label = { Text("IMO Number") },
                        leadingIcon = { Icon(Icons.Outlined.DirectionsBoat, contentDescription = null, tint = IndustrialPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary, focusedLabelColor = IndustrialPrimary, cursorColor = IndustrialPrimary)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.searchVesselByImo(imoNumber) },
                        enabled = !isSearching && imoNumber.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Searching...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Search Vessel", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (searchError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(searchError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // AnimatedVisibility — o card do navio aparece com slide + fade
            AnimatedVisibility(
                visible = searchResult != null,
                enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit    = fadeOut() + slideOutVertically()
            ) {
                searchResult?.let { result ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Vessel Found!", fontWeight = FontWeight.Bold, color = IndustrialPrimary, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Foto via Wikimedia Commons / Wikipedia
                            val photoUrl = viewModel.vesselPhotoUrl
                            if (viewModel.isLoadingPhoto) {
                                Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerLow), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = IndustrialPrimary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            } else if (photoUrl != null) {
                                val context = LocalContext.current
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(context).data(photoUrl).crossfade(true).build(),
                                    contentDescription = "Photo of ${result.name}",
                                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        Box(modifier = Modifier.fillMaxSize().background(SurfaceContainerLow), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = IndustrialPrimary)
                                        }
                                    },
                                    error = {
                                        Box(modifier = Modifier.fillMaxSize().background(SurfaceContainerLow), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.BrokenImage, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                                                Text("Photo unavailable", fontSize = 12.sp, color = TextSecondary)
                                            }
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text("Name: ${result.name}", fontWeight = FontWeight.Bold)
                            Text("IMO: ${result.imo}")
                            if (result.deadweight > 0) Text("Deadweight: ${result.deadweight.toInt()} t")
                            if (result.loa  > 0) Text("LOA: ${"%.1f".format(result.loa)} m")
                            if (result.beam > 0) Text("Beam: ${"%.1f".format(result.beam)} m")
                            if (result.numberOfHolds > 0) Text("Est. Holds: ${result.numberOfHolds}")

                            if (hasEstimates) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                    Text(
                                        "Technical dimensions estimated from vessel type. Edit after loading if needed.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.selectVessel(result)
                                    navController.navigate("cargo_plan")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                            ) { Text("Use This Vessel", fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
    }
}