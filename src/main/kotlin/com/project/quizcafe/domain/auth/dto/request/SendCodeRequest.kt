package com.project.quizcafe.domain.auth.dto.request

import com.project.quizcafe.domain.auth.entity.VerificationType

data class SendCodeRequest(
    val toMail: String,
    val type: VerificationType
)