package com.project.quizcafe.quizbook.dto.response

import com.project.quizcafe.quizbook.entity.QuizBook

data class GetQuizBookResponse(
    val id: Long,
    val version: Long, // 🔹 버전 필드 추가
    val category: String,
    val title: String,
    val description: String
) {
    companion object {
        fun from(entity: QuizBook): GetQuizBookResponse =
            GetQuizBookResponse(
                id = entity.id,
                version = entity.version, // 🔹 매핑
                category = entity.category,
                title = entity.title,
                description = entity.description
            )
    }
}
