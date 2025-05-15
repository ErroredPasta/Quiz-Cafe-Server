package com.project.quizcafe.domain.quizsolving.dto.response

import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quizsolving.dto.request.CreateMcqOptionSolvingRequest
import java.time.LocalDateTime

data class QuizSolvingResponse(
    val id: Long,
    val quizBookSolvingId: Long,
    val quizId: Long,
    val questionType: QuestionType,
    val content: String,
    val answer: String,
    val explanation: String?,
    val memo: String?,
    val userAnswer: String?,
    val isCorrect: Boolean,
    val completedAt: LocalDateTime,
    val mcqOptions: List<McqOptionSolvingResponse>? = null
)
