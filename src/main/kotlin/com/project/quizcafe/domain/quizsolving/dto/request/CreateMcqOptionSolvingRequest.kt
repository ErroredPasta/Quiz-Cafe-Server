package com.project.quizcafe.domain.quizsolving.dto.request

data class CreateMcqOptionSolvingRequest(
    val optionNumber: Int,
    val optionContent: String,
    val isCorrect: Boolean
)