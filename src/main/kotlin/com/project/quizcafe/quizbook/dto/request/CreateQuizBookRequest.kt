package com.project.quizcafe.quizbook.dto.request

data class CreateQuizBookRequest (
    val category : String,
    val title : String,
    val description : String
)