package com.project.quizcafe.domain.quizbooksolving.dto.response

import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import java.time.LocalDateTime

data class QuizBookSolvingResponse(
    val id: Long,
    val userId: Long,
    val quizBookId: Long,
    val version: Long,
    val level: QuizLevel,
    val category: String,
    val title: String,
    val description: String?,
    val totalQuizzes: Int,
    val correctCount: Int,
    val completedAt: LocalDateTime = LocalDateTime.now(),
    val solvingTime: Long?,
    val quizzes: List<QuizSolvingResponse>
)