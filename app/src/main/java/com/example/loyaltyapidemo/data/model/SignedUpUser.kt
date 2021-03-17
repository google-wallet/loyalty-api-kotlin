package com.example.loyaltyapidemo.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class SignedUpUser(
        val name: String,
        val email: String
)