package com.project.quizcafe.auth.dto.request

data class SignupRequest(
    val id: Long?,
    val loginEmail: String,
    val password: String,
    val nickName: String
)
