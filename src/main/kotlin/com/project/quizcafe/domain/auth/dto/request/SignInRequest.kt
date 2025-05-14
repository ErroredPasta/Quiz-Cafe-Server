package com.project.quizcafe.domain.auth.dto.request

data class SignInRequest(
    val loginEmail: String,
    val password: String,
)
