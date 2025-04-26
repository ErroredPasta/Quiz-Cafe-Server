package com.project.quizcafe.quiz.dto.request

data class UpdateMcqOptionRequest(
    val optionContent: String?,
    val isCorrect: Boolean?
)