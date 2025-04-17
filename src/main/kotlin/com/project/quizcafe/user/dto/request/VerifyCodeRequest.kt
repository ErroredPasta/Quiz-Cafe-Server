package com.project.quizcafe.user.dto.request

data class VerifyCodeRequest(
    val toMail: String,
    val code: String
)
