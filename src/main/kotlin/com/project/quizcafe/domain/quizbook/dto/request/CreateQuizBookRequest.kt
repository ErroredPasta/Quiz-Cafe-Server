package com.project.quizcafe.domain.quizbook.dto.request

import com.project.quizcafe.domain.quizbook.entity.QuizLevel

data class CreateQuizBookRequest (
    val category : String,
    val title : String,
    val description : String,
    val level: QuizLevel
)