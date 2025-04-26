package com.project.quizcafe.quiz.dto.request

import com.project.quizcafe.quiz.entity.QuestionType

data class UpdateQuizRequest(
    val content: String? = null,
    val answer: String? = null,
    val explanation: String? = null
)