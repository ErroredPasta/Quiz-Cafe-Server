package com.project.quizcafe.domain.quizbooksolving.dto.request

import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import java.time.LocalDateTime

data class CreateQuizBookSolvingRequest(
    val quizBookId: Long,
    val version: Long,
    val totalQuizzes: Int,
    val correctCount: Int,
    val completedAt: LocalDateTime = LocalDateTime.now(),
    val solvingTime: Long?,
    val quizzes: List<CreateQuizSolvingRequest>
)
