package com.project.quizcafe.domain.quizbook.dto.response

import com.project.quizcafe.domain.quizbook.entity.QuizLevel

data class GetQuizBookAndQuizSummaryResponse (
    val id: Long,
    val version: Long, // 🔹 버전 필드 추가
    val category: String,
    val title: String,
    val description: String,
    val level: QuizLevel,
    val createdBy: String? =null,
    val quizzes : List<QuizSummary>
)