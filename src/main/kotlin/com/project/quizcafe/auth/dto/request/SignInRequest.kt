package com.project.quizcafe.auth.dto.request

data class SignInRequest(
    val loginEmail: String,
    val password: String,
)
