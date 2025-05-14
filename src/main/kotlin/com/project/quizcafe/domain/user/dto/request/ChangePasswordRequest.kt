package com.project.quizcafe.domain.user.dto.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)