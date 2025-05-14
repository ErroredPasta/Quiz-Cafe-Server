package com.project.quizcafe.domain.quizsolving.dto.response

import com.project.quizcafe.domain.quizsolving.dto.request.CreateMcqOptionSolvingRequest

data class McqOptionSolvingResponse(
    val id: Long,
    val quizSolvingId: Long,
    val optionNumber: Int,
    val optionContent: String,
    val isCorrect: Boolean,
)