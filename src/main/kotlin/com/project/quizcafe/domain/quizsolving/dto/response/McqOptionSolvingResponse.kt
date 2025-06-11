package com.project.quizcafe.domain.quizsolving.dto.response

data class McqOptionSolvingResponse(
    val id: Long,
    val quizSolvingId: Long,
    val optionNumber: Int,
    val optionContent: String,
    val isCorrect: Boolean,
)