package com.project.quizcafe.domain.quizbooksolving.dto.request

import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import java.time.LocalDateTime

data class CreateQuizBookSolvingRequest(
    val quizBookId: Long,
    val version: Long,
    val level: QuizLevel,
    val category: String,
    val title: String,
    val description: String?,
    val totalQuizzes: Int,
    val correctCount: Int,
    val completedAt: LocalDateTime = LocalDateTime.now(),
    val quizzes: List<CreateQuizSolvingRequest>
)
