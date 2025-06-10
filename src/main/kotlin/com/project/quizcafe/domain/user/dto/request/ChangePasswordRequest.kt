package com.project.quizcafe.domain.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordRequest(

    @field:NotBlank(message = "비밀번호를 입력하세요.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자리여야 합니다.")
    val oldPassword: String,

    @field:NotBlank(message = "비밀번호를 입력하세요.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자리여야 합니다.")
    val newPassword: String
)