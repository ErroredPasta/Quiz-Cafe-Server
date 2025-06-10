package com.project.quizcafe.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignUpRequest(
    //val id: Long?,
    @field:NotBlank(message = "이메일을 입력하세요.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    val loginEmail: String,

    @field:NotBlank(message = "비밀번호를 입력하세요.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자리여야 합니다.")
    val password: String,

    @field:NotBlank(message = "닉네임을 입력하세요.")
    val nickName: String
)
