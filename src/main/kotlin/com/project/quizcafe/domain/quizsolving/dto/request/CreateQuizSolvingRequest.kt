package com.project.quizcafe.domain.quizsolving.dto.request

import java.time.LocalDateTime

data class CreateQuizSolvingRequest(
    val quizId: Long,
    val memo: String?,
    val userAnswer: String?,
    val isCorrect: Boolean,
    val completedAt: LocalDateTime = LocalDateTime.now(),
)
