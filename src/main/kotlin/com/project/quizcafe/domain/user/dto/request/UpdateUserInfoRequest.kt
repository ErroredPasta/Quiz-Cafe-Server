package com.project.quizcafe.domain.user.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdateUserInfoRequest(
    @field:NotBlank(message = "닉네임을 입력하세요.")
    val nickname: String
)