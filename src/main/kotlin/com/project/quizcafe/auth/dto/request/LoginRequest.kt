package com.project.quizcafe.auth.dto.request

data class LoginRequest(
    val loginEmail: String,
    val password: String,
)
