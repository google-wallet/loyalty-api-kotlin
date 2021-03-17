package com.example.loyaltyapidemo.ui.signup

/**
 * Sign up result : success (user details) or error message.
 */
data class SignUpResult(
        val success: SignedUpUserView? = null,
        val error: Int? = null
)