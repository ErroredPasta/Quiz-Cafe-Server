package com.project.quizcafe.auth.dto

data class TokenInfo(
    val grantType: String,
    val accessToken: String
)