package com.project.quizcafe.domain.quizbook.dto.response

import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import java.time.LocalDateTime

data class GetQuizBookAndQuizSummaryResponse (
    val id: Long,
    val version: Long,
    val category: String,
    val title: String,
    val description: String,
    val level: QuizLevel,
    val createdAt: LocalDateTime,//만든날짜
    val totalSaves: Int,//북마크수
    val averageCorrectCount: Double,//맞힌개수
    val views: Int,//나중에 추가 조회수
    val ownerId: Long?=null,//만든사람id
    val createdBy: String? =null,//만든이
    val quizzes : List<QuizSummary>,
    val isSaved: Boolean = false
)