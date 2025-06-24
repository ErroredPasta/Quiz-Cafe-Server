package com.project.quizcafe.domain.auth.dto.response

import com.nimbusds.oauth2.sdk.token.RefreshToken

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)