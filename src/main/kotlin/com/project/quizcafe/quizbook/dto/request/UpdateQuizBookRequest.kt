package com.project.quizcafe.quizbook.dto.request

data class UpdateQuizBookRequest(
    val category: String? = null,
    val title: String? = null,
    val description: String? = null
)