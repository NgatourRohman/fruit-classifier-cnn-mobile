package com.ngatour.fruitclassifier.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String,
    val expires_in: Int,
    val user: AuthUser? = null
)

@Serializable
data class AuthUser(
    val id: String,
    val email: String
)