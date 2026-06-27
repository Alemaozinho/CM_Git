package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val user = authViewModel.currentUser
    var showUpgradeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = { AppMenuActions(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar = { ExactBottomNav(navController, "profile") },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar
            Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = IndustrialPrimary) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(user?.name ?: "User", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(user?.email ?: "", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Badge PRO ou FREE
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (authViewModel.isPro) ActiveOrange else OutlineVariant
            ) {
                Text(
                    text = if (authViewModel.isPro) "● PRO" else "FREE",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = if (authViewModel.isPro) OnActiveOrange else TextSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card de upgrade — só aparece para contas free
            if (!authViewModel.isPro) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Upgrade to PRO", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Export PDF stability reports", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("• Full voyage history", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("• Priority support", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showUpgradeDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ActiveOrange)
                        ) {
                            Text("Upgrade — 1€ / month", fontWeight = FontWeight.Bold, color = OnActiveOrange)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sign Out", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = { Text("Upgrade to PRO", fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
            text = { Text("For 1€/month, unlock PDF export and full voyage history. Payment integration coming soon — tap Confirm to activate PRO for testing.") },
            confirmButton = {
                Button(
                    onClick = { authViewModel.upgradeToPro(); showUpgradeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ActiveOrange)
                ) {
                    Text("Confirm", color = OnActiveOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpgradeDialog = false }) { Text("Cancel", color = IndustrialPrimary) }
            },
            containerColor = IndustrialSurface
        )
    }
}