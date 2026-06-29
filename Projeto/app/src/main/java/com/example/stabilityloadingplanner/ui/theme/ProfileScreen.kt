package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stabilityloadingplanner.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val isPro       = authViewModel.isPro
    val displayName = authViewModel.displayName
    val email       = authViewModel.email
    val scope       = rememberCoroutineScope()

    var showUpgradeDialog   by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentDone         by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title   = { Text(stringResource(R.string.profile_title), fontWeight = FontWeight.Bold, color = IndustrialPrimary) },
                actions = { AppMenuActions(navController) },
                colors  = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialSurface)
            )
        },
        bottomBar      = { ExactBottomNav(navController, "profile") },
        containerColor = IndustrialBackground
    ) { padding ->
        Column(
            modifier            = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = IndustrialPrimary.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Person, null, modifier = Modifier.size(48.dp), tint = IndustrialPrimary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(email, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(20.dp), color = if (isPro) Color(0xFFFF8F00) else OutlineVariant) {
                Text(
                    if (isPro) "⭐ PRO" else "FREE",
                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color      = if (isPro) Color.White else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = paymentDone, enter = fadeIn(), exit = fadeOut()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.profile_welcome_pro),
                        modifier   = Modifier.padding(16.dp),
                        color      = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Center
                    )
                }
            }

            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = IndustrialSurface),
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.profile_your_plan), fontWeight = FontWeight.Bold, color = IndustrialPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("✔  ${stringResource(R.string.profile_vessel_search)}", style = MaterialTheme.typography.bodyMedium)
                    Text("✔  ${stringResource(R.string.profile_cargo)}",          style = MaterialTheme.typography.bodyMedium)
                    Text("✔  ${stringResource(R.string.profile_stability_feature)}", style = MaterialTheme.typography.bodyMedium)
                    Text("✔  ${stringResource(R.string.profile_marine_feature)}", style = MaterialTheme.typography.bodyMedium)
                    if (isPro) {
                        Text("✔  ${stringResource(R.string.profile_pdf)}", style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFF8F00), fontWeight = FontWeight.Bold)
                    } else {
                        Text("✗  ${stringResource(R.string.profile_pdf)}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick  = { showUpgradeDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00))
                        ) { Text(stringResource(R.string.profile_upgrade_btn), fontWeight = FontWeight.Bold) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick  = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border   = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) { Text(stringResource(R.string.profile_sign_out), fontWeight = FontWeight.Bold) }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingPayment) showUpgradeDialog = false },
            title = { Text(stringResource(R.string.profile_upgrade_title), fontWeight = FontWeight.Bold, color = Color(0xFFFF8F00)) },
            text  = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isProcessingPayment) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(color = Color(0xFFFF8F00), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(R.string.profile_processing), textAlign = TextAlign.Center, color = TextSecondary)
                    } else {
                        Text(stringResource(R.string.profile_upgrade_text))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.profile_upgrade_details), style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(stringResource(R.string.profile_payment_details), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Card:   •••• •••• •••• 4242", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Amount: €1.00 / month",       style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!isProcessingPayment) {
                    Button(
                        onClick = {
                            isProcessingPayment = true
                            scope.launch {
                                delay(2000)
                                authViewModel.upgradeToPro {
                                    isProcessingPayment = false
                                    paymentDone         = true
                                    showUpgradeDialog   = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00))
                    ) { Text(stringResource(R.string.profile_pay_btn), fontWeight = FontWeight.Bold) }
                }
            },
            dismissButton = {
                if (!isProcessingPayment) {
                    TextButton(onClick = { showUpgradeDialog = false }) {
                        Text(stringResource(R.string.profile_cancel), color = TextSecondary)
                    }
                }
            },
            containerColor = IndustrialSurface
        )
    }
}