package com.project.quizcafe.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class VerifyCodeRequest(
    @field:NotBlank(message = "이메일을 입력하세요.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    val toMail: String,

    @field:Size(min = 6, max = 6, message = "코드는 6자리여야 합니다.")
    val code: String
)