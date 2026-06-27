package com.example.stabilityloadingplanner.ui.theme

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.stabilityloadingplanner.data.models.LocalUserDatabase
import com.example.stabilityloadingplanner.data.models.UserRecord

class AuthViewModel : ViewModel() {
    var currentUser by mutableStateOf<UserRecord?>(null)
        private set
    var authError by mutableStateOf<String?>(null)
        private set

    val isLoggedIn: Boolean get() = currentUser != null
    val isPro: Boolean get() = currentUser?.isPro == true

    fun login(email: String, password: String): Boolean {
        authError = null
        val user = LocalUserDatabase.login(email.trim(), password)
        return if (user != null) {
            currentUser = user
            true
        } else {
            authError = "Invalid email or password."
            false
        }
    }

    fun register(name: String, email: String, password: String): Boolean {
        authError = null
        val user = LocalUserDatabase.register(name.trim(), email.trim(), password)
        return if (user != null) {
            currentUser = user
            true
        } else {
            authError = "This email is already registered."
            false
        }
    }

    fun logout() {
        currentUser = null
    }

    // Simula o upgrade para Pro — depois integra Firebase + pagamento real
    fun upgradeToPro() {
        currentUser = currentUser?.copy(isPro = true)
    }

    fun clearError() {
        authError = null
    }
}