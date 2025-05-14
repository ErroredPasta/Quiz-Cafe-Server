package com.project.quizcafe.domain.quizbook.dto.request

import com.project.quizcafe.domain.quizbook.entity.QuizLevel

data class UpdateQuizBookRequest(
    val category: String? = null,
    val title: String? = null,
    val description: String? = null,
    val level: QuizLevel? = null
)