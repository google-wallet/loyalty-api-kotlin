package com.example.loyaltyapidemo.ui.signup

/**
 * User details post sign up that is exposed to the UI
 */
data class SignedUpUserView(
        val name: String,
        val jwt: String
)