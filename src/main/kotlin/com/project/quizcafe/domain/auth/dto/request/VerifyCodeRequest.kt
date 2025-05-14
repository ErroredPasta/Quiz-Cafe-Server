package com.project.quizcafe.domain.auth.dto.request

data class VerifyCodeRequest(
    val toMail: String,
    val code: String
)