package com.example.stabilityloadingplanner.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VesselSetupScreen(navController: NavController, viewModel: VesselViewModel) {
    var imoNumber by remember { mutableStateOf("") }
    val searchResult = viewModel.searchResult
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vessel Setup", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
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
                text = { Text("Register Manual") },
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
                        onClick = {
                            viewModel.searchVesselByImo(imoNumber)
                            if (viewModel.searchResult == null) Toast.makeText(context, "Navio não encontrado", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search Vessel", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (searchResult != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Vessel Found!", fontWeight = FontWeight.Bold, color = IndustrialPrimary, style = MaterialTheme.typography.titleMedium)
                        Text("Name: ${searchResult.name}")
                        Text("IMO: ${searchResult.imo}")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.selectVessel(searchResult)
                                navController.navigate("cargo_plan")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
                        ) { Text("Use this Vessel", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}