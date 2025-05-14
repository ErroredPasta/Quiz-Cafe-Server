package com.project.quizcafe.domain.quiz.dto.request

data class UpdateQuizRequest(
    val content: String? = null,
    val answer: String? = null,
    val explanation: String? = null
)