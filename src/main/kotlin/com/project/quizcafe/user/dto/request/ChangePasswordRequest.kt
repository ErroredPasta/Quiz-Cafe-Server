package com.project.quizcafe.user.dto.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)