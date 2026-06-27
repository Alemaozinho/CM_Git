package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = IndustrialPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection(
                step      = "1",
                icon      = Icons.Outlined.DirectionsBoat,
                title     = "Find Your Vessel",
                body      = "Go to Setup and enter the 7-digit IMO number of your vessel. The app will search for it in the global vessel registry and load its technical data automatically. If dimensions are estimated, you can correct them later using the ✎ button in the Cargo Plan."
            )

            HelpSection(
                step      = "2",
                icon      = Icons.Outlined.Inventory2,
                title     = "Plan the Cargo",
                body      = "In the Loading tab, enter the weight of cargo in each hold and select the cargo type. The app shows your total load against the vessel's deadweight capacity and warns you if you exceed it."
            )

            HelpSection(
                step      = "3",
                icon      = Icons.Outlined.AccountBalance,
                title     = "Check Stability",
                body      = "The Stability tab calculates the GM (metacentric height) and KG (centre of gravity) based on your cargo plan. A GM above 0.15 m is considered stable. A green card means the vessel is safe to sail — a red card means you need to adjust the cargo distribution."
            )

            HelpSection(
                step      = "4",
                icon      = Icons.Outlined.Waves,
                title     = "Check Marine Conditions",
                body      = "The Marine tab shows real-time sea conditions at your departure port, including wave height, wave period, and sea temperature from the Open-Meteo Marine API. A warning appears if wave height exceeds 3 metres. Set your voyage ports in Voyage Settings to update the coordinates."
            )

            HelpSection(
                step      = "5",
                icon      = Icons.Outlined.Assessment,
                title     = "Export the Report",
                body      = "PRO accounts can export a full stability and loading report as a PDF saved to your Downloads folder. The report includes the vessel data, cargo plan, stability results, and voyage details. Upgrade to PRO in the Profile tab."
            )

            HelpSection(
                step      = "6",
                icon      = Icons.Outlined.Person,
                title     = "Your Profile",
                body      = "The Profile tab shows your account details and plan. Free accounts have access to all core features. PRO accounts unlock PDF export. Tap Upgrade to activate PRO for 1€/month."
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Test Accounts", fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Free:  free@test.com  /  free123", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("Pro:    pro@test.com  /  pro123",  style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HelpSection(step: String, icon: ImageVector, title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = IndustrialSurface),
        shape    = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Número do passo
            Surface(shape = RoundedCornerShape(8.dp), color = IndustrialPrimary, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(step, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(icon, contentDescription = null, tint = IndustrialPrimary, modifier = Modifier.size(16.dp))
                    Text(title, fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(body, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}