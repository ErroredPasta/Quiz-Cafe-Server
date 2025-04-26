package com.project.quizcafe.quiz.dto.request

import com.project.quizcafe.quiz.entity.QuestionType

data class CreateQuizRequest(
    val quizBookId: Long,
    val questionType: QuestionType,
    val content: String,
    val answer: String,
    val explanation: String?
)
