package com.project.quizcafe.quiz.dto.response

import com.project.quizcafe.quiz.entity.QuestionType

data class QuizResponse(
    val id: Long,
    val quizBookId: Long,
    val questionType: QuestionType,
    val content: String,
    val answer: String,
    val explanation: String?,
    val version: Long
)