package com.example.stabilityloadingplanner.data.models

data class UserRecord(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val isPro: Boolean = false
)