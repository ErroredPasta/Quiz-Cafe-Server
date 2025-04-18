package com.project.quizcafe.auth.dto.request

data class VerifyCodeRequest(
    val toMail: String,
    val code: String
)