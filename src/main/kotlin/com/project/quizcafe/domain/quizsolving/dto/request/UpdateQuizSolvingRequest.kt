package com.project.quizcafe.domain.quizsolving.dto.request

data class UpdateQuizSolvingRequest(
    val userAnswer: String? = null,
    val isCorrect: Boolean? = null,
    val memo: String? = null
)