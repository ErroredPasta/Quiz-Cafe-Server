package com.project.quizcafe.domain.quiz.dto.response

data class McqOptionResponse(
    val id: Long,
    val quizId: Long,
    val optionNumber: Int,
    val optionContent: String,
    val isCorrect: Boolean
)