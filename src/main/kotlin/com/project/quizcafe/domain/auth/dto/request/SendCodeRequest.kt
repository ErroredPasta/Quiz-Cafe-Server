package com.project.quizcafe.domain.auth.dto.request

import com.project.quizcafe.domain.auth.entity.VerificationType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendCodeRequest(

    @field:NotBlank(message = "이메일을 입력하세요.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    val toMail: String,
    val type: VerificationType
)