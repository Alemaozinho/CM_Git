package com.example.stabilityloadingplanner.data.models

object LocalUserDatabase {

    // Contas de teste para demonstração — uma free, uma pro
    private val users = mutableListOf(
        UserRecord(id = "1", name = "Free User",  email = "free@test.com", password = "free123", isPro = false),
        UserRecord(id = "2", name = "Pro User",   email = "pro@test.com",  password = "pro123",  isPro = true)
    )

    fun login(email: String, password: String): UserRecord? =
        users.find { it.email.equals(email, ignoreCase = true) && it.password == password }

    fun register(name: String, email: String, password: String): UserRecord? {
        // Não deixa registar o mesmo email duas vezes
        if (users.any { it.email.equals(email, ignoreCase = true) }) return null
        val newUser = UserRecord(
            id = System.currentTimeMillis().toString(),
            name = name,
            email = email,
            password = password,
            isPro = false
        )
        users.add(newUser)
        return newUser
    }
}