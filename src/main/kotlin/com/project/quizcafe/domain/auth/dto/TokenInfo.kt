package com.project.quizcafe.domain.auth.dto

data class TokenInfo(
    val grantType: String,
    val accessToken: String
)