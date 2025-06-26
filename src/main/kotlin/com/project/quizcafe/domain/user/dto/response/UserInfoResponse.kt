package com.project.quizcafe.domain.user.dto.response

import java.time.LocalDateTime

data class UserInfoResponse(
    val nickname: String,
    val email: String,
    val createdAt: LocalDateTime?
)