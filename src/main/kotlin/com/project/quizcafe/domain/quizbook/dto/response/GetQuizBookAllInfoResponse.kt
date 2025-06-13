package com.project.quizcafe.domain.quizbook.dto.response

import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import java.time.LocalDateTime

data class GetQuizBookAllInfoResponse(
    val id: Long,
    val version: Long, // 🔹 버전 필드 추가
    val category: String,
    val title: String,
    val description: String,
    val level: QuizLevel,
    val createdBy: String? =null,
    val totalQuizzes: Int,
    val createdAt: LocalDateTime,
    val quizzes : List<QuizResponse>
)