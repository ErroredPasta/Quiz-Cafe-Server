package com.project.quizcafe.domain.quiz.dto.request

data class UpdateMcqOptionRequest(
    val optionContent: String?,
    val isCorrect: Boolean?
)