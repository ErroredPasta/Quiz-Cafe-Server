package com.project.quizcafe.domain.quizsolving.dto.request

data class CreateSingleMcqOptionSolvingRequest (
    val quizSolvingId: Long,
    val optionNumber: Int,
    val optionContent: String,
    val isCorrect: Boolean
)