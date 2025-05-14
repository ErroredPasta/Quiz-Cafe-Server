package com.project.quizcafe.domain.quizbook.dto.response

import com.project.quizcafe.domain.quiz.entity.QuestionType

data class QuizSummary(
    val quizId : Long,
    val quizContent: String,
    val quizType: QuestionType
)
