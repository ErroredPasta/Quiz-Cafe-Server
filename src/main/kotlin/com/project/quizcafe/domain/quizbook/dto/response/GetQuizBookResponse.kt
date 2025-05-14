package com.project.quizcafe.domain.quizbook.dto.response

import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.entity.QuizLevel

data class GetQuizBookResponse(
    val id: Long,
    val version: Long, // 🔹 버전 필드 추가
    val category: String,
    val title: String,
    val description: String,
    val level: QuizLevel
) {
    companion object {
        fun from(entity: QuizBook): GetQuizBookResponse =
            GetQuizBookResponse(
                id = entity.id,
                version = entity.version, // 🔹 매핑
                category = entity.category,
                title = entity.title,
                description = entity.description,
                level = entity.level
            )
    }
}
