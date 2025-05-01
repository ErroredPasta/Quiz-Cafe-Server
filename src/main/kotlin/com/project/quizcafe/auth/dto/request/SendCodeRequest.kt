package com.project.quizcafe.auth.dto.request

import com.project.quizcafe.auth.entity.VerificationType
import io.swagger.v3.oas.annotations.Parameter

data class SendCodeRequest(
    val toMail: String,
    val type: VerificationType
)