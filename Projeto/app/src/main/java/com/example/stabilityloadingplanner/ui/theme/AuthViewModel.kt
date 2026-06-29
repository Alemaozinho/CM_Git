package com.example.stabilityloadingplanner.ui.theme

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    var currentUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set
    var isPro by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set

    val isLoggedIn: Boolean get() = currentUser != null
    val displayName: String get() = currentUser?.displayName
        ?: currentUser?.email?.substringBefore('@')
        ?: "User"
    val email: String get() = currentUser?.email ?: ""

    init {
        auth.addAuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            if (currentUser != null) loadUserPlan()
            else isPro = false
        }
        if (currentUser != null) loadUserPlan()
    }

    private fun loadUserPlan() {
        val uid = currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(uid)
                    .collection("profile").document("data")
                    .get().await()
                isPro = doc.getString("plan") == "pro"
            } catch (e: Exception) {
                Log.e("AUTH", "Error loading plan: ${e.message}")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) { onError("Please fill in all fields"); return }
        viewModelScope.launch {
            isLoading = true
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                currentUser = auth.currentUser
                loadUserPlan()
                onSuccess()
            } catch (e: Exception) {
                onError(when {
                    e.message?.contains("password")  == true -> "Incorrect password"
                    e.message?.contains("no user")   == true -> "User not found"
                    e.message?.contains("network")   == true -> "Network error. Check your connection"
                    else -> "Login failed. Check your credentials"
                })
            } finally {
                isLoading = false
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) { onError("Please fill in all fields"); return }
        if (password.length < 6) { onError("Password must be at least 6 characters"); return }
        viewModelScope.launch {
            isLoading = true
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                currentUser = auth.currentUser

                val profileUpdates = userProfileChangeRequest { displayName = name.trim() }
                auth.currentUser?.updateProfile(profileUpdates)?.await()
                currentUser = auth.currentUser

                val uid = currentUser?.uid ?: return@launch
                db.collection("users").document(uid)
                    .collection("profile").document("data")
                    .set(mapOf(
                        "displayName" to name.trim(),
                        "email"       to email.trim(),
                        "plan"        to "free",
                        "createdAt"   to com.google.firebase.Timestamp.now()
                    )).await()

                isPro = false
                onSuccess()
            } catch (e: Exception) {
                onError(when {
                    e.message?.contains("email")    == true -> "Email already in use"
                    e.message?.contains("password") == true -> "Password too weak (min 6 characters)"
                    e.message?.contains("network")  == true -> "Network error. Check your connection"
                    else -> "Registration failed. Try again"
                })
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
        isPro = false
    }

    fun upgradeToPro(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val uid = currentUser?.uid
                if (uid != null) {
                    // withTimeoutOrNull garante que não fica preso se o Firestore não responder
                    withTimeoutOrNull(5000L) {
                        db.collection("users").document(uid)
                            .collection("profile").document("data")
                            .set(mapOf("plan" to "pro"), SetOptions.merge()).await()
                    }
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Firestore upgrade error: ${e.message}")
            } finally {
                // finally garante que onSuccess() é SEMPRE chamado
                // mesmo que o Firestore falhe — o estado local fica PRO
                isPro = true
                onSuccess()
            }
        }
    }
}
