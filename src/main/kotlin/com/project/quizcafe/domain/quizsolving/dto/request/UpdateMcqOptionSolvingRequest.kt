package com.project.quizcafe.domain.quizsolving.dto.request

data class UpdateMcqOptionSolvingRequest(
    val optionNumber: Int? = null,
    val optionContent: String? = null,
    val isCorrect: Boolean? = null
)