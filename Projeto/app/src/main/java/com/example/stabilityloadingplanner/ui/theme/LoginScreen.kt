package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg        by remember { mutableStateOf<String?>(null) }
    val isLoading = authViewModel.isLoading

    Scaffold(containerColor = IndustrialBackground) { padding ->
        Column(
            modifier            = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Icon(
                imageVector        = Icons.Default.DirectionsBoat,
                contentDescription = null,
                tint               = IndustrialPrimary,
                modifier           = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("StabilityPlanner", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = IndustrialPrimary)
            Text("Maritime Cargo Management", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value           = email,
                onValueChange   = { email = it; errorMsg = null },
                label           = { Text("Email") },
                leadingIcon     = { Icon(Icons.Default.MailOutline, null, tint = IndustrialPrimary) },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors          = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value                = password,
                onValueChange        = { password = it; errorMsg = null },
                label                = { Text("Password") },
                leadingIcon          = { Icon(Icons.Default.Lock, null, tint = IndustrialPrimary) },
                trailingIcon         = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector        = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint               = TextSecondary
                        )
                    }
                },
                modifier             = Modifier.fillMaxWidth(),
                singleLine           = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors               = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndustrialPrimary)
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    errorMsg!!,
                    color     = MaterialTheme.colorScheme.error,
                    style     = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick  = {
                    authViewModel.login(
                        email     = email,
                        password  = password,
                        onSuccess = { },
                        onError   = { msg -> errorMsg = msg }
                    )
                },
                enabled  = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = IndustrialPrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("Don't have an account? ", color = TextSecondary)
                Text("Create one", color = IndustrialPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}